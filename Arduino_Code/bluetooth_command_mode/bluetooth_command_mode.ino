#include <SoftwareSerial.h>

#define KEY_PIN 9
#define RX_PIN 10
#define TX_PIN 11

SoftwareSerial bluetooth(RX_PIN,TX_PIN);

void setup(){
  Serial.begin(9600);
  pinMode(KEY_PIN,OUTPUT);
  digitalWrite(KEY_PIN,HIGH);
  Serial.println("Enter AT commands");
  bluetooth.begin(9600);
}

void loop(){
   if(bluetooth.available()){
      Serial.write(bluetooth.read()); 
   }
   if(Serial.available()){
      bluetooth.write(Serial.read()); 
   }
}
