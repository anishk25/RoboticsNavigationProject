#include "Wire.h"
#include "gyroscope.h"
#include "ping_sensor.h"
#include "timer_interrupt.h"
#include <Encoder.h>
#include <SoftwareServo.h> 

#define M_PI 3.1415926
#define DIST_CM_BETWEEN_PINGS 15.24
#define WHEEL_CIRCUMFRENCE_CM 48.1

#define MOTOR_FORWARD 80
#define MOTOR_BREAK 95

#define PING_PIN_LEFT_FRONT 7
#define PING_PIN_LEFT_BACK 6
#define PING_PING_RIGHT_FRONT 5
#define MOTOR_PIN 11
#define BACK_SERVO_PIN 8
#define FRONT_SERVO_PIN 9

#define TARGET_DISTANCE_CM 2000

PingSensor distSensorLF(PING_PIN_LEFT_FRONT);
PingSensor distSensorRF(PING_PING_RIGHT_FRONT);
PingSensor distSensorLB(PING_PIN_LEFT_BACK);
Gyroscope gyro(220,76,-85,1788);

// wheel and servos
Encoder rightWheel(2,4);
SoftwareServo motorServo;
SoftwareServo frontServo;
SoftwareServo backServo;

// encoder constants
const float gearRatio = 64*16;            //  This is the gear ratio used to count number of revolutions in the wheel
const int clockFreq = 10;     
const float timeInc = 1.0f/clockFreq;

//variables to keep track of wheel
long currWheelPosition = -999;
long lastWheelPosition = 0;
float distanceTraveledInCm = 0;
float centerLineAngle;               // this is the angle of the center of hallway, every time robot turns this will be corrected
boolean target_distance_reached = false;

//PID control variables
const float Kp = 3.0f;
const float propThreshold = 60;
const float minError = 6;


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
  
  //setup up the gyro and calibrate it
  gyro.setup();
  gyro.calibrateGyro();  
 
  // compute the center line angle relative to current yaw
  computeCenterAngle();
  
  //start propelling the robot forward
  motorServo.write(MOTOR_FORWARD);
  
  // setup up the 10HZ clock
  setupTimer(clockFreq,256);
}

void loop(){
  if(!target_distance_reached){
    controlTurning();
    updateEncoder();
  }else{
     motorServo.write(MOTOR_BREAK);
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
   if(abs(propOutput) > minError){
      servo_angle += (int)propOutput; 
   }
   frontServo.write(servo_angle);
}

float computeProportional(){
  float distanceRight = distSensorRF.getDistance();
  float distanceLeft  = distSensorLF.getDistance();
  float error = distanceRight - distanceLeft;
  float angle_output = Kp * error;
  if(abs(angle_output) > propThreshold){
     return -propThreshold; 
  }else{
     return propThreshold; 
  }
  return angle_output;
}

void  computeCenterAngle(){
   // calculating angle of robot with the wall using two distance sensors on one side
   float front_dist = distSensorLF.getDistance();
   float back_dist =  distSensorLB.getDistance();
   float difference = abs(back_dist-front_dist);
   float angle = atan2(difference,DIST_CM_BETWEEN_PINGS);
   angle = angle * (180/M_PI);
   
   float gyro_yaw = gyro.getYPR(GET_YAW);
   
   // assume angle of gyro increases going counter_clockwise(CHECK THIS!!)
   //robot tilted to the right, rotate angle counter clockwise
   if(front_dist >= back_dist){
     centerLineAngle = gyro_yaw + angle;
   }else{
    //robot tilted to the left, rotate angle clockwise
     centerLineAngle = gyro_yaw - angle;
   }
}

// this function will compute the linear
// distance traveled by the robot in 
// the the hallway, this function will be 
// called every 100 ms by the Timer Interrupt
float computeLinearDistanceTraveled(){
    long diff_position = currWheelPosition - lastWheelPosition;
    lastWheelPosition = currWheelPosition;
    
    // compute number of rotations done by wheel
    float num_rotations = diff_position / gearRatio;
    
    // this is the distance traveled by the wheels
    float abs_dist_traveled = num_rotations * WHEEL_CIRCUMFRENCE_CM;
    
    // now compute how much linear distance was covered
    float angle_with_center = abs(centerLineAngle - gyro.getYPR(GET_YAW));
    float linear_dist = abs_dist_traveled*cos(angle_with_center);
    return linear_dist;
}

// Timer Interrupt
ISR(TIMER1_COMPA_vect){
  distanceTraveledInCm += computeLinearDistanceTraveled();
  if(distanceTraveledInCm >= TARGET_DISTANCE_CM){
     target_distance_reached;
  } 
}



