#include <timer_interrupt.h>
#include <Encoder.h>

Encoder rightWheel(2,4);
Encoder leftWheel(3,5);

const float ratio = 64*16;
const int clockFreq = 10;
const float timeInc = 1.0f/clockFreq;

long positionRight = -999;
long positionLeft = -999;
long lastPositionRight = 0;
long lastPositionLeft = 0;

float velocity_right = 0.0f;
float velocity_left = 0.0f;

void setup(){
   Serial.begin(9600);
   rightWheel.write(0);
   leftWheel.write(0);
   setupTimer(clockFreq,256);
}

void loop(){
   updatePositions();
   Serial.print("velocity: ");
   Serial.println(velocity_right);
  
}

void updatePositions(){
    long newRight = rightWheel.read();
    long newLeft = leftWheel.read();
    
    if(newRight != positionRight || newLeft != positionLeft){
        positionRight = newRight;
        positionLeft = positionLeft;
    }
}


ISR(TIMER1_COMPA_vect){
   
   long diff_right = positionRight - lastPositionRight;
   long diff_left = positionLeft - lastPositionLeft;
   
   velocity_right = diff_right/timeInc;
   velocity_left = diff_left/timeInc;
   
   lastPositionRight = positionRight;
   //lastPositionLeft = positionLeft;
}

