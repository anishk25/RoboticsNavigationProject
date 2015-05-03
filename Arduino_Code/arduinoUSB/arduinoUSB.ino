#define LED_PIN 13
#include<TimerThree.h>

int ledBrightness = 0;
int count = 0;
int incomingByte = 0;
boolean flag = false;
char incomingMessage[4];


void setup(){
  Serial.begin(9600);
  pinMode(LED_PIN,OUTPUT);


   
}

void loop(){
   if(Serial.available()){   
     Serial.readBytes(incomingMessage,4);
     if(strcmp(incomingMessage,"DARK") == 0){
         ledBrightness = 50;
     }else if(strcmp(incomingMessage,"BRIT") == 0){
         ledBrightness = 200; 
     }
   }
   analogWrite(LED_PIN,ledBrightness);
}

