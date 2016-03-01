
#include <SPI.h>
#include <boards.h>
#include <RBL_nRF8001.h>

#define MESSAGE_SIZE 20

void setup()
{  
  //set device name
  ble_set_name("Recv");
  //start BLE library.
  ble_begin();
  
}

int count = 0;

void loop()
{
  
  
  //Checks if a message is waiting
  if(ble_read()!=-1){
    //set chat out 
    
    //lights up
    digitalWrite(13, 1); 
    count = 20000;
  }
  
  //light counter
  if(count==0){
   digitalWrite(13, 0); 
  }else{
   count--; 
  }
}

