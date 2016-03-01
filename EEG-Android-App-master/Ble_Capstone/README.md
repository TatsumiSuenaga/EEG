# Capstone BLEduino Hardware Code

## Production

### BLEduino
* [Transmitter](transmitter/transmitter.ino)
* [Receiver](receiver/receiver.ino )
* [Sensor](sensor_analog/sensor_analog.ino )

### RBL
* [Transmitter](RBLTransmitter/RBLTransmitter.ino)
* [Receiver](RBLReceiver/RBLReceiver.ino )
* [Sensor](RBLAnalogSensor/RBLAnalogSensor.ino )

## Testing

### BLEduino
* [Transmitter Pulse](transmitter_test/transmitter_test.ino)
* [Sensor Sine Generator](sensor/sensor.ino)

### RBL
* [Sensor Step Generator](RBLStepSensorTest/RBLStepSensorTest.ino)
* [Sensor Ramp Wave Generator](RBLRampWaveSensor/RBLRampWaveSensor.ino )

##Setup
Most of the setup information is taken from: http://bleduino.cc/start/intro/
However, we're not sure how long that site will stay up and running, so we'll copy the important parts here.

###1. Install Arduino IDE
 * To program the BLEduino, you’ll need the latest non-beta (1.0.5), Arduino development environment.
 * https://www.arduino.cc/en/Main/OldSoftwareReleases OR we already have it in the repo as "arduino-1.0.5-macosx.zip" if you're using a mac

###2. Install the BLEduino Driver
The next thing you'll need to do is install the proper BLEduino drivers. This process is Plug and Play on Mac OS and Linux but slightly more complicated on Windows.

####MAC
The first time you plug in the BLEduino on Mac, the "Keyboard Setup Assistant" will launch. This is due to the OS thinking the BLEduino is a keyboard (which technically, it could be). There's nothing to configure, so you can close this dialogue box.

####LINUX
Just plug and play. Nothing to do here!

####WINDOWS
The following instructions are for Windows 7 (and XP with slight variations) only.
If you are using Windows 8, your instructions can be found here[http://bleduino.cc/start/win8/].
First, download and extract this zip[blueduino_driver_1.1_for_windows.zip] somewhere easy to access, like your desktop.

Second, plug in your board and wait for Windows to begin its driver installation process. If the installer does not launch automatically, you'll have to navigate to the Windows Device Manager and find the BLEduino (USB IO Board) listing manually.

![Alt text](https://github.com/weil41/BLE_Capstone/blob/master/readmeScreenshots/screenshotdm.png)

Choose "Browse my computer for driver software", and click Next.

![Alt text](https://github.com/weil41/BLE_Capstone/blob/master/readmeScreenshots/stepwin1.png)

Click the "Browse..." button. Navigate to the extracted BLEduino driver folder you downloaded. Click OK, then click Next.

![Alt text](https://github.com/weil41/BLE_Capstone/blob/master/readmeScreenshots/stepwin2.png)

Windows will try to install the driver but will complain about the driver being unsigned. It’s safe to select ‘Install this driver software anyway’ on the warning dialog.

![Alt text](https://github.com/weil41/BLE_Capstone/blob/master/readmeScreenshots/stepwin3.png)

The Driver is installed! Note the COM port used at the top. (In this case it's COM 3) This will be useful when uploading code to the BLEduino.

###3. BLEduino in Board List
* download the "bleduino_hardware_1.1.zip" file

To get the IDE to recognize the BLEduino and properly upload code to it, you need to tell the IDE that the board you’ll be programming is a BLEduino. However, if you try to pick the BLEduino from the board list, you'll find it's not there.

![Alt text](https://github.com/weil41/BLE_Capstone/blob/master/readmeScreenshots/screenshotnb.png)

To add the BLEduino to the list, download the zip folder mentioned at the beginning of this section and put it inside your Arduino sketchbook. The Arduino sketchbook is usually found in your home directory (Documents in Windows). To double check, you can go to File > Preferences within the Arduino IDE and check the Sketchbook location text box. With this location you simply just drop the unzipped Hardware folder into the Arduino folder.

![Alt text](https://github.com/weil41/BLE_Capstone/blob/master/readmeScreenshots/screenshotl.png)

Now, when you open the Arduino IDE, you should see the BLEduino inside the board list.

![Alt text](https://github.com/weil41/BLE_Capstone/blob/master/readmeScreenshots/screenshotnb2.png)

###4. Using the BLEduino Library
* Download the bleduino library "bleduino_library_1.1.zip"

The BLEduino comes with a BLE library that allows you to easily send and receive data via BLE. The BLEduino can still be programmed without the library but it won’t be able to use its BLE functionalities without it. Importing the library and examples is fairly straightforward. You first download the library. Then you import it into Arduino via Sketch > Import Library > Add Library.

## Sensor Message Structure
The value of a arduino analog pin read is a 10 bit unsigned integer. This creates a problem as BLE only allows messages of single bytes to be transmitted. To circumvent this the pin value is seperated into the most and least significant bytes, and then transmitted seperatly. They are then combined by the mobile device upon receiving them. This does mean that every pin read transmits 6 empty bits. The rational for leaving these bits empty is to allow for variable sized messages.