#include "Wire.h"
#include "gyroscope.h"
#include "ping_sensor.h"
#include<TimerThree.h>
#include <Encoder.h>
#include <Servo.h> 

#define M_PI 3.1415926
#define DIST_CM_BETWEEN_PINGS 15.24
#define WHEEL_CIRCUMFRENCE_CM 48.1

#define MOTOR_FORWARD 85
#define MOTOR_BREAK 95

#define PING_PIN_LEFT_FRONT 7
#define PING_PIN_LEFT_BACK 6
#define PING_PING_RIGHT_FRONT 5
#define MOTOR_PIN 11
#define BACK_SERVO_PIN 8
#define FRONT_SERVO_PIN 9
#define LED_PIN 24

#define TARGET_DISTANCE_CM 610
#define INTERUPT_PERIOD 100000

PingSensor distSensorLF(PING_PIN_LEFT_FRONT);
PingSensor distSensorRF(PING_PING_RIGHT_FRONT);
PingSensor distSensorLB(PING_PIN_LEFT_BACK);
Gyroscope gyro(110,83,-85,1788);

// wheel and servos
Encoder rightWheel(3,4);
Servo motorServo;
Servo frontServo;
Servo backServo;

// encoder constants
const float gearRatio = 64*16;            //  This is the gear ratio used to count number of revolutions in the wheel
const int clockFreq = 10;     
const float timeInc = 1.0f/clockFreq;

//variables to keep track of wheel
long currWheelPosition = 0;
long lastWheelPosition = 0;
volatile float distanceTraveledInCm = 0;
float centerLineAngle;               // this is the angle of the center of hallway, every time robot turns this will be corrected
volatile boolean target_distance_reached = false;
volatile float gyro_yaw = 0;

//PID control variables
const float Kp = 3.0f;
const float propThreshold = 50;
const float minError = 6;

boolean centerAngleComputed = false;

float computeLinearDistanceTraveled();

void setup(){
  Serial.begin(9600);
  
  // initialize wheel to start counting from 0
  rightWheel.write(0);
  // assign pins to servo motors
  motorServo.attach(MOTOR_PIN);
  frontServo.attach(BACK_SERVO_PIN);
  backServo.attach(FRONT_SERVO_PIN);
  
   // set back Servo to a fixed angle of 90 degrees
  backServo.write(90);
  frontServo.write(90);
  
  
  //setup LED
  pinMode(LED_PIN,OUTPUT);
  digitalWrite(LED_PIN,LOW);
  
  
  //setup up the gyro and calibrate it
  gyro.setup();
  gyro.calibrateGyro();  
  gyro_yaw = gyro.getYPR(GET_YAW);
  
  //compute the center line angle relative to current yaw
  computeCenterAngle();
  
  // setup up the the Timer and Interrupt
  Timer3.initialize();
  Timer3.attachInterrupt(distTimerInterrupt,INTERUPT_PERIOD);
  Timer3.start();
  
}

void loop(){
   gyro_yaw = gyro.getYPR(GET_YAW);
  if(!centerAngleComputed){
    computeCenterAngle();
  }else{
    if(!target_distance_reached){
      // update gyro reading, can't do this in iterrupt routine
      controlTurning();
      updateEncoder();
      motorServo.write(MOTOR_FORWARD);
      Serial.println(distanceTraveledInCm);
    }else{
       motorServo.write(MOTOR_BREAK);
       digitalWrite(LED_PIN,HIGH);
    }
  }
}

void updateEncoder(){
    long newWheelPos = rightWheel.read();
    if(newWheelPos != currWheelPosition){
       currWheelPosition = newWheelPos; 
    }
}

// constrols the angle of servos using Proportional controller
// TRY OUT PID to see if better control can be obtained
void controlTurning(){
   float propOutput = computeProportional();
   int servo_angle = 90;
   if(fabs(propOutput) > minError){
      servo_angle -= (int)propOutput; 
   }
   backServo.write(servo_angle);
}

float computeProportional(){
  float distanceRight = distSensorRF.getDistance();
  float distanceLeft  = distSensorLF.getDistance();
  float error = distanceRight - distanceLeft;
  float angle_output = Kp * error;
  if(fabs(angle_output) > propThreshold){
    if(angle_output < 0){
     return -propThreshold; 
    }
    else{
     return propThreshold; 
    }
  }
  return angle_output;
}

void  computeCenterAngle(){
   centerAngleComputed = true;
   // calculating angle of robot with the wall using two distance sensors on one side
   float front_dist = distSensorLF.getDistance();
   float back_dist =  distSensorLB.getDistance();
   float difference = fabs(back_dist-front_dist);
   
   float angle = atan2(difference, 15.24f);
   angle = angle * (180/M_PI);
   Serial.print("Angle with wall:");
   Serial.println(angle);
   
  
   Serial.print("Gyro Yaw:");
   Serial.println(gyro_yaw);
  
   
   // gyro increases going clockwise 
   
   // turn counter clockwise
   if(front_dist >= back_dist){
     centerLineAngle = fixWrapAngle(gyro_yaw + angle);
   }else{
     centerLineAngle = gyro_yaw - angle;
   }
   Serial.print("center line angle:");
   Serial.println(centerLineAngle);
   
  
}


// Timer Interrupt
void distTimerInterrupt(){
   // calculate difference in rotations since last time
   if(centerAngleComputed){
     long diff_position = currWheelPosition - lastWheelPosition;
     lastWheelPosition = currWheelPosition;
      
      // compute number of rotations done by wheel
      float num_rotations = ((float)diff_position) / gearRatio;
       
      // this is the distance traveled by the wheels
      float abs_dist_traveled = num_rotations * WHEEL_CIRCUMFRENCE_CM;
      
      // now compute how much linear distance was covered
      float angle_with_center = fabs(centerLineAngle - gyro_yaw);
      
      angle_with_center = angle_with_center > 180 ? 360 - angle_with_center : angle_with_center;
      
      // negated distance because encoder values are negative when robot
      // is moving forward
      float linear_dist = abs_dist_traveled*fabs(cos(angle_with_center));
      distanceTraveledInCm += linear_dist;
     
      if(distanceTraveledInCm >= TARGET_DISTANCE_CM){
         target_distance_reached = true;
      }
   }
}

float fixWrapAngle(float angle){
    return angle > 180 ? angle - 360: angle;
}




