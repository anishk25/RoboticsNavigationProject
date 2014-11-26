#ifndef GYROSCOPE_H
#define GYROSCOPE_H

#if defined(ARDUINO) && ARDUINO >= 100
	#include "Arduino.h"
#endif

#include "I2Cdev.h"
#include "MPU6050_6Axis_MotionApps20.h"
#define DO_CALIB 1
#define DO_READING 2

#define GET_YAW 0
#define GET_PITCH 1
#define GET_ROLL 2

volatile bool mpuInterrupt = false; 

void dmpDataReady() {
    mpuInterrupt = true;
}

class Gyroscope{
private:
	MPU6050 mpu;
	// MPU control/status vars
	bool dmpReady;  // set true if DMP init was successful
	uint8_t mpuIntStatus;   // holds actual interrupt status byte from MPU
	uint8_t devStatus;      // return status after each device operation (0 = success, !0 = error)
	uint16_t packetSize;    // expected DMP packet size (default is 42 bytes)
	uint16_t fifoCount;     // count of all bytes currently in FIFO
	uint8_t fifoBuffer[64]; // FIFO storage buffer

	// orientation/motion vars
	Quaternion q;           // [w, x, y, z]         quaternion container
	VectorFloat gravity;    // [x, y, z]            gravity vector
	float euler[3];         // [psi, theta, phi]    Euler angle container
	float ypr[3];           // [yaw, pitch, roll]   yaw/pitch/roll container and gravity vector
	float yaw,pitch,roll;

	int x_offset,y_offset,z_offset,z_accel_offset;

	// variables for calibrating gyro
	bool calib_done;
	bool first_meas_done;
	int meas_count;
	float total_error;
	float prev_yaw;
	float curr_yaw;


public:
	Gyroscope(int xOffset, int yOffset, int zOffset, int zAccelOffset){
		this->x_offset = xOffset;
		this->y_offset = yOffset;
		this->z_offset = zOffset;
		this->z_accel_offset = zAccelOffset;
	}

	void setup(){
		#if I2CDEV_IMPLEMENTATION == I2CDEV_ARDUINO_WIRE
        	Wire.begin();
        	TWBR = 24; // 400kHz I2C clock (200kHz if CPU is 8MHz)
    	#elif I2CDEV_IMPLEMENTATION == I2CDEV_BUILTIN_FASTWIRE
        	Fastwire::setup(400, true);
    	#endif

        // initialize device
    	mpu.initialize();

    	// load and configure the DMP
    	devStatus = mpu.dmpInitialize();

    	// set gyro offsets
	    mpu.setXGyroOffset(x_offset);
	    mpu.setYGyroOffset(y_offset);
	    mpu.setZGyroOffset(z_offset);
	    mpu.setZAccelOffset(z_accel_offset); // 1688 factory default for my test chip

	    dmpReady = false;
	    if(devStatus == 0){
	    	mpu.setDMPEnabled(true);
	    	// enable Arduino interrupt detection
	        attachInterrupt(0, dmpDataReady, RISING);
	        mpuIntStatus = mpu.getIntStatus();
	        dmpReady = true;
	        packetSize = mpu.dmpGetFIFOPacketSize();
	    }else{
	    	Serial.print(F("DMP Initialization failed (code "));
	        Serial.print(devStatus);
	        Serial.println(F(")"));
	    }
	}

	void calibrateGyro(){
		calib_done = false;
		first_meas_done = false;
		meas_count = 0;
		total_error = 0;
		prev_yaw = 0;
		curr_yaw = 0;
		while(!calib_done){
			Serial.println("calibrating");
			doFIFO(DO_CALIB);
		}
	}

	float getYPR(int type){
		doFIFO(DO_READING);
		switch(type){
			case GET_YAW: return yaw;
			case GET_ROLL: return roll;
			case GET_PITCH: return pitch;
		}
	}


	void doFIFO(int type){
		if (!dmpReady) return;
		while (!mpuInterrupt && fifoCount < packetSize) {}

		mpuInterrupt = false;
		mpuIntStatus = mpu.getIntStatus();
		fifoCount = mpu.getFIFOCount();
		if ((mpuIntStatus & 0x10) || fifoCount == 102){
			mpu.resetFIFO();
		}else if(mpuIntStatus & 0x02){
			while (fifoCount < packetSize) fifoCount = mpu.getFIFOCount();
			mpu.getFIFOBytes(fifoBuffer, packetSize);
			fifoCount -= packetSize;

			mpu.dmpGetQuaternion(&q, fifoBuffer);
			mpu.dmpGetGravity(&gravity, &q);
			mpu.dmpGetYawPitchRoll(ypr, &q, &gravity);

			if(type == DO_CALIB){
				if(!first_meas_done){
					first_meas_done = true; 
					prev_yaw = ypr[0] * 180/M_PI;
					curr_yaw = prev_yaw;
				}
				total_error += abs(curr_yaw-prev_yaw);
				prev_yaw = curr_yaw;
				curr_yaw = ypr[0] * 180/M_PI;
				meas_count++;
				if(meas_count >= 50){
					if(total_error < 1){ 
						calib_done  = true; 
					}else{
						meas_count  = 0; 
						total_error = 0;
					}
				} 
			}else{
				yaw = ypr[0] * 180/M_PI;
				pitch = ypr[1] * 180/M_PI;
				roll = ypr[2] * 180/M_PI;
			}

		}
	}
};





#endif