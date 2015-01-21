#include "Wire.h"
#include <kalman_encoder.h>
#include<TimerThree.h>
#include<Encoder.h>
#include<Servo.h>

#define TIMER_INTERUPT_PERIOD 100000
#define WHEEL_CIRCUMFRENCE_CM 48.1
#define TARGET_DISTANCE_CM 457.2

#define MOTOR_FORWARD 80
#define MOTOR_BACKWARD 110
#define MOTOR_STOP 95

#define MOTOR_PIN 11
#define BACK_SERVO_PIN 8
#define FRONT_SERVO_PIN 9
#define LED_PIN 24

//Kalman Filter Constants
#define VELOCITY_NOISE 0.15
#define POSITION_NOISE 0.15
#define ENCODER_NOISE 0.05

// wheel and servos
Encoder rightWheel(2,4);
Servo motorServo;
Servo frontServo;
Servo backServo;

// encoder constants
const float gearRatio = 64*16;            //  This is the gear ratio used to count number of revolutions in the wheel
const int clockFreq = 10;     
const float timeInc = 1.0f/clockFreq;

//variables to keep track of wheel
long currWheelPosition = 0;
long lastWheelPosition = 0;
volatile float distanceTraveledInCm = 0;
boolean target_distance_reached = false;
boolean break_applied = false;


//Kalman Filter
KalmanFilterEncoder kalmanEncoder(POSITION_NOISE,VELOCITY_NOISE,ENCODER_NOISE);


void setup(){
  Serial.begin(9600);
  
  // initialize wheel to start counting from 0
  rightWheel.write(0);
  
  // assign pins to servo motors
  motorServo.attach(MOTOR_PIN);
  frontServo.attach(BACK_SERVO_PIN);
  backServo.attach(FRONT_SERVO_PIN);
  
  // set back Servo to a fixed angle of 90 degrees
  backServo.write(90);
  frontServo.write(90);
 
  Timer3.initialize();
  Timer3.attachInterrupt(distTimerInterrupt,TIMER_INTERUPT_PERIOD);
  Timer3.start();
}

void loop(){
    if(!target_distance_reached){
      updateEncoder();
      motorServo.write(MOTOR_FORWARD);
      Serial.println(distanceTraveledInCm);
    }else{
       //motorServo.write(MOTOR_BREAK);
       if(!break_applied){
         motorServo.write(MOTOR_BACKWARD);
         delay(20);
         motorServo.write(MOTOR_STOP);
         break_applied = true; 
       }

    }
}



void updateEncoder(){
    long newWheelPos = rightWheel.read();
    if(newWheelPos != currWheelPosition){
       currWheelPosition = newWheelPos; 
    }
}




void distTimerInterrupt(){
    long diff_position = currWheelPosition - lastWheelPosition;
    lastWheelPosition = currWheelPosition;
    
    // compute number of rotations done by wheel
    float num_rotations = ((float)diff_position) / gearRatio;
    
    // this is the distance traveled by the wheels
    float dist_traveled = num_rotations * WHEEL_CIRCUMFRENCE_CM;
    float curr_velocity = dist_traveled/timeInc;
    
    // measured position based only on encoders
    float measuredPosition = distanceTraveledInCm + dist_traveled;
    
    // filter new position using Kalman Filter
    distanceTraveledInCm = kalmanEncoder.getFilteredPosition(measuredPosition,curr_velocity,timeInc);
    
    if(distanceTraveledInCm >= TARGET_DISTANCE_CM){
         target_distance_reached = true;
    }
}







