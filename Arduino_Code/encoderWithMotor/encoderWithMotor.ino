#include <Encoder.h>
#include <Servo.h>

#define MOTOR_PIN 11
#define BACK_SERVO_PIN 8
#define FRONT_SERVO_PIN 9

// Wheel Circumfrence is 18.875 inches
// 48.1 cm

#define RIGHT_ENCODER_A 2
#define RIGHT_ENCODER_B 4
#define LED_PIN 22

#define MOTOR_FORWARD 80
#define MOTOR_BREAK 90
#define MAX_REVOLUTIONS 1




Servo motorServo;
Servo frontServo;
Servo backServo;

Encoder rightWheel(RIGHT_ENCODER_A,RIGHT_ENCODER_B);

float ratio = 64*16;
int num_revolutions = 0;
long positionRight = -999;
boolean rotationsDone = false;

void setup(){
  rightWheel.write(0);
  pinMode(LED_PIN,OUTPUT);
  digitalWrite(LED_PIN,LOW);
  //motorServo.attach(MOTOR_PIN);
  //frontServo.attach(FRONT_SERVO_PIN);
  //backServo.attach(BACK_SERVO_PIN);
  
  //frontServo.write(90);
  //backServo.write(90);
  //Serial.begin(9600);
}

void loop(){
  if(!rotationsDone){
    countRevolutions();
    checkForStop();
  }
}

void checkForStop(){
   if(num_revolutions >= MAX_REVOLUTIONS){
      motorServo.write(MOTOR_BREAK);
      rotationsDone = true;
      digitalWrite(LED_PIN,HIGH);
   }else{
      motorServo.write(MOTOR_FORWARD);
   } 
}

void countRevolutions(){
    long newRight;  
    newRight = rightWheel.read();
    if(newRight != positionRight){
       float rev = newRight/ratio;
       positionRight = newRight; 
       if( rev >= 1.0f){
           num_revolutions++;
           Serial.print("Full Rotation: ");
           Serial.println(num_revolutions);
           rightWheel.write(0);
       }
    }
  
}


