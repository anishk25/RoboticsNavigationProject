#define LED_PIN 13
#include<TimerThree.h>
#define INTERUPT_PERIOD 2000000


int incomingByte = 140;
int count = 0;


void setup(){
  Serial.begin(9600);
  pinMode(LED_PIN,OUTPUT); 
  
  Timer3.initialize();
  Timer3.attachInterrupt(msgInterrupt,INTERUPT_PERIOD);
  Timer3.start();
   
}

void loop(){
   if(Serial.available()){   
     incomingByte = Serial.read(); 
     analogWrite(LED_PIN,incomingByte);
   }
}

void msgInterrupt(){
   Serial.write(count);
   count++;
}
