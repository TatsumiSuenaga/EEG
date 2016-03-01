
#include <SPI.h>
#include <boards.h>
#include <RBL_nRF8001.h>

#define MESSAGE_SIZE 20

void setup()
{  
  // Default pins set to 9 and 8 for REQN and RDYN
  // Set your REQN and RDYN here before ble_begin() if you need
  //ble_set_pins(3, 2);
  
  // Set your BLE Shield name here, max. length 10
  //ble_set_name("My Name");
  ble_set_name("Trigger");
  // Init. and start BLE library.
  ble_begin();
  
}

unsigned char buf[16] = {0};
unsigned char len = 0;

void loop()
{
  
  //construct message
  byte length = MESSAGE_SIZE;
  byte message[MESSAGE_SIZE];
  for(int i = 0; i < length; i+=2){
     int pin_value = analogRead(3);
     message[i] = (pin_value >> 8) & 0xFF;
     message[i+1]= pin_value & 0xFF;
  }

  ble_write_bytes(message, length);
  
  ble_do_events();
}

