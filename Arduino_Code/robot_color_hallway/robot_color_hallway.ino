
#include <PID_v1.h>
#include <Servo.h>
#include <ping_sensor.h>
#include <Encoder.h>
#include <SoftwareSerial.h>



#define DIST_BETWEEN_WHEELS_CM 25.05
#define WHEEL_CIRCUMFRENCE_CM 48.1
#define TWOPI PI*2

#define MOTOR_FORWARD 81
#define MOTOR_BREAK 95

#define BACK_SERVO_PIN 9
#define FRONT_SERVO_PIN 8
#define MOTOR_PIN 4

//#define PING_PIN_FRONT_LEFT 7
//#define PING_PIN_FRONT_RIGHT 6
//#define PING_PIN_BACK_LEFT 12
//#define PING_PIN_BACK_RIGHT 3

#define PING_PIN_FRONT_LEFT 12
#define PING_PIN_FRONT_RIGHT 3
#define PING_PIN_BACK_LEFT 7
#define PING_PIN_BACK_RIGHT 6


#define LED_PIN 13

#define ANDROID_MESSAGE_LENGTH 3
#define STOP_CODE  200
#define START_CODE 100
#define RX_PIN 10
#define TX_PIN 11

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

Encoder rightWheel(2,4);
Encoder leftWheel(3,5);
long currEncPosRight = 0;
long currEncPosLeft = 0;
long lastEncPosRight = 0;
long lastEncPosLeft = 0;
float x_pos = 0;
float y_pos = 0;
float curr_angle = 0;

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
  receiveAndroidSignals();
  //calcRobotPos();
  update_ping_distances();
  setServoAngle();
  motorServo.write(motorDirection);
  analogWrite(LED_PIN,ledBrightness);
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



void calcRobotPos(){
   updateEncoders();
   float left_dist = ((float)currEncPosLeft-lastEncPosLeft)*cm_per_tick;
   float right_dist = ((float)currEncPosRight-lastEncPosRight)*cm_per_tick;
  
   lastEncPosRight = currEncPosRight;
   lastEncPosLeft = currEncPosLeft;
   
   float delta_dist = (left_dist + right_dist)/2.0f;
   curr_angle += (left_dist - right_dist)/DIST_BETWEEN_WHEELS_CM;
   curr_angle = fmod(curr_angle,PI*2);
   
   x_pos += delta_dist * cos(curr_angle);
   y_pos += delta_dist * sin(curr_angle);
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
      if(code == START_CODE){
           motorDirection = MOTOR_FORWARD;
           ledBrightness =100;
      }else if(code == STOP_CODE){
           motorDirection = MOTOR_BREAK;
           ledBrightness = 200;
      }
  } 
}



