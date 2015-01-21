#include <kalman_gyro.h>
#include <gyroscope.h>
#include "Wire.h"


#define GYROSCOPE_SENSITIVITY 65.536

KalmanFilterGyro kalmanFilter;
unsigned long timer;
Gyroscope gyro(220,76,-85,1788);



void setup(){
  Serial.begin(9600);
  gyro.setup();
  gyro.calibrateGyro();
  
  // initialize with starting gyro angle
  kalmanFilter.setAngle(getGyroYaw());
  timer = micros();
  
  
}

float getGyroYaw(){
   float yaw = gyro.getYPR(GET_YAW);
   //return yaw < 0 ? yaw + 360  : yaw;
   return yaw;
}

void loop(){
   float currYaw = getGyroYaw();
   float currRate = gyro.getYawRate();
   float filteredAngle = kalmanFilter.getAngle(currYaw,currRate,((float)(micros() - timer))/1000000);
   timer = micros();
   Serial.println(filteredAngle);
}
