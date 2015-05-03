#include <SoftwareSerial.h>
#define LED_PIN 13
#define RX_PIN 10
#define TX_PIN 11


#define ANDROID_MESSAGE_LENGTH 3
#define STOP_CODE  200
#define START_CODE 100

char incomingAndroidMsg[ANDROID_MESSAGE_LENGTH];
SoftwareSerial bluetooth(RX_PIN,TX_PIN);

void setup(){
  bluetooth.begin(9600);
  pinMode(LED_PIN,OUTPUT);  
}
void loop(){
  if(bluetooth.available()){
      bluetooth.readBytes(incomingAndroidMsg,ANDROID_MESSAGE_LENGTH);
      int code  = atoi(incomingAndroidMsg);
      if(code == START_CODE){
          analogWrite(LED_PIN,100);
      }else if(code == STOP_CODE){
          analogWrite(LED_PIN,200);
      }
  }
  delay(50);
}


