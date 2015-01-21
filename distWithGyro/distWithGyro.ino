#include <kalman_encoder.h>
#include <gyroscope.h>
#include "Wire.h"
#include <kalman_gyro.h>
#include <ping_sensor.h>
#include <PID_v1.h>
#include <Servo.h> 
#include<TimerThree.h>
#include<Encoder.h>


#define PING_SENSOR_FRONT_PIN 6
#define PING_SENSOR_BACK_PIN 7

#define MOTOR_PIN 11
#define BACK_SERVO_PIN 8
#define FRONT_SERVO_PIN 9

#define MOTOR_FORWARD 80
#define MOTOR_BACKWARD 110
#define MOTOR_STOP 95

#define TIMER_INTERUPT_PERIOD 100000
#define WHEEL_CIRCUMFRENCE_CM 48.1
#define DIST_CM_BETWEEN_PINGS 15.24
#define TARGET_DISTANCE_CM 304.8
#define M_PI 3.1415926

#define LEFT_ENCODER_INT_PIN 18
#define LEFT_ENCODER_PIN 17

#define RIGHT_ENCODER_INT_PIN 19
#define RIGHT_ENCODER_PIN 20

#define GYROSCOPE_SENSITIVITY 65.536

//Kalman Filter Constants
#define VELOCITY_NOISE 0.15
#define POSITION_NOISE 0.15
#define ENCODER_NOISE 0.1

Servo motorServo;
Servo frontServo;
Servo backServo;

// PID variables
double setDistance, currDistance, pidOutput;
double Kp = 4;
double Ki = 0.2;
double Kd = 0.2;

PID myPID(&currDistance,&pidOutput,&setDistance,Kp,Ki,Kd,DIRECT);
PingSensor distSensorFront(PING_SENSOR_FRONT_PIN);
PingSensor distSensorBack(PING_SENSOR_BACK_PIN);
float centerLineAngle;
boolean centerAngleComputed = false;

// gyro variables
Gyroscope gyro(220,76,-85,1788);
KalmanFilterGyro gyroFilter;
volatile float currGyroYaw = 0;
volatile float currGyroRate = 0;

// encoder constants
const float gearRatio = 64*16;            //  This is the gear ratio used to count number of revolutions in the wheel
const int clockFreq = 10;     
const float timeInc = 1.0f/clockFreq;

// Encoder objects
Encoder rightEncoder(RIGHT_ENCODER_INT_PIN,RIGHT_ENCODER_PIN);
Encoder leftEncoder(LEFT_ENCODER_INT_PIN,LEFT_ENCODER_PIN);
long currRightWheelPos = 0;
long currLeftWheelPos = 0;
long lastRightWheelPos = 0;
long lastLeftWheelPos = 0;

volatile float distanceTraveledInCm = 0;
boolean target_distance_reached = false;

//Kalman Filter Encoder
KalmanFilterEncoder encoderFilter(POSITION_NOISE,VELOCITY_NOISE,ENCODER_NOISE);

void setup(){
   Serial.begin(9600);
   // set up servos 
   frontServo.attach(FRONT_SERVO_PIN);
   backServo.attach(BACK_SERVO_PIN);
   motorServo.attach(MOTOR_PIN);
   backServo.write(90);
   motorServo.write(MOTOR_FORWARD);
   
   //inititalize wheels
   rightEncoder.write(0);
   leftEncoder.write(0);
   
   // setup PID
   setDistance = 50;   
   myPID.SetMode(AUTOMATIC);
   myPID.SetOutputLimits(-60,60);
   
   
   // setup Gyro and gyro kalman filter
   gyro.setup();
   gyro.calibrateGyro();
   gyroFilter.setAngle(gyro.getYPR(GET_YAW));
   gyroFilter.setRate(gyro.getYawRate());
   
   // start timer
   Timer3.initialize();
   Timer3.attachInterrupt(distTimerInterrupt,TIMER_INTERUPT_PERIOD);
   Timer3.start();
}

void loop(){
  getGyroAngle();
  if(!centerAngleComputed){
      computeCenterAngle();
      centerAngleComputed = true;
  }else{
    if(!target_distance_reached){ 
       controlTurning();
       updateEncoders();
       Serial.print("Distance covered in cm: ");
       Serial.println(distanceTraveledInCm);
    }else{
      motorServo.write(MOTOR_STOP);
    }
  }
}


void getGyroAngle(){
    currGyroYaw = gyro.getYPR(GET_YAW);
    currGyroRate = gyro.getYawRate();
}

void controlTurning(){
    currDistance = distSensorFront.getDistance();
    myPID.Compute();
    int servoAngle = 90 - (int)pidOutput;
    frontServo.write(servoAngle);
}

void updateEncoders(){
   long newRight = rightEncoder.read();
   long newLeft = leftEncoder.read();
  
  if(newRight != currRightWheelPos){
    currRightWheelPos = newRight;
  }
  if(newLeft != currLeftWheelPos){
    currRightWheelPos = newLeft; 
  }
}

void computeCenterAngle(){
   
   float angle_sum = 0.0f;
   int total_readings = 50;
   float front_dist,back_dist,difference,angle;
   
   for(int i = 0; i < total_readings; i++){
     // calculating angle of robot with the wall using two distance sensors on one side
     front_dist = distSensorFront.getDistance();
     back_dist =  distSensorBack.getDistance();
     difference = fabs(back_dist-front_dist);
     angle = atan2(difference, DIST_CM_BETWEEN_PINGS);
     angle = angle * (180/M_PI);
     angle_sum += angle;
   }
   
   angle = angle_sum / total_readings;
   Serial.print("Angle with wall:");
   Serial.println(angle);
   
   if(front_dist >= back_dist){
     centerLineAngle = fixWrapAngle(currGyroYaw + angle);
   }else{
     centerLineAngle = currGyroYaw - angle;
   }
   Serial.print("center line angle:");
   Serial.println(centerLineAngle);
   delay(5000);
}

float fixWrapAngle(float angle){
    return angle > 180 ? angle - 360: angle;
}

void distTimerInterrupt(){
  currGyroYaw = gyroFilter.getAngle(currGyroYaw,currGyroRate,timeInc);
  
  if(!target_distance_reached){
      //compute Distance using the the left and right encoder
      long diff_right = currRightWheelPos - lastRightWheelPos;
      long diff_left = currLeftWheelPos - lastLeftWheelPos;
      // average the distance from the two encoders
      float avg_diff = (float)(diff_right + diff_left) / 2.0;
      
      float num_rotations = avg_diff / gearRatio;
      
      // compute how much distance the wheels traveled
      float abs_dist_traveled = num_rotations * WHEEL_CIRCUMFRENCE_CM;
      float abs_curr_velocity = abs_dist_traveled / timeInc; 
     
      // now compute how much linear distance was covered with
      // respect to the wall
       float angle_with_center = fabs(centerLineAngle - currGyroYaw);
       angle_with_center = angle_with_center > 180 ? 360 - angle_with_center : angle_with_center;
       //convert to radians
       angle_with_center *= (float)M_PI/180;
      
       float linear_dist = abs_dist_traveled*fabs(cos(angle_with_center));
       float linear_velocity = abs_curr_velocity*fabs(cos(angle_with_center));
       
       // measured position based only on encoders
      float measuredPosition = distanceTraveledInCm + linear_dist;
      distanceTraveledInCm += encoderFilter.getFilteredPosition(measuredPosition,linear_velocity,timeInc);
      if(distanceTraveledInCm >= TARGET_DISTANCE_CM){
             target_distance_reached = true;
      }
  }
  
}







