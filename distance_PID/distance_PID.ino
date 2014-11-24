#include <Servo.h>

#define PING_PIN_LEFT 5
#define PING_PIN_RIGHT 6
#define DRIVE_SERVO_PIN 11
#define FRONT_SERVO_PIN 9
#define BACK_SERVO_PIN 8
#define MOTOR_FORWARD 105

const double Kp = 3.0f;
const double propThreshold = 60;
const double minError = 6;
long distanceLeft,distanceRight;
double prop_output;


Servo frontServo,backServo,driveServo;

void setup(){
    backServo.attach(BACK_SERVO_PIN);
    frontServo.attach(FRONT_SERVO_PIN);
    driveServo.attach(DRIVE_SERVO_PIN);
    
    backServo.write(90);
    driveServo.write(MOTOR_FORWARD);
    Serial.begin(9600);
}

void loop(){
    distanceLeft = getDistanceFromPing(PING_PIN_LEFT);
    distanceRight = getDistanceFromPing(PING_PIN_RIGHT);
    delay(20);
    prop_output = computeProportional(distanceLeft,distanceRight);
    controlTurn(prop_output);
    
   
}

// pos:right
// neg:left
void controlTurn(double propError){
    int servoAngle = 90;
    if(abs(propError) > minError){
         servoAngle += propError;
    }
    frontServo.write(servoAngle);
}


double computeProportional(long leftDistance, long rightDistance){
   double error = rightDistance - leftDistance;
   double output =  Kp * error;
   if(abs(output) > propThreshold){
      if(output < 0){
         return -propThreshold;
      } else{
         return propThreshold; 
      }
   }
   return output;
}

long getDistanceFromPing(int pingPin){
   // delay to prevent from sampling too much
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
   return microseconds / 29 / 2; 
}



