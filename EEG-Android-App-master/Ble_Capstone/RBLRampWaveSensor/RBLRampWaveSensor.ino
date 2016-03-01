
#include <SPI.h>
#include <boards.h>
#include <RBL_nRF8001.h>

#define MESSAGE_SIZE 20

//RBL Test program. Sends ramp wave

void setup()
{  
 //sets device name
  ble_set_name("EMG_SENSOR");
  //start BLE library.
  ble_begin();
  
}

int at = 0;
void loop()
{
 
  //construct message
  byte length = MESSAGE_SIZE;
  byte message[MESSAGE_SIZE];
  
  for(int i = 0; i < length; i+=2){
     int pin_value = 0;
     if(at<500){
         pin_value = (at/500.0) * 1024;
     }else if(at<1000){
        pin_value = 1024 - 1024 *((at-500)/500.0); 
     }else{
       at = 0;
     }
     //split value into seperate bytes
     message[i] = (pin_value >> 8) & 0xFF;
     message[i+1]= pin_value & 0xFF;
     at++;
  }

  //send message
  ble_write_bytes(message, length);
  
  //complete transmittion
  ble_do_events();
}

