#include <Servo.h>

#define BACK_SERVO_PIN 9
#define FRONT_SERVO_PIN 8
#define MOTOR_PIN 11

#define MOTOR_FORWARD 81

Servo frontServo, backServo, motorServo;

void setup(){
  motorServo.attach(MOTOR_PIN);
  frontServo.attach(FRONT_SERVO_PIN);
  backServo.attach(BACK_SERVO_PIN);
  
  frontServo.write(90);
  backServo.write(90);
  motorServo.write(MOTOR_FORWARD);
}
void loop(){
    int angle;
    for(angle = 60; angle <= 120; angle++){
       frontServo.write(angle);
       delay(20);
    }
    for(angle = 120; angle >= 60; angle--){
       frontServo.write(angle);
       delay(20);
    }
}  
