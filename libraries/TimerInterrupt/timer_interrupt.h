#ifndef TIMER_INTERRUPT_H
#define TIMER_INTERRUPT_H

#if defined(ARDUINO) && ARDUINO >= 100
#include "Arduino.h"
#endif
    
    int getMatchRegister(int freq,int preScaler){
        return (16000000/(preScaler*freq))-1;
    }

	void setupTimer(int frequency,int preScaler){
        int matchRegister = getMatchRegister(frequency,preScaler);
		cli(); // stop interrups
   		TCCR1A = 0;// set entire TCCR1A register to 0
  		TCCR1B = 0;// same for TCCR1B
  	    TCNT1  = 0;//initialize counter value to 0
  	    OCR1A = matchRegister;
  	    TCCR1B |= (1 << WGM12);

        switch(preScaler){
        	case 1: 
        		TCCR1B |= (1 << CS10);
        		break;
        	case 8: 
        		TCCR1B |= (1 << CS11);
        		break;
        	case 64:
        		TCCR1B |= (1 << CS11) | (1 << CS10);
        		break;
        	case 256:
        	    TCCR1B |= (1 << CS12);
        	    break;
        	case 1024: 
        		TCCR1B |= (1 << CS12) | (1 << CS10);
        		break;

        }
  	    TIMSK1 |= (1 << OCIE1A);
   		sei();//allow interrupts
  	}



#endif