#include <Encoder.h>
#include <Servo.h>

#define DRIVE_MOTOR_PIN 11

Encoder rightWheel(3,5);
Servo driveMotor;

float ratio = 64*16;

long positionRight = -999;

void setup(){
  rightWheel.write(0);
  driveMotor.attach(DRIVE_MOTOR_PIN);
  Serial.begin(9600);
}

void loop(){
  //driveMotor.write(80);
  /*long newRight;  
  newRight = rightWheel.read();
  
  if(newRight != positionRight){
     Serial.println(newRight);
     positionRight = newRight; 
  }*/
  updateEncoder(&rightWheel,&positionRight);
}

void updateEncoder(Encoder* encoder, long *currPos){
  long newPos; 
  newPos= encoder->read();
  if(newPos != *currPos){
    *currPos = newPos;
     Serial.println(*currPos);
  }
}
