#include <PID_v1.h>
#include <Servo.h>
#include <Wire.h>
#include <gyroscope.h>


#define FRONT_SERVO_PIN 5
#define BACK_SERVO_PIN 6
#define MOTOR_SERVO_PIN  11


Servo frontServo,backServo,motorServo;
Gyroscope gyro(220,76,-85,1788);
double setAngle, gyroAngle, pidOutput;
double Kp = 5;
double Ki = 0;
double Kd = 0;
boolean firstCollected = false;

PID myPID(&gyroAngle,&pidOutput,&setAngle,Kp,Ki,Kd,DIRECT);

void setup(){
    Serial.begin(9600);
    frontServo.attach(FRONT_SERVO_PIN);
    backServo.attach(BACK_SERVO_PIN);
    motorServo.attach(MOTOR_SERVO_PIN);
    gyro.setup();
    gyro.calibrateGyro();
    
    setAngle = 56;
    gyroAngle = 50;
    myPID.SetMode(AUTOMATIC);
    myPID.SetOutputLimits(-60,60);
    
    
}

void loop(){
      if(!firstCollected){
         firstCollected = true;
         setAngle = gyro.getYPR(GET_YAW);
      }
       motorServo.write(105); 
       gyroAngle = gyro.getYPR(GET_YAW);
       myPID.Compute();
       setServoAngle();
       Serial.print("gyro yaw: ");
       Serial.print(gyroAngle);
       Serial.print("     pid output: ");
       Serial.println(pidOutput);
}

void setServoAngle(){
    int servoAngle = 90 + (int)pidOutput;
    frontServo.write(servoAngle);
}
  


