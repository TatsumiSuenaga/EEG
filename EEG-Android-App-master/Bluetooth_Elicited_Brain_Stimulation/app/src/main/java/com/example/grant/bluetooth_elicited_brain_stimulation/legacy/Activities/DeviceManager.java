package com.example.grant.bluetooth_elicited_brain_stimulation.legacy.Activities;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.example.grant.bluetooth_elicited_brain_stimulation.legacy.AppProperties;
import com.example.grant.bluetooth_elicited_brain_stimulation.legacy.CustomAdapters.PairAdapter;
import com.example.grant.bluetooth_elicited_brain_stimulation.legacy.CustomViews.FontsOverride;
import com.example.grant.bluetooth_elicited_brain_stimulation.legacy.CustomViews.NonScrollableListView;
import com.example.grant.bluetooth_elicited_brain_stimulation.legacy.CustomClasses.Pair;
import com.example.grant.bluetooth_elicited_brain_stimulation.R;
import com.example.grant.bluetooth_elicited_brain_stimulation.legacy.CustomClasses.Serialize;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* The main activity. Presents a screen to select different device categories to run */
public class DeviceManager extends AppCompatActivity{

    // UI elements
    public Map<AppProperties.Type,ImageButton> addButtons;
    public  Map<AppProperties.Type,NonScrollableListView> views;
    public Map<AppProperties.Type,PairAdapter> pairAdapters;
    public ImageButton proceedButton;
    public TextView textView;
    public Serialize serialize;
    public Context context;
    private AppProperties appProperties;

    //Constants
    private static int STATIC_INT=1;
    private static int SCAN_PERIOD=10000;

    //BLE
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private ArrayList<ScanFilter> filters;
    private BluetoothGatt mGatt=null;
    private android.os.Handler mHandler = new android.os.Handler();

    //Booleans
    private Boolean connect=true;


    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FontsOverride.setDefaultFont(this, "DEFAULT", "font/interstate_light.ttf");
        setContentView(R.layout.activity_device_manager);
        textView= (TextView)findViewById(R.id.profile_name);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
        startScanThread();
    }
    /*initializes constants */
    private void init(){
        appProperties=(AppProperties) this.getApplication();
        textView.setText(appProperties.getCurrentProfileName());
        serialize= new Serialize(this.getApplicationContext());
        context= this;

        //Initializes local maps as HashMaps
        addButtons= new HashMap<AppProperties.Type,ImageButton>();
        views=new HashMap<AppProperties.Type,NonScrollableListView>();
        pairAdapters=new HashMap<AppProperties.Type,PairAdapter>();

        //get all add buttons from view
        addButtons.put(AppProperties.Type.Electrode, (ImageButton) findViewById(R.id.addElectrodeButton));
        addButtons.put(AppProperties.Type.Transmitter, (ImageButton) findViewById(R.id.addTransmitterButton));
        addButtons.put(AppProperties.Type.Receiver, (ImageButton) findViewById(R.id.addRecieverButton));
        proceedButton= (ImageButton) findViewById(R.id.proceedButton);


        //get all views from view
        views.put(AppProperties.Type.Electrode, (NonScrollableListView) findViewById(R.id.electrodesView));
        views.put(AppProperties.Type.Transmitter, (NonScrollableListView) findViewById(R.id.transmittersView));
        views.put(AppProperties.Type.Receiver, (NonScrollableListView) findViewById(R.id.recieversView));

        //Initializes bluetoothAdapter,scanner, and filters
        mBluetoothAdapter=appProperties.getAdapter();
        if (Build.VERSION.SDK_INT >= 21) {
            mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
            settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
        }
        filters = new ArrayList<ScanFilter>();


        //sets  properties for adapters, App properties hashmaps, views, and onClick listners by type (as defined in enums in AppProperties)
        for (final AppProperties.Type type : AppProperties.Type.values()){
            pairAdapters.put(type, new PairAdapter(appProperties.getNameAddressListCopy(type), this, appProperties, type));

            addButtons.get(type).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), AddDevice.class);
                    i.putExtra("Type", type);
                    connect = false;
                    startActivityForResult(i, STATIC_INT);
                }
            });
            views.get(type).setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Pair<String, String> p = pairAdapters.get(type).getItem(position);
                    BluetoothGatt mg = null;
                    Boolean success = false;

                    //searches for associated gatt by it's address
                    for (Iterator<BluetoothGatt> it = appProperties.getConnectedGattIterator(type); it.hasNext(); ) {
                        mg = it.next();
                        if (mg.getDevice().getAddress().equals(p.second)) {
                            success = true;
                            break;
                        }
                    }
                    //if it is successfull it will pull up a box to test communication with that device
                    if (success) {
                        if (mg.connect() && mg.discoverServices()) {
                            BluetoothGattService x = null;
                            BluetoothGattCharacteristic y = null;
                            for (Iterator<BluetoothGattService> mgsIt = mg.getServices().iterator(); mgsIt.hasNext(); x = mgsIt.next()) {
                                for (Iterator<BluetoothGattCharacteristic> mscIt = x.getCharacteristics().iterator(); mscIt.hasNext(); ) {
                                    Toast.makeText(getApplicationContext(), "Characteristic:" + y.getStringValue(1),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }


                        }
                    }
                }
            });
            proceedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean userIssue = false;
                    startScanThread();
                    // make sure there's at least one device connected
                    if (appProperties.getConnectedGattListSize(AppProperties.Type.Transmitter) == 0 &&
                            appProperties.getConnectedGattListSize(AppProperties.Type.Receiver) == 0 &&
                            appProperties.getConnectedGattListSize(AppProperties.Type.Electrode) == 0) {
                        Toast.makeText(getApplicationContext(), "Connect a BLE Device.",
                                Toast.LENGTH_LONG).show();
                        userIssue = true;
                    }
                    // if there's a receiver, make sure there's at least one transmitter
                    if (appProperties.getConnectedGattListSize(AppProperties.Type.Receiver) > 0) {
                        if (appProperties.getConnectedGattListSize(AppProperties.Type.Transmitter) < 1) {
                            Toast.makeText(getApplicationContext(), "There must be a transmitter to trigger the receiver.",
                                    Toast.LENGTH_LONG).show();
                            userIssue = true;
                        }
                    }
                    // if there's a transmitter, make sure there's one receiver
                    if (appProperties.getConnectedGattListSize(AppProperties.Type.Transmitter) > 0) {
                        if (appProperties.getConnectedGattListSize(AppProperties.Type.Receiver) != 1) {
                            Toast.makeText(getApplicationContext(), "There must be a receiver for the transmitter to trigger.",
                                    Toast.LENGTH_LONG).show();
                            userIssue = true;
                        }
                    }

                    if (!userIssue) {
                        if (!appProperties.connectedMatchesNameAdress()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            TextView tv= new TextView(context);
                            tv.setText("Some devices are not connected are you sure you want to proceed?");
                            builder.setView(tv);
                            builder.setTitle("Alert");
                            // Set up the buttons
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    goToDeviceCommunication(type);
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        }
                        else {
                            goToDeviceCommunication(type);
                        }
                    }
                }
            });


            views.get(type).setAdapter(pairAdapters.get(type));
        }
    }
    public void goToDeviceCommunication(AppProperties.Type type){
        scanLeDevice(false);
        Intent i = new Intent(getApplicationContext(), DeviceCommunication.class);
        connect=false;
        startActivityForResult(i, STATIC_INT);
    }
    /* scans for BLE devices */
    public void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //stops the current scan if one is happening
                    if (Build.VERSION.SDK_INT < 21) {//if bellow 21 uses depreciated methods(must be at least 18)
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    } else {//uses api 21 non depreciated method
                        mLEScanner.stopScan(getScanCallback());
                    }
                }
            }, SCAN_PERIOD);
            //starts scanning for BLE devices
            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                mLEScanner.startScan(filters, settings, getScanCallback());
            }
        } else {
            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            } else {
                mLEScanner.stopScan(getScanCallback());
            }
        }
        invalidateOptionsMenu();
    }

    /* Asynchronous process that will be called each time a device is discovered
     * for older devices (<21) */
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi,
                                     final byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (final AppProperties.Type type : AppProperties.Type.values()) {
                                ArrayList<Pair<String,String>>myList = appProperties.getNameAddressListCopy(type);
                                for(int x=0;x<myList.size();x++){
                                    if( myList.get(x).second.equals(device.getAddress())&&!appProperties.isInConnectedAdresses(device.getAddress(),type)){
                                        connectToDevice(device,type);
                                        break;
                                    }
                                }
                            }
                        }
                    });
                }
            };



    /* gets the scan callback for use in new devices (>=21) */
    @TargetApi(21)
    private ScanCallback getScanCallback() {

        final ScanCallback mScanCallback = new ScanCallback() {
            //will add the result to the deviceAdapter's list when the a new device is found
            @Override
            public void onScanResult(int callbackType, ScanResult result) {

                BluetoothDevice btDevice = result.getDevice();
                //won't add to the list if it already contains the device or device has already been chosen and added (and not yet removed) before
                for (final AppProperties.Type type : AppProperties.Type.values()) {
                   ArrayList<Pair<String,String>>myList = appProperties.getNameAddressListCopy(type);
                    for(int x=0;x<myList.size();x++){
                        if( myList.get(x).second.equals(btDevice.getAddress()) && !appProperties.isInConnectedAdresses(btDevice.getAddress(),type)){
                            connectToDevice(btDevice,type);
                            break;
                        }
                    }
                }
            }

        };
        return mScanCallback;
    }


    //connects to the device and displays appropriate toast message on success or fail
    public void connectToDevice(BluetoothDevice device,AppProperties.Type type) {
        mGatt = device.connectGatt(this, false, gattCallback);
        if (mGatt != null) {
            appProperties.addGatt(mGatt, type);
            mGatt.connect();
        }
    }
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {};

    public void startScanThread(){
        connect=true;
        if(thread==null||!thread.isAlive()){
       thread = new Thread(new Runnable() {
            public void run(){
                while(true) {
                    if(!connect)
                        break;
                    connect=!appProperties.connectedMatchesNameAdress();
                    scanLeDevice(connect);
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        //TODO
                    }
                }
            }
        });
        thread.start();
        }
    }
    public void stopScanThread(){
        connect=false;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        for (AppProperties.Type type : AppProperties.Type.values()) {
            pairAdapters.get(type).updateNameAddressList(appProperties.getNameAddressListCopy(type));
        }
        startScanThread();
        serialize.writeProfileToFile(true);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        stopScanThread();
        startScanThread();
        //TODO
    }
    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        stopScanThread();
        appProperties.disconnectAllConnected();
        finish();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        stopScanThread();
        appProperties.disconnectAllConnected();
        //TODO

    }

}
