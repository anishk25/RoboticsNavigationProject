#include "Wire.h"
#include "gyroscope.h"

Gyroscope gyro(220,76,-85,1788);
void setup(){
  Serial.begin(9600);
  gyro.setup();
  gyro.calibrateGyro();
}

void loop(){
  float yaw = gyro.getYPR(GET_YAW);
  Serial.println(yaw);
}


