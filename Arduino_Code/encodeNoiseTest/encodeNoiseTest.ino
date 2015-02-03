#include <Encoder.h>
#include <Servo.h> 

#define MOTOR_FORWARD 102
#define MOTOR_BREAK 95
#define BACK_SERVO_PIN 8
#define FRONT_SERVO_PIN 9
#define MOTOR_PIN 11

#define WHEEL_CIRCUMFRENCE_CM 48.1
#define TARGET_DISTANCE_CM 182.88 // 6 ft

const float gearRatio = 64*16;
float totalDistanceTraveled = 0;
long currWheelPosition = 0;

Encoder wheel(2,4);
Servo motorServo;
Servo frontServo;
Servo backServo;

void setup(){
    Serial.begin(9600);
    wheel.write(0);
    // assign pins to servo motors
    motorServo.attach(MOTOR_PIN);
    frontServo.attach(BACK_SERVO_PIN);
    backServo.attach(FRONT_SERVO_PIN);
    
    // set back Servo to a fixed angle of 90 degrees
    backServo.write(95);
    frontServo.write(95);
}

void loop(){
    if(totalDistanceTraveled < TARGET_DISTANCE_CM){
        updateEncoder();
        motorServo.write(MOTOR_FORWARD);
    }else{
         motorServo.write(MOTOR_BREAK);
    }
    
}

void updateEncoder(){
   long newWheelPos = wheel.read();
   if(newWheelPos != currWheelPosition){
       currWheelPosition = newWheelPos; 
       totalDistanceTraveled = (((float)currWheelPosition)/gearRatio) * WHEEL_CIRCUMFRENCE_CM;
       Serial.println(totalDistanceTraveled);
   }
}





