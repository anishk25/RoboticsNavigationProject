#include <Encfoder.h>

#define encoderPinA 2
#define encoderPinB 4

volatile int encoderPos = 0;
float ratio = 64*8;

void setup(){
   pinMode(encoderPinA,INPUT);
   digitalWrite(encoderPinA,HIGH);
   pinMode(encoderPinB,INPUT);
   digitalWrite(encoderPinB,HIGH);
   attachInterrupt(0,doEncoder,CHANGE); 
   Serial.begin(9600);
   Serial.println(encoderPos,DEC); 
}

void loop(){
  Serial.println(encoderPos/ratio,DEC);
}

void doEncoder(){
   if(digitalRead(encoderPinA) == digitalRead(encoderPinB)){
      encoderPos++;
   }else{
      encoderPos--;
   }
}

