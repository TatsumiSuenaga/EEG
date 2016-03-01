#include <BLEduino.h>

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
    //reads analog pin 3 (A3)
    int val = analogRead(3); 
    
    //if grounded 
    //Sends 1 via UART service, TX characteristic 
    //turns on LED1
    if(val == 0){
      
      //construct message
      byte length = 1;
      byte message[1];
      message[0] = 1;
      
      
      BLE.sendData(UART_SEND, message, length);
      
      digitalWrite(BLEDUINO_LED, 1);
      
      prev = 1;
    }else if(prev == 1){
      //once A3 is ungrounded sends terminal 0 via UART service, TX characteristic 
      //turns off LED1
      prev = 0;
      
      //construct message
      byte length = 1;
      byte message[1];
      message[0] = 0;


      BLE.sendData(UART_SEND, message, length);
      
      digitalWrite(BLEDUINO_LED, 0);
    }
  }
}



