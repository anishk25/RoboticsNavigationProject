
#include <PID_v1.h>
#include <Servo.h>
#include <ping_sensor.h>
#include <Encoder.h>
#include <SoftwareSerial.h>

#define RIGHT_WHEEL_PIN1 2
#define RIGHT_WHEEL_PIN2 4
#define LEFT_WHEEL_PIN1 3
#define LEFT_WHEEL_PIN2 5


#define DIST_BETWEEN_WHEELS_CM 25.05
#define WHEEL_CIRCUMFRENCE_CM 48.1
#define TWOPI PI*2

#define MOTOR_FORWARD 81
#define MOTOR_BREAK 95
#define MOTOR_BACKWARD 107

#define BACK_SERVO_PIN 9
#define FRONT_SERVO_PIN 8
#define MOTOR_PIN 26


#define PING_PIN_FRONT_LEFT 12
#define PING_PIN_FRONT_RIGHT 24
#define PING_PIN_BACK_LEFT 7
#define PING_PIN_BACK_RIGHT 6


#define LED_PIN 13

// Android communication constants
#define ANDROID_MESSAGE_LENGTH 3
#define STOP_CODE       100
#define START_CODE      101
#define FORWARD_CODE    102
#define BACKWARD_CODE   103
#define TURN_ROBOT_CODE 104
#define RX_PIN 10
#define TX_PIN 11

// state constants
#define NAVIGATION_STATE  0     // robot is navigating in hallway
#define TURNING_STATE     1     // robot is turning in hallway corner/intersection
#define REST_STATE        2     // robot is standing still with all sensors off  


int curr_robot_state;

char incomingAndroidMsg[ANDROID_MESSAGE_LENGTH];

const float gearRatio = 64*16;
const float cm_per_tick = WHEEL_CIRCUMFRENCE_CM/gearRatio;

Servo frontServo, backServo, motorServo;
double setDistDiff, distDiffFront, servoAngleOutFront;
double distDiffBack, servoAngleOutBack;

int motorDirection = MOTOR_BREAK;
int ledBrightness = 30;

PingSensor distSensorFL(PING_PIN_FRONT_LEFT);
PingSensor distSensorFR(PING_PIN_FRONT_RIGHT);
PingSensor distSensorBL(PING_PIN_BACK_LEFT);
PingSensor distSensorBR(PING_PIN_BACK_RIGHT);

Encoder rightWheel(RIGHT_WHEEL_PIN1,RIGHT_WHEEL_PIN2);
Encoder leftWheel(LEFT_WHEEL_PIN1,LEFT_WHEEL_PIN2);
long currEncPosRight = 0;
long currEncPosLeft = 0;
long lastEncPosRight = 0;
long lastEncPosLeft = 0;
float x_pos = 0;
float y_pos = 0;
float curr_angle = 0;
boolean turn_started = false;

double Kp = 4;
double Ki = 0.0;
double Kd = 0.0;

PID distPIDFront(&distDiffFront,&servoAngleOutFront,&setDistDiff,Kp,Ki,Kd,DIRECT);
PID distPIDBack(&distDiffBack,&servoAngleOutBack,&setDistDiff,Kp,Ki,Kd,DIRECT);

SoftwareSerial bluetooth(RX_PIN,TX_PIN);


struct dist_values{
   float front_left;
   float front_right;
   float back_left;
   float back_right;
};

typedef struct dist_values PingValue;
PingValue pingValues;

void setup(){
  bluetooth.begin(9600);
  rightWheel.write(0);
  leftWheel.write(0);
  curr_robot_state = NAVIGATION_STATE;
  motorServo.attach(MOTOR_PIN);
  frontServo.attach(BACK_SERVO_PIN);
  backServo.attach(FRONT_SERVO_PIN);
  pinMode(LED_PIN,OUTPUT);
  

  
  setDistDiff = 1.0;
  distPIDFront.SetMode(AUTOMATIC);
  distPIDFront.SetOutputLimits(-50,50);
  distPIDBack.SetMode(AUTOMATIC);
  distPIDBack.SetOutputLimits(-50,50);
  
}

void loop(){
  switch(curr_robot_state){
     case NAVIGATION_STATE:
         receiveAndroidSignals();
         update_ping_distances();
         setServoAngle();
         motorServo.write(motorDirection);
         analogWrite(LED_PIN,ledBrightness);
       break; 
     case TURNING_STATE:
         turnRobotInCorner();
       break;  
     case REST_STATE:
         receiveAndroidSignals();
         break;
  }
}




void update_ping_distances(){
  pingValues.front_left  = distSensorFL.getDistance();
  pingValues.front_right = distSensorFR.getDistance();
  pingValues.back_left   = distSensorBL.getDistance();
  pingValues.back_right  = distSensorBR.getDistance();
  
  distDiffFront =  pingValues.front_left - pingValues.front_right;
  distDiffBack =   pingValues.back_left  - pingValues.back_right;
  distPIDFront.Compute();
  distPIDBack.Compute();
  delay(50);
}


void setServoAngle(){
  frontServo.write(90 + (int)servoAngleOutFront);
  backServo.write(90 + (int)servoAngleOutBack);
}


// this will turn robot 90 degrees
void turnRobotInCorner(){
     // momentarily stop robot at corner and reset encoders
     if(turn_started){
         rightWheel.write(0);
         leftWheel.write(0);
         motorServo.write(MOTOR_BREAK);
         delay(5000);
         motorServo.write(motorDirection);
         turn_started = false;
     }
     if(curr_angle <= 1.57){
        updateEncoders();
        calcRobotAngle();
     }else{
         // turn completed, get back to navigation
         // and tell Android phone that robot is done turning
         curr_robot_state =  NAVIGATION_STATE;
         bluetooth.write("TRDNE.");
     }
}



void calcRobotAngle(){
   float left_dist = ((float)currEncPosLeft-lastEncPosLeft)*cm_per_tick;
   float right_dist = ((float)currEncPosRight-lastEncPosRight)*cm_per_tick;
  
   lastEncPosRight = currEncPosRight;
   lastEncPosLeft = currEncPosLeft;
   curr_angle += fabs((left_dist - right_dist)/DIST_BETWEEN_WHEELS_CM);
   
   // these are needed if the robot cares about its
   // x and y position within hallway
  
   //curr_angle = fmod(curr_angle,PI*2);
  // float delta_dist = (left_dist + right_dist)/2.0f;
  // x_pos += delta_dist * cos(curr_angle);
  // y_pos += delta_dist * sin(curr_angle);
}  


void updateEncoders(){
  long newPos;
  
  // update right Encoder
  newPos = rightWheel.read();
  if(-newPos != currEncPosRight){
    currEncPosRight = -newPos;
  }
  
  // update left Encoder
  newPos = leftWheel.read();
  if(newPos != currEncPosLeft){
    currEncPosLeft = newPos;
  }
}



void receiveAndroidSignals(){
  if(bluetooth.available()){
      bluetooth.readBytes(incomingAndroidMsg,ANDROID_MESSAGE_LENGTH);
      int code  = atoi(incomingAndroidMsg);
      switch(code){
         case FORWARD_CODE:
            motorDirection = MOTOR_FORWARD;
            ledBrightness = 50;
            curr_robot_state = NAVIGATION_STATE;
            break;
         case BACKWARD_CODE:
             motorDirection = MOTOR_BACKWARD;
             ledBrightness = 100;
             curr_robot_state = NAVIGATION_STATE;
             break;
         case STOP_CODE:
             motorDirection = MOTOR_BREAK;
             ledBrightness = 200;
             curr_robot_state = REST_STATE;
             break;
         case TURN_ROBOT_CODE: 
              curr_robot_state = TURNING_STATE;
              curr_angle = 0;
              ledBrightness = 250;
              turn_started = true;
             break;
               
      }
  } 
}










