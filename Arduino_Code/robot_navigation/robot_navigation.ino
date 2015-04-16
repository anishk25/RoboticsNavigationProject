
#include <PID_v1.h>
#include <Servo.h>
#include <ping_sensor.h>
#include <Encoder.h>



#define DIST_BETWEEN_WHEELS_CM 25.05
#define WHEEL_CIRCUMFRENCE_CM 48.1
#define TWOPI PI*2

#define MOTOR_FORWARD 81
#define MOTOR_BREAK 95


#define BACK_SERVO_PIN 9
#define FRONT_SERVO_PIN 8
#define MOTOR_PIN 11

#define PING_PIN_FRONT_LEFT 7
#define PING_PIN_FRONT_RIGHT 6
#define PING_PIN_BACK_LEFT 12
#define PING_PIN_BACK_RIGHT 10
#define PING_PIN_HEAD_RIGHT 28
#define PING_PIN_HEAD_LEFT  30

// state variables
#define HALLWAY_NAVIGATION_STATE   0
#define TURNING_STATE              1



#define DIST_FRONT_FOR_TURN 61.44
#define DIST_SIDE_FOR_TURN 250;
#define DIST_SIDE_TO_STOP_TURN 170;


const float gearRatio = 64*16;
const float cm_per_tick = WHEEL_CIRCUMFRENCE_CM/gearRatio;

Servo frontServo, backServo, motorServo;
double setDistDiff, distDiffFront, servoAngleOutFront;
double distDiffBack, servoAngleOutBack;



PingSensor distSensorFL(PING_PIN_FRONT_LEFT);
PingSensor distSensorFR(PING_PIN_FRONT_RIGHT);
PingSensor distSensorBL(PING_PIN_BACK_LEFT);
PingSensor distSensorBR(PING_PIN_BACK_RIGHT);
PingSensor distSensorHR(PING_PIN_HEAD_RIGHT);
PingSensor distSensorHL(PING_PIN_HEAD_LEFT);

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


// robot will start out with navigating through hallways
int curr_state = HALLWAY_NAVIGATION_STATE;

struct dist_values{
   float front_left;
   float front_right;
   float back_left;
   float back_right;
   float head_right;
   float head_left; 
};
typedef struct dist_values PingValue;
PingValue pingValues;

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
  update_ping_distances();
  switch(curr_state){
   case  HALLWAY_NAVIGATION_STATE:
       navigateInHallway();
       break;
   case TURNING_STATE:
       turnInHallway();
       break;
  }
}


void navigateInHallway(){
  computePIDOut();
  setServoAngle();
  checkForHallwayEdge();
  //updateEncoders();
  //calcRobotPos();
}

void turnInHallway(){
    if( (pingValues.front_left < 60 || pingValues.front_right < 60)){
        curr_state =  HALLWAY_NAVIGATION_STATE;
        motorServo.write(MOTOR_BREAK);
        delay(5000);
        motorServo.write(MOTOR_FORWARD);
    }
}

void update_ping_distances(){
  pingValues.front_left  = distSensorFL.getDistance();
  pingValues.front_right = distSensorFR.getDistance();
  pingValues.back_left   = distSensorBL.getDistance();
  pingValues.back_right  = distSensorBR.getDistance();
  pingValues.head_left   = distSensorHL.getDistance();
  pingValues.head_right  = distSensorHR.getDistance();
  delay(50);
}



void checkForHallwayEdge(){
    float dist_avg = (pingValues.head_right + pingValues.head_left)/2;
    if( dist_avg <= DIST_FRONT_FOR_TURN && (pingValues.front_left >= 200 || pingValues.front_right >= 200)){
       curr_state = TURNING_STATE;
        if(pingValues.front_left >= 300){
             backServo.write(40);
             frontServo.write(140);
        }else{
             backServo.write(140);
             frontServo.write(40); 
        }
        motorServo.write(MOTOR_BREAK);
        delay(5000);
        motorServo.write(MOTOR_FORWARD);
    }
}

void computePIDOut(){
    distDiffFront =  pingValues.front_left - pingValues.front_right;
    distDiffBack =   pingValues.back_left  - pingValues.back_right;
    
    // check if one of the sides of the robot is facing an empty space
    if(pingValues.front_left >= 150 || pingValues.front_right >= 150){
        if(pingValues.front_left >= 200){
           distDiffFront = 76.2f - pingValues.front_left;
        }else{
           distDiffFront = 76.2f - pingValues.front_right;
        }
     }
     
     if(pingValues.back_left >= 200 || pingValues.back_right >= 200){
        if(pingValues.front_left >= 200){
           distDiffBack = 76.2f - pingValues.back_left;
        }else{
           distDiffBack = 76.2f  - pingValues.back_right;
        }
     }
    
    distPIDFront.Compute();
    distPIDBack.Compute();
}

void setServoAngle(){
  frontServo.write(90 + (int)servoAngleOutFront);
  backServo.write(90 + (int)servoAngleOutBack);
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





