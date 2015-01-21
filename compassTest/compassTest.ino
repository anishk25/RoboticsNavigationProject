#define M_PI 3.141592
#include "Wire.h"
#include "I2Cdev.h"
#include "HMC5843.h"

HMC5843 mag;
int16_t mx, my, mz;

void setup() {
    // join I2C bus (I2Cdev library doesn't do this automatically)
    Wire.begin();
    Serial.begin(9600);

    // initialize device
    Serial.println("Initializing I2C devices...");
    mag.initialize();

    // verify connection
    Serial.println("Testing device connections...");
    Serial.println(mag.testConnection() ? "HMC5843 connection successful" : "HMC5843 connection failed");
    Serial.println("done");
}

void loop() {
    // read raw heading measurements from device
    mag.getHeading(&mx, &my, &mz);
    
    float heading = atan2(my,mx);
    float declinationAngle = 0.174f;
    heading += declinationAngle;
    
    if(heading < 0){
       heading += 2*M_PI; 
    }
    
    if(heading > 2*M_PI){
       heading -= 2*M_PI; 
    }
    
    float headingDegrees = heading * 180/M_PI;
    Serial.print("Heading (degrees): "); Serial.println(headingDegrees);

    

    // display tab-separated gyro x/y/z values
   /* Serial.print("mag:\t");
    Serial.print(mx); Serial.print("\t");
    Serial.print(my); Serial.print("\t");
    Serial.println(mz);*/


}
