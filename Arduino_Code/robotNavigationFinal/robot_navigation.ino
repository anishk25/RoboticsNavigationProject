
#include <PID_v1.h>
#include <Servo.h>
#include <ping_sensor.h>
#include <Encoder.h>
#include<TimerThree.h>


#define DIST_BETWEEN_WHEELS_CM 25.05
#define WHEEL_CIRCUMFRENCE_CM 48.1
#define TWOPI PI*2

#define MOTOR_FORWARD 82
#define MOTOR_BREAK 95


#define BACK_SERVO_PIN 9
#define FRONT_SERVO_PIN 8
#define MOTOR_PIN 11

#define PING_PIN_FRONT_LEFT 7
#define PING_PIN_FRONT_RIGHT 6

#define PING_PIN_BACK_LEFT 12
#define PING_PIN_BACK_RIGHT 10

#define TARGET_DIST 304.8


const float gearRatio = 64*16;
const float cm_per_tick = WHEEL_CIRCUMFRENCE_CM/gearRatio;

Servo frontServo, backServo, motorServo;
double setDistDiff, distDiffFront, servoAngleOutFront;
double distDiffBack, servoAngleOutBack;

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

void setup(){
  Serial.begin(9600);
  
  rightWheel.write(0);
  leftWheel.write(0);
  
  motorServo.attach(MOTOR_PIN);
  frontServo.attach(BACK_SERVO_PIN);
  backServo.attach(FRONT_SERVO_PIN);
  
  motorServo.write(MOTOR_FORWARD);
  
  setDistDiff = 1.0;
  distPIDFront.SetMode(AUTOMATIC);
  distPIDFront.SetOutputLimits(-50,50);
  distPIDBack.SetMode(AUTOMATIC);
  distPIDBack.SetOutputLimits(-50,50);
  
}

void loop(){
  if(x_pos < TARGET_DIST){
    computePIDOut();
    setServoAngle();
    updateEncoders();
    calcRobotPos();
  }else{
    motorServo.write(MOTOR_BREAK);
  }
  
  Serial.print("Theta: ");
  Serial.print(curr_angle);
  Serial.print("\tX_pos: ");
  Serial.print(x_pos);
  Serial.print("\tY_pos: ");
  Serial.println(y_pos);
  
}

void computePIDOut(){
    distDiffFront = distSensorFL.getDistance() - distSensorFR.getDistance();
    distDiffBack =  distSensorBL.getDistance() - distSensorBR.getDistance();
    distPIDFront.Compute();
    distPIDBack.Compute();
}

void setServoAngle(){
  frontServo.write(90 + (int)servoAngleOutFront);
  backServo.write(90 + (int)servoAngleOutBack);
}

// could modify this function to take an encoder as a parameter
// but it doesn't register correct encoder values that way
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

void calcRobotPos(){
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





