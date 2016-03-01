#include <BLEduino.h>
#define DELAY 5
#define PULSE 200

BLEduino BLE;
int prev; 


//Transmitter test code for BLEduino
//fires a 'pulse' of triggered messages and waits for a DELAY seconds.

//sets up device and restarts BLE communication
void setup(){
  prev = 0;
  pinMode(BLEDUINO_LED, OUTPUT);
  BLE.begin(); //Initialize BLE object
  BLE.sendCommand(COMMAND_RESET); //Start advertising BLEduino
}

void loop(){

 
  if(BLE.isConnected()){
    
      //transmit pulse to UART service, Tx characteristic
       
      digitalWrite(BLEDUINO_LED, 1);
        
      for(int i = 0; i < PULSE; i++){
        byte length = 1;
        byte message[1];
        message[0] = 1;

        BLE.sendData(UART_SEND, message, length);
      }
      
      digitalWrite(BLEDUINO_LED, 0);
      
      //wait until next pulse
      delay(1000*DELAY);
    }
}



