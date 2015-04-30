#include <SoftwareSerial.h>
#include <Servo.h>
#include<TimerThree.h>

#define LED_PIN 13
#define RX_PIN 10
#define TX_PIN 11
#define SERVO_PIN 12

#define MAX_BYTES_TO_READ 3
#define MAX_BYTES_TO_WRITE 4
#define INTERUPT_PERIOD 1000000

#define MSG

SoftwareSerial bluetooth(RX_PIN,TX_PIN);
//int bluetooth_data;
char bluetooth_data[MAX_BYTES_TO_READ];
char send_buffer[MAX_BYTES_TO_WRITE];
Servo testServo;
int receive_valid_count = 0;
int old_receive_count = 0;
int send_count = 0;

void setup(){
  testServo.attach(SERVO_PIN);
  bluetooth.begin(9600);
  pinMode(LED_PIN,OUTPUT);
  Timer3.initialize();
  Timer3.attachInterrupt(sendInterrupt,INTERUPT_PERIOD);
  Timer3.start();
}

void loop(){
  
   /*if(bluetooth.available()){
      bluetooth.readBytes(bluetooth_data,MAX_BYTES_TO_READ);
      int servoAngle = atoi(bluetooth_data);
      int ledValue = map(servoAngle,0,180,0,255);
      if(servoAngle >= 0 && servoAngle <= 180){
          //testServo.write(servoAngle);
          analogWrite(LED_PIN,ledValue);  
          receive_valid_count++;
      }
   }
   delay(50);*/
   /*if(receive_valid_count != old_receive_count){
     old_receive_count = receive_valid_count;
     if(receive_valid_count % 2 == 0){
       bluetooth.write("E.");
     }else{
       bluetooth.write("O.");
     }
   }*/
}

void sendInterrupt(){
    if(send_count % 2 == 0){
       bluetooth.write("E.");
     }else{
       bluetooth.write("O.");
     }
     send_count++;
}
