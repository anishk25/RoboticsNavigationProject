#define LED_PIN 13
#include<TimerThree.h>
#define INTERUPT_PERIOD 2000000
int ledBrightness = 0;
int count = 0;
int incomingByte = 0;
boolean flag = false;
char incomingMessage[4];


void setup(){
  Serial.begin(9600);
  pinMode(LED_PIN,OUTPUT);
  randomSeed(analogRead(0)); 
  
  /*Timer3.initialize();
  Timer3.attachInterrupt(msgInterrupt,INTERUPT_PERIOD);
  Timer3.start();*/
   
}

void loop(){
   if(Serial.available()){   
     Serial.readBytes(incomingMessage,4);
     if(strcmp(incomingMessage,"BRIT") == 0){
         ledBrightness = 200;
     }else if(strcmp(incomingMessage,"DARK") == 0){
         ledBrightness = 20; 
     }
     //ledBrightness = Serial.read();
   }
   analogWrite(LED_PIN,ledBrightness);
}

void msgInterrupt(){
   Serial.write(count);
   count++;
}
