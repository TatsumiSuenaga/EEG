
#include <SPI.h>
#include <boards.h>
#include <RBL_nRF8001.h>

#define MESSAGE_SIZE 20

//RBL BLE sensor which transmits values from analog pin
void setup()
{  
  //sets device name
  ble_set_name("EMG_SENSOR");
  
  // Init. and start BLE library.
  ble_begin();
  
}

void loop()
{
  //construct message
  byte length = MESSAGE_SIZE;
  byte message[MESSAGE_SIZE];
  
  for(int i = 0; i < length; i+=2){
     //reads pin
     int pin_value = analogRead(3);
     
     //splits value into two bytes
     message[i] = (pin_value >> 8) & 0xFF;
     message[i+1]= pin_value & 0xFF;
  }

  //sends message
  ble_write_bytes(message, length);
  
  //complete transmittion
  ble_do_events();  
}

