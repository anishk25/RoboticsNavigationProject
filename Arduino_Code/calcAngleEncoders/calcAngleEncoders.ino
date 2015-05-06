#include <Encoder.h>
#include <Servo.h>

#define MOTOR_FORWARD 80
#define MOTOR_BREAK 95

#define DIST_BETWEEN_WHEELS_CM 25.05
#define WHEEL_CIRCUMFRENCE_CM 48.1

#define MOTOR_FORWARD 82
#define MOTOR_BREAK 95

#define BACK_SERVO_PIN 9
#define FRONT_SERVO_PIN 8
#define MOTOR_PIN 26

const float gearRatio = 64*16;
const float cm_per_tick = WHEEL_CIRCUMFRENCE_CM/gearRatio;

Servo frontServo, backServo, motorServo;

Encoder rightWheel(2,4);
Encoder leftWheel(3,5);
long currEncPosRight = 0;
long currEncPosLeft = 0;
long lastEncPosRight = 0;
long lastEncPosLeft = 0;

float curr_angle = 0;

void setup(){
  Serial.begin(9600);
  
  rightWheel.write(0);
  leftWheel.write(0);
  
  motorServo.attach(MOTOR_PIN);
  frontServo.attach(BACK_SERVO_PIN);
  backServo.attach(FRONT_SERVO_PIN);
  
  backServo.write(40);
  frontServo.write(150);
  motorServo.write(MOTOR_FORWARD);
 
}

void loop(){
  if(curr_angle <= 1.57){
    updateEncoders();
    calcRobotAngle();
  }else{
     motorServo.write(MOTOR_BREAK);
  }
  
  
}

// could modify this function to take an encoder as a parameter
// but it doesn't register correct encoder values that way
void updateEncoders(){
  long newPos;
  
  // update right Encoder
  newPos = rightWheel.read();
  if(-newPos != currEncPosRight){
    currEncPosRight = -newPos;
    //Serial.print("right: ");
    //Serial.println(currEncPosRight);
  }
  
  // update left Encoder
  newPos = leftWheel.read();
  if(newPos != currEncPosLeft){
    currEncPosLeft = newPos;
    //Serial.print("left: ");
    //Serial.println(currEncPosLeft);
  }
}

void calcRobotAngle(){
    float left_dist = ((float)currEncPosLeft-lastEncPosLeft) * cm_per_tick;
    float right_dist = ((float)currEncPosRight-lastEncPosRight) * cm_per_tick;
    lastEncPosRight = currEncPosRight;
    lastEncPosLeft = currEncPosLeft;
    curr_angle += fabs((left_dist - right_dist)/DIST_BETWEEN_WHEELS_CM);
    Serial.println(curr_angle); 
}

