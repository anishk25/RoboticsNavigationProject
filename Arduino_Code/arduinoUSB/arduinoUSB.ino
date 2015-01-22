#define LED_PIN 13
int incomingByte = 140;

void setup(){
  Serial.begin(9600);
   pinMode(LED_PIN,OUTPUT); 
}

void loop(){
   if(Serial.available()){
      incomingByte = Serial.read();
   }
   analogWrite(LED_PIN,incomingByte); 
}
