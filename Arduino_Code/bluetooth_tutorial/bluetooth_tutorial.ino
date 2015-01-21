 // refer
 // http://mcuoneclipse.com/2013/06/19/using-the-hc-06-bluetooth-module/
 // for baud rates

 // HC-06 Data Sheet
 // http://www.e-gizmo.com/KIT/images/EGBT-04/EGBT-045MS-046S%20Bluetooth%20Module%20Manual%20rev%201r0.pdf

#include <SoftwareSerial.h>

#define RX_PIN 10
#define TX_PIN 11
#define KEY_PIN 7

SoftwareSerial MySerial(RX_PIN,TX_PIN);
String response = "";

void setup(){
   pinMode(KEY_PIN,OUTPUT);
   digitalWrite(KEY_PIN,HIGH);
   Serial.begin(9600);
   Serial.println("Enter AT commands:");

   
   MySerial.write("AT+BAUD4");
   MySerial.begin(9600);
}

void loop(){
   if(MySerial.available()){
     while(MySerial.available()){
       response += (char)MySerial.read();
     }
     Serial.println(response);
     response = "";
   }
   if(Serial.available()){
      MySerial.write(Serial.read()); 
   }
}
