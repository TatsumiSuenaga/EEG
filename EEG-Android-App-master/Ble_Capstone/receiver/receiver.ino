#include <BLEduino.h>

//Reciever code for BLEduino
//Waits for 1 via UART Rx
//Upon recieve lights up, signals CHAT


BLEduino BLE;
int count;
 
//sets up device and restarts BLE communication
void setup(){
  count = 0;
  pinMode(BLEDUINO_LED, OUTPUT);
  BLE.begin(); //Initialize BLE object
  BLE.sendCommand(COMMAND_RESET); //Start advertising BLEduino
}
 
void loop(){

  //Checks if a message is waiting in UART Rx
  if(BLE.available(UART_READ)){
    BLEPacket packet = BLE.read(UART_READ);
    
    //translates message
    uint8_t length = packet.length;
    uint8_t * data = packet.data;
    
     for(int i = 0; i < length; i++){
       if((char)data[i] == '1'){
         //TODO
         //insert CHAT analog pin write
         //analogWrite(PIN,VALUE);
   
         digitalWrite(BLEDUINO_LED, 1);
         count = 20000;
       }
     }
  }
  
  //light counter
  if(count==0){
   digitalWrite(BLEDUINO_LED, 0); 
  }else{
   count--; 
  }
}
 

