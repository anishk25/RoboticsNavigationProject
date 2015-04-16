#define LED_PIN 13

#define ANDROID_MESSAGE_LENGTH 4

#define IDLE_COLOR_PAPER_STATE   0
#define SEARCH_COLOR_PAPER_STATE 1
#define DONE_COLOR_PAPER_STATE   2

char incomingAndroidMsg[ANDROID_MESSAGE_LENGTH];
int curr_state;

void setup(){
  Serial.begin(9600);
  pinMode(LED_PIN,OUTPUT); 
  curr_state = IDLE_COLOR_PAPER_STATE; 
}
void loop(){
  if(Serial.available()){
      Serial.readBytes(incomingAndroidMsg,ANDROID_MESSAGE_LENGTH);
      if(strcmp(incomingAndroidMsg,"STRT")){
          curr_state = SEARCH_COLOR_PAPER_STATE;
      }else if(strcmp(incomingAndroidMsg,"STOP")){
          curr_state = DONE_COLOR_PAPER_STATE;
      }
  }
  ledBehavior();
}

void ledBehavior(){
   if(curr_state == SEARCH_COLOR_PAPER_STATE){
      analogWrite(LED_PIN,50); 
   }else if(curr_state == DONE_COLOR_PAPER_STATE){
      digitalWrite(LED_PIN,HIGH);
   }
}


