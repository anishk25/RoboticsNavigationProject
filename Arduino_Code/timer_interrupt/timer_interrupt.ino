int ledPin = 13;
boolean toggle1 = 0;


void setup(){
   pinMode(13, OUTPUT);
   Serial.begin(9600);
   cli(); // stop interrups
   TCCR1A = 0;// set entire TCCR1A register to 0
   TCCR1B = 0;// same for TCCR1B
   TCNT1  = 0;//initialize counter value to 0
   // set compare match register for 100hz increments
   // prescaler is 64
   OCR1A = 15624;// = (16*10^6) / (100*256) - 1 (must be <65536 and >256)
   // turn on CTC mode
   TCCR1B |= (1 << WGM12);
  
   // Set CS10 and CS12 bits for 64 prescaler
   TCCR1B |= (1 << CS12) | (1 << CS10); 
   TIMSK1 |= (1 << OCIE1A);
   sei();//allow inter1upts
}

ISR(TIMER1_COMPA_vect){
 Serial.println("INTERRUPT!!");
 if (toggle1){
    digitalWrite(13,HIGH);
    toggle1 = 0;
  }
  else{
    digitalWrite(13,LOW);
    toggle1 = 1;
  }
}

void loop(){
  
}
