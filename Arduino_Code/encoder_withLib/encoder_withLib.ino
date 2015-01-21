#include <Encoder.h>
#include <Servo.h>

#define DRIVE_MOTOR_PIN 11

Encoder rightWheel(2,4);
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
  long newRight;  
  newRight = rightWheel.read();
  
  if(newRight != positionRight){
     Serial.println(newRight/ratio);
     positionRight = newRight; 
  }
}
