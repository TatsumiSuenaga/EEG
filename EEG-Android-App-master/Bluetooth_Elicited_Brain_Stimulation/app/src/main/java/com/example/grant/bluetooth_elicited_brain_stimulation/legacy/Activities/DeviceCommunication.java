package com.example.grant.bluetooth_elicited_brain_stimulation.legacy.Activities;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.pm.ActivityInfo;
import android.graphics.Color;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.grant.bluetooth_elicited_brain_stimulation.legacy.AppProperties;
import com.example.grant.bluetooth_elicited_brain_stimulation.legacy.CustomClasses.Serialize;
import com.example.grant.bluetooth_elicited_brain_stimulation.legacy.CustomViews.FontsOverride;
import com.example.grant.bluetooth_elicited_brain_stimulation.R;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import com.example.grant.bluetooth_elicited_brain_stimulation.legacy.CustomClasses.Pair;

/*
* displays device activity to user and allow user to record displayed data to csv.
* */
public class DeviceCommunication extends AppCompatActivity {

    //Gatt Related
    private HashMap<String,ArrayList<Pair<String,Float>>> electrodeRecordings;
    private HashMap <String,ArrayList<HashMap<String,Integer>>>  transmitterRecordings;
    private BluetoothGatt recieverGatt;
    private ArrayList<Pair<AppProperties.Type,Pair<BluetoothGatt,BluetoothGatt>>> newGatts;

    //UI elements
    public AppProperties appProperties;
    public View v;

    //Booleans
    private Boolean run=true;
    private Boolean recordEnabled=false;






    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //store ui variables to app
        appProperties= (AppProperties) this.getApplication();
        setContentView(R.layout.activity_device_communication);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        v = findViewById(android.R.id.content);
        FontsOverride.setDefaultFont(this, "DEFAULT", "font/interstate_light.ttf");

        //start core app activity
        init();
    }
    public void createNewGatt(final AppProperties.Type type, final BluetoothGatt mGatt, final Object o ,final String name){
        //puts a new recordings list when an Electrodes or reciever is added
        if (type== AppProperties.Type.Electrode)
         electrodeRecordings.put(name,new ArrayList<Pair<String,Float>>());
        else if(type== AppProperties.Type.Transmitter)
            transmitterRecordings.put(name,new ArrayList<HashMap<String, Integer>>());

        final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        int count = 0;

            /*
            * this is where the logic goes for change in devices connectivity
            * */
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

            }
            /*
            * reacts to changed characteristics  in the newly created gatt. This is where we put logic
            * behind reading from devices and putting that data to the screen
            * */
            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                byte[] value = characteristic.getValue();
                int length = value.length;
                gatt.readCharacteristic(characteristic);
                // this is all electrode logic in regards to interacting with the screen
                if(type == AppProperties.Type.Electrode){
                    float sum = 0;
                    for(int i = 0; i < length;i+=2) {
                        int l = (value[i] << 8)&0xFF00;
                        int r = value[i+1]&0xFF;
                        float pin_value = l | r;
                        sum += pin_value/(length/2.0);
                        //adds name and value to electrode  recordings
                        electrodeRecordings.get(name).add(new Pair<String, Float>(new Date().toString(), pin_value));
                    }

                    // creates a time series and when the timeseries gets to 250 it starts deleting entries inside the time series
                    //this is so the graph doesn't grow too large.
                    TimeSeries series = (TimeSeries) o;
                    if (series.getItemCount() > 250) {
                        series.remove(0);
                    }
                    series.add(count/800.0, sum);//adds the new data to the the series
                    electrodeRecordings.get(name).add(new Pair<String, Float>(new Date().toString(), sum));//adds data to recordings

                    count+=20;
                }
                else if(type == AppProperties.Type.Transmitter ){
                    //TODO light indicator
                    if(recieverGatt!=null) {
                        for (int i = 0; i < length; i++){
                            //retrieves the associated characteristic from the recievers gatt
                            BluetoothGattCharacteristic character = recieverGatt.getService(UUID.fromString("8C6Bda7a-A312-681D-025B-0032C0D16A2D")).getCharacteristic(UUID.fromString("8C6Babcd-A312-681D-025B-0032C0D16A2D"));
                            byte[] test = new byte[1];//creates message to send to reciever
                            test[0] = '1';
                            character.setValue(test);//set the characteristic value to that message

                            transmitterRecordings.get(name).add(new HashMap<String, Integer>());
                            //records that the transmitter has triggered and is paired with arraylist positions of each electrode recording (beacuse recordings could be of different sizes)
                            for(Iterator <Map.Entry<String,ArrayList<Pair<String,Float>>>> it=electrodeRecordings.entrySet().iterator();it.hasNext();){
                                Map.Entry<String,ArrayList<Pair<String,Float>>> e= it.next();
                                transmitterRecordings.get(name).get(transmitterRecordings.get(name).size() - 1).put(e.getKey(),e.getValue().size()-1);
                            }
                            //recieverGatt.writeDescriptor(character.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")));
                            recieverGatt.writeCharacteristic(character);
                        }
                    }
                }
            }


            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    // first we get all of the services off the device
                    for(Iterator<BluetoothGattService> mgsi=gatt.getServices().iterator();mgsi.hasNext();) {
                        BluetoothGattService serviceT = mgsi.next();
                        //then from those services we get all the characteristics
                        for (Iterator<BluetoothGattCharacteristic> mgci = serviceT.getCharacteristics().iterator(); mgci.hasNext(); ) {
                            BluetoothGattCharacteristic character = mgci.next();
                            gatt.setCharacteristicNotification(character, true);// set that we want to be notified when characteristic is changed
                            //finaly we get all of the descriptors from the  characteristics
                            for (Iterator<BluetoothGattDescriptor> mgdi = character.getDescriptors().iterator(); mgdi.hasNext(); ) {
                                BluetoothGattDescriptor descriptor = mgdi.next();
                                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);//sends enable notification for the descriptor
                                gatt.writeDescriptor(character.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")));//this is absolutely the most important line of code never ever erase this you wont find it online anywhere
                            }
                        }
                    }


            }
            /*
            * where we put all connection change logic if needed.
            * */
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                switch (newState) {
                    case BluetoothProfile.STATE_CONNECTED:
                        gatt.discoverServices();

                        //TODO
                        break;
                    case BluetoothProfile.STATE_DISCONNECTED:
                        gatt.connect();
                        //TODO
                        break;
                    default:
                        Log.e("gattCallback", "STATE_OTHER");
                }

            }

        };
        //get the ble device from previous gatt and close the connections
        final BluetoothDevice device= mGatt.getDevice();
        mGatt.disconnect();
        mGatt.close();

        //create new gatt with the new logic (gattCallback)
        final BluetoothGatt newGatt= device.connectGatt(this, false, gattCallback);

        //store the gatt to the reciever if of type reciever (there can be only one)
        if(type == AppProperties.Type.Receiver) {
            recieverGatt = newGatt;
        }
        // new pair for storing the  old gatt and its associated replacement
        Pair mPair=new Pair<AppProperties.Type,Pair<BluetoothGatt,BluetoothGatt>>(type,new Pair<BluetoothGatt,BluetoothGatt>(mGatt,newGatt));
        newGatts.add(mPair);
        //appProperties.replaceGatt(mGatt, newGatt, type);

        //if the gatt is not connected then we continualy try to connect to it untill it gets connected
        final Thread thread=( new Thread(){
            public void run(){
                while(!newGatt.connect()){
                    Log.e("onFail","not connected");
                }
                Log.e("onSuccess","connected");
                mGatt.discoverServices();
                }});
        thread.run();
    }


    private void init(){
        //local variables
        Pair<String,String> nameAddress;
        String headerTxt;
        String nameTxt;


        //We will Insert all of our elements into the view at insert point
        final ViewGroup insertPoint = (ViewGroup) findViewById(R.id.myLayout);
        insertPoint.removeAllViews();

        //Initialize recording HashMaps. there is no need to keep track of reciever
        electrodeRecordings= new HashMap<String, ArrayList<Pair<String, Float>>>();
        transmitterRecordings= new HashMap <String,ArrayList<HashMap<String,Integer>>>();

        //Initializing variable to hold new gatts temporarily while we itterate over old ones, these will replace old gatts.
        newGatts= new ArrayList<>();

        //cycle through each type to add it to our view
        for(AppProperties.Type type: AppProperties.Type.values()){
            //if there are no  gatts of this type then we continue
            if(appProperties.getConnectedGattListSize(type)==0){
                continue;
            }
            //otherwise since this is our first gatt of this type we add a header and insert it
            headerTxt=type.name()+"s: ";
            TextView header= new TextView(this);
            header.setText(headerTxt);
            header.setTextSize(30);
            insertPoint.addView(header);

            // We cycle through each gatt of this type to add to our view
            for(Iterator <BluetoothGatt> gattIterator= appProperties.getConnectedGattIterator(type);gattIterator.hasNext();){
                final BluetoothGatt myGatt= gattIterator.next();

                nameTxt=appProperties.findAssociatedName(myGatt,type);
                TextView name= new TextView(this);
                name.setText(nameTxt);
                name.setTextSize(20);

                insertPoint.addView(name);

                //if it is an electrode we add it as a graph
                if(type == AppProperties.Type.Electrode) {
                    recordEnabled=true;
                    //adds a new linear layout to store the graph view
                    final LinearLayout linearLayout=new LinearLayout(this);

                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    linearLayout.setMinimumHeight(500);
                    linearLayout.setMinimumWidth(25);

                    insertPoint.addView(linearLayout);

                    //local variables specific to electrodes
                    final TimeSeries series = new TimeSeries("test");
                    final XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
                    final XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
                    final XYSeriesRenderer renderer = new XYSeriesRenderer();
                    final GraphicalView mChart = ChartFactory.getLineChartView(this, dataset, mRenderer);

                    dataset.addSeries(series);

                    //set color and settings of renderer
                    mRenderer.addSeriesRenderer(renderer);
                    mRenderer.setApplyBackgroundColor(true);
                    mRenderer.setBackgroundColor(Color.WHITE);
                    mRenderer.setZoomButtonsVisible(false);
                    mRenderer.setInScroll(true);

                    mRenderer.setShowGrid(true);
                    mRenderer.setLabelsColor(Color.WHITE);
                    mRenderer.setGridColor(Color.RED);
                    mRenderer.setShowAxes(false);
                    mRenderer.setZoomEnabled(false, false);
                    mRenderer.setPanEnabled(false, false);
                    linearLayout.addView(mChart);

                    //creates a new gatt which will interact with the view
                    createNewGatt(type, myGatt, series,nameTxt);

                    // this thread will update the graph after a specified period of time
                    final Thread thread=( new Thread(){
                        public void run(){
                           while(run){
                                try {
                                    Thread.sleep(1000/60L);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                mChart.repaint();
                           }

                        }
                    });
                    thread.start();
                }
                else if(type== AppProperties.Type.Transmitter) {//TODO
                    //if it is a transmitter we add a new transmitter to the view
                    final TextView state= new TextView(this);
                    state.setTextSize(15);
                    insertPoint.addView(state);
                    // create new gatt to interact with the app
                    createNewGatt(type, myGatt, state,nameTxt);


                } else if(type == AppProperties.Type.Receiver){
                    //if it is a reciever then we add it to the view
                    final TextView state= new TextView(this);
                    state.setTextSize(15);
                    insertPoint.addView(state);
                    //and create a new Gatt to pipe the transmitter to
                    createNewGatt(AppProperties.Type.Receiver, myGatt, state,nameTxt);

                }

            }
        }
        //if record is enabled that means that you had at least one electrode to record from
        if(recordEnabled){
            final Button recordButton=new Button(this);
            recordButton.setText("Record");
            recordEnabled=false;
            recordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if record is enabled then we need to stop recording and serialize recordings to a csv
                    if(recordEnabled){
                        recordEnabled=false;
                        Serialize serialize= new Serialize(getApplicationContext());
                        serialize.writeToCSV(electrodeRecordings,transmitterRecordings);
                        Toast.makeText(getApplicationContext(), "Data has been written to CSV",
                                Toast.LENGTH_SHORT).show();
                        recordButton.setText("Record");
                        //then we clear the set for electrodes and transmitters to get ready for a new recording
                        for(Map.Entry<String,ArrayList<Pair<String,Float>>> e:electrodeRecordings.entrySet()){
                            e.getValue().clear();
                        }
                        for(Map.Entry<String,ArrayList<HashMap<String,Integer>>> e:transmitterRecordings.entrySet()){
                            e.getValue().clear();
                        }

                    }
                    //otherwise we start recording and start storing data to electrode and transmitter recordings
                    else{
                        recordEnabled=true;
                        recordButton.setText("Stop Recording");

                    }


                }
            });
            //and we finally add record button
            insertPoint.addView(recordButton);

        }
        //replaces all old gatts with the newly created gatts
        for(Pair<AppProperties.Type, Pair<BluetoothGatt, BluetoothGatt>> p: newGatts){
            appProperties.replaceGatt(p.second.first,p.second.second,p.first);
        }
    }
    //if back is pressed we destroy the connections between the app and the devices
    @Override
    public void onBackPressed() {
        run=false;
        setResult(RESULT_CANCELED);
        appProperties.disconnectAllConnected();
        finish();


    }


}
