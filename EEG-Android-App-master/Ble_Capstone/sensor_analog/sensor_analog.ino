#include <BLEduino.h>
#define MESSAGE_SIZE 20

BLEduino BLE;
int prev; 
//Transmitter code for BLEduino
//Scans analog pin 3. When pin 3 is grounded it transmits

//sets up device and restarts BLE communication
void setup(){
  prev = 0;
  pinMode(BLEDUINO_LED, OUTPUT);
  BLE.begin(); //Initialize BLE object
  BLE.sendCommand(COMMAND_RESET); //Start advertising BLEduino
}

void loop(){

   //checks if a client is connected
  if(BLE.isConnected()){
    
      //construct message
      byte length = MESSAGE_SIZE;
      byte message[MESSAGE_SIZE];
      for(int i = 0; i < length; i+=2){
         int pin_value = analogRead(3);
         message[i] = (pin_value >> 8) & 0xFF;
         message[i+1]= pin_value & 0xFF;
      }
      
      BLE.sendData(UART_SEND, message, length);
      
      digitalWrite(BLEDUINO_LED, 1);
      
  }
}



