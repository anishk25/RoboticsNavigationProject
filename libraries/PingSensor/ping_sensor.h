#ifndef PING_SENSOR_H
#define PING_SENSOR_H

#if defined(ARDUINO) && ARDUINO >= 100
	#include "Arduino.h"
#endif

class PingSensor{
private:
	int pingPin;
public:
	PingSensor(int ping_pin){
		this->pingPin = ping_pin;
	}

	float getDistance(){
		// delay tp prevent from sampling too much
		delay(30);
		long duration;
		float dist_cm;
		
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

	float microSecondsToCm(long microseconds){
		return ((float)microseconds) / 29 / 2; 
	}

};

#endif