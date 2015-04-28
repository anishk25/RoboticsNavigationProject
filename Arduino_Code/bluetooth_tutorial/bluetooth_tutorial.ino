#include <SoftwareSerial.h>

#define LED_PIN 13
#define RX_PIN 10
#define TX_PIN 11


SoftwareSerial bluetooth(RX_PIN,TX_PIN);
int bluetooth_data;

void setup(){
  bluetooth.begin(9600);
  bluetooth.println("Bluetooth On Please press 1 or 0 to blink LED..");
  pinMode(LED_PIN,OUTPUT);
}

void loop(){
   if(bluetooth.available()){
      bluetooth_data = bluetooth.read();
      if(bluetooth_data == '1'){
         digitalWrite(LED_PIN,HIGH); 
         bluetooth.println("LED on Arduino ON!");
      }else if(bluetooth_data == '0'){
         digitalWrite(LED_PIN,LOW);
          bluetooth.println("LED on Arduino OFF!"); 
      }
   }
  delay(100);
}
