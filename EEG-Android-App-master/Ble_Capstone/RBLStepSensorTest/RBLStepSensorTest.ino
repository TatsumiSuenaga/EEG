
#include <SPI.h>
#include <boards.h>
#include <RBL_nRF8001.h>

#define MESSAGE_SIZE 20


//RBL BLE sensor test. Generates step signal
void setup()
{  
  //Set device name
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
     if(at>=5000){
       at = 0;
     }
     int pin_value = 10*(at/500);
      
     //splits bytes into pairs
     message[i] = (pin_value >> 8) & 0xFF;
     message[i+1]= pin_value & 0xFF;
     at++;
  }

  //sends message
  ble_write_bytes(message, length);
  
  //completes transmittion
  ble_do_events();
}

