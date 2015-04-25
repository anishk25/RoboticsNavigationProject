#include <Servo.h>

#define LED_PIN 13


#define LEFT_BRIGHTNESS 45
#define RIGHT_BRIGHTNESS 90
#define STRAIGHT_BRIGHTNESS 135
#define FORWARD_BRIGHTNESS 180
#define BACKWARD_BRIGHTNESS 225
#define STOP_BRIGHTNESS 255

#define BACK_SERVO_PIN 9
#define FRONT_SERVO_PIN 8
#define MOTOR_PIN 11

// MOTOR_BREAK is definitely 95 but might have
// to swap values of MOTOR_BACKWARD and MOTOR_FORWARD
#define MOTOR_FORWARD 81
#define MOTOR_BREAK 95
#define MOTOR_BACKWARD 105

// same situation with these angles,
// STRAIGHT_ANGLE is definitely 90
// but LEFT and RIGHT values might need to be 
// swapped
#define STRAIGHT_ANGLE  90
#define LEFT_ANGLE      110
#define RIGHT_ANGLE     70

// might have to change the pin connections of front and back servo
// pin, only the front one will be changing angles
// according to received message, back servo will always
// remain at 90
Servo frontServo, backServo, motorServo;

int motorDirection = MOTOR_BREAK;
int ledBrightness = 0;
int servoAngle = STRAIGHT_ANGLE;

char incomingMessage[4];

void setup(){
  motorServo.attach(MOTOR_PIN);
  frontServo.attach(FRONT_SERVO_PIN);
  backServo.attach(BACK_SERVO_PIN);
  pinMode(LED_PIN,OUTPUT);
  
  frontServo.write(STRAIGHT_ANGLE);
  backServo.write(STRAIGHT_ANGLE);
}

void loop(){
  if(Serial.available()){
      Serial.readBytes(incomingMessage,4);
      if(strcmp(incomingMessage,"LEFT") == 0){
           servoAngle = LEFT_ANGLE;
           ledBrightness = LEFT_BRIGHTNESS;
       }else if(strcmp(incomingMessage,"RGHT") == 0){
           servoAngle = RIGHT_ANGLE;
           ledBrightness = RIGHT_BRIGHTNESS; 
       }else if(strcmp(incomingMessage,"STRT") == 0){
           servoAngle = STRAIGHT_ANGLE;
           ledBrightness = STRAIGHT_BRIGHTNESS; 
       }else if(strcmp(incomingMessage,"FRWD") == 0){
           motorDirection = MOTOR_FORWARD;
           ledBrightness = FORWARD_BRIGHTNESS; 
       }else if(strcmp(incomingMessage,"BKWD") == 0){
           motorDirection = MOTOR_BACKWARD;
           ledBrightness = BACKWARD_BRIGHTNESS; 
       }else if(strcmp(incomingMessage,"STOP") == 0){
           motorDirection = MOTOR_BREAK;
           ledBrightness = STOP_BRIGHTNESS; 
       }
  }
    frontServo.write(servoAngle);
    motorServo.write(motorDirection);
    analogWrite(LED_PIN,ledBrightness);
}  








