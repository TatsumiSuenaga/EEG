#include <BLEduino.h>

//Console Module Code
//Write and receive character strings
#include <BLEduino.h>;
#include <math.h>

#define MESSAGE_SIZE 20
BLEduino BLE;
 
 float at;
 
void setup(){
  at = 0;
  BLE.begin(); //Initialize BLE object
  BLE.sendCommand(COMMAND_RESET); //Start advertising BLEduino
}
 
void loop(){

  //Send message to BLEduino
  if(BLE.isConnected()){
     byte length = MESSAGE_SIZE;
     byte message[MESSAGE_SIZE];
     for(int i = 0; i < MESSAGE_SIZE; i++){
      
        int pin_value = 127*sin(at/(2000.0) * (2.0 * PI));
         message[i] = (pin_value >> 8) & 0xFF;
         message[i+1]= pin_value & 0xFF;
         if(at==2000){
           at = 0; 
         }else{
           at++; 
         }
     }
     BLE.sendData(UART_SEND, message, length);
   }
}
 

