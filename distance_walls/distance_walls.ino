#include <Servo.h>

#define PING_PIN_LEFT     8
#define PING_PIN_RIGHT   7
#define FRONT_SERVO_PIN   6
#define BACK_SERVO_PIN    5
#define MOTOR_SERVO_PIN   11

Servo frontServo,backServo,motorServo;
long distanceLeft, distanceRight;
long threshHold = 8;


void setup(){
    frontServo.attach(FRONT_SERVO_PIN);
    backServo.attach(BACK_SERVO_PIN);
    motorServo.attach(MOTOR_SERVO_PIN);
    goStraight(&backServo);
    Serial.begin(9600);
}

void loop(){
   motorServo.write(105);
   //keepStraightFromWalls();
}


void keepStraightFromWalls(){
   distanceLeft =  getDistanceFromPing(PING_PIN_LEFT);
   distanceRight = getDistanceFromPing(PING_PIN_RIGHT);
   turnBasedOnDist();
}

void turnBasedOnDist(){
   if(abs(distanceLeft-distanceRight) >= threshHold){
       if(distanceLeft < distanceRight){
         turnRight(&frontServo); 
       }else{
         turnLeft(&frontServo); 
       }
   }else{
     goStraight(&frontServo);
   }
}

void turnLeft(Servo* s){
  s->write(60);
}

void turnRight(Servo* s){
  s->write(140);
}

void goStraight(Servo* s){
  s->write(90);
}

long getDistanceFromPing(int pingPin){
   // delay to prevent from sampling too much
   delay(30);
   long duration,dist_cm;
   
  // The PING is triggered by a HIGH pulse of 2 or more microseconds.
  // Give a short LOW pulse beforehand to ensure a clean HIGH pulse:
   pinMode(pingPin,OUTPUT);
   digitalWrite(pingPin,LOW);
   delayMicroseconds(2);
   digitalWrite(pingPin,HIGH);
   delayMicroseconds(5);
   digitalWrite(pingPin,LOW);
   
   pinMode(pingPin,INPUT);
   duration = pulseIn(pingPin,HIGH); 
   dist_cm = microSecondsToCm(duration);
   return dist_cm; 
     
}

long microSecondsToCm(long microseconds){
   // speed of sound is 340 m/s or 29 ms/cm so to find
   // the distance the ping travels back and forth we take
   // half of the distance traveled to get the measurement
   return microseconds / 29 / 2; 
}
