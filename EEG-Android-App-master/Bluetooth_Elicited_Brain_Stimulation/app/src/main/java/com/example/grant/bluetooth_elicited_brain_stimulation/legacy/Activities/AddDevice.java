package com.example.grant.bluetooth_elicited_brain_stimulation.legacy.Activities;




import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.widget.AdapterView;
import android.view.View;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import java.util.ArrayList;
import android.os.Handler;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.bluetooth.le.ScanCallback;

import java.util.List;

import android.widget.TextView;
import android.widget.Toast;

import android.os.Build;

import android.annotation.TargetApi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.le.ScanResult;
import android.content.pm.ActivityInfo;

import com.example.grant.bluetooth_elicited_brain_stimulation.legacy.AppProperties;
import com.example.grant.bluetooth_elicited_brain_stimulation.legacy.CustomAdapters.DeviceAdapter;
import com.example.grant.bluetooth_elicited_brain_stimulation.legacy.CustomViews.FontsOverride;
import com.example.grant.bluetooth_elicited_brain_stimulation.R;

/**
 * Allows the user to find and assign a bluetooth device to a certain
 * category or "type".
 */
@TargetApi(18)
public class  AddDevice extends AppCompatActivity implements OnItemClickListener{
    private final static int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;

    // UI elements
    public DeviceAdapter deviceAdapter;
    public ImageButton refresh;
    public ListView listView;

    // BLE
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler=new Handler();
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private BluetoothGatt mGatt;
    private AppProperties app;



    /*
     * When the app first starts up this is called.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FontsOverride.setDefaultFont(this, "DEFAULT", "font/interstate_light.ttf");
        setContentView(R.layout.activity_add_device);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // initialize the variables and info for LE to work
        init();
        // start scanning for devices
        scanLeDevice(true);
    }
    /*
    * Initializes all variables
    * */
    private void init(){
        app=(AppProperties) this.getApplication();
        mBluetoothAdapter=app.getAdapter();
        //if the Build version is above 21 we use new methods
        if(Build.VERSION.SDK_INT>=21){
            mLEScanner= mBluetoothAdapter.getBluetoothLeScanner();
            settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        }
        filters = new ArrayList<ScanFilter>();

        // handles the refresh button
        refresh= (ImageButton)findViewById(R.id.refreshButton);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceAdapter.clear();
                scanLeDevice(true);
            }

        });

        deviceAdapter=new DeviceAdapter(new ArrayList<BluetoothDevice>(),this);

        listView=(ListView)findViewById(R.id.listView);
        listView.setAdapter(deviceAdapter);
        // set default text if no devices are found
        TextView empty=(TextView)findViewById(R.id.noBLEdevices);
        listView.setEmptyView(empty);

        listView.setOnItemClickListener(this);
    }

    /**
     * stops scan on a pause
     */

    @Override
    protected void onPause() {
        super.onPause();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            scanLeDevice(false);//scan is stopped
        }
    }
    /*
    * on Resume the scan is re-enabled
    */
    @Override
    protected void onResume() {
        super.onResume();
        //sends request to re-enable bluetooth if not enabled
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {//else restarts the scan
            if (Build.VERSION.SDK_INT >= 21) {
                mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();
                filters = new ArrayList<ScanFilter>();
            }
            scanLeDevice(true);
        }
    }
    /*
    * on Destroy closes gat and sends back results canceled to previous screen (this would happen for intsance if user hits back button)
    * */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGatt == null) {
            return;
        }
        mGatt.close();
        mGatt = null;

        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    /* scans for BLE devices */
    public void scanLeDevice(final boolean enable) {
        mHandler=new Handler();
        if (enable) {
            mHandler.postDelayed(new Runnable() {//stops scan after Scan_Period
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT < 21) {//if bellow 21 uses depreciated methods(must be at least 18)
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    } else {//uses api 21 non depreciated method
                        mLEScanner.stopScan(getScanCallback());

                    }
                }
            }, SCAN_PERIOD);
            //starts scanning for BLE devices
            if (Build.VERSION.SDK_INT < 21) {//if below 21 uses depreciated methods
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                System.out.println("This is the scanCallback"  + mLeScanCallback.toString());
            } else {//uses api 21 non depreciated methods
                mLEScanner.startScan(filters, settings, getScanCallback());
            }
        } else {//stops the current scan if one is happening
            if (Build.VERSION.SDK_INT < 21) {//same as previous descriptions
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

                            if (!app.inDeviceLists(device) && !deviceAdapter.contains(device)) {
                                deviceAdapter.add(device);
                                deviceAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            };

    /* gets the scan callback for use in new devices (>=21) */
    @TargetApi(21)
    private ScanCallback getScanCallback(){
        final ScanCallback mScanCallback = new ScanCallback() {
            //will add the result to the deviceAdapter's list when the a new device is found
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                BluetoothDevice btDevice = result.getDevice();
                //won't add to the list if it already contains the device or device has already been chosen and added (and not yet removed) before
                if (!app.inDeviceLists(btDevice) && !deviceAdapter.contains(btDevice)) {
                    deviceAdapter.add(btDevice);
                    deviceAdapter.notifyDataSetChanged();
                }
            }
            //onBatchScanResults logic if needed
            @Override
            public void onBatchScanResults(List<ScanResult> results) {
               //TODO
            }
            //Where scan failure logic would go if needed
            @Override
            public void onScanFailed(int errorCode) {
                //TODO
            }
        };
        return mScanCallback;
    }


    /*connects to the device and displays appropriate toast message on success or fail*/
    public void connectToDevice(BluetoothDevice device) {

        if (mGatt == null) {
            mGatt = device.connectGatt(this, false, gattCallback);
            scanLeDevice(false);// stops scan
        }
        Toast.makeText(getApplicationContext(), "Connecting to:"+device.getAddress(),
                Toast.LENGTH_LONG).show();
        if(mGatt==null){//if mGatt is null then the connection was unsuccessful
            Toast.makeText(getApplicationContext(),"Connection to "+device.getAddress()+" "+ device.getName()+" failed! Please try again." ,
                    Toast.LENGTH_SHORT).show();
        }
        else{
        Toast.makeText(getApplicationContext(), "Connection successfull",Toast.LENGTH_SHORT).show();
        }
    }

    /*Because we don't want to do anything with the gatt right now GattCallback is empty
    *however if we wanted to read or write to gatt we would define methods
    */
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {};

    /*
    * onClick for the listView adapter on this page, will add the
    * selected device to connectedDevices in appProperties on click of the listview item
    * */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        connectToDevice(deviceAdapter.getItem(position));//connects to the device at selected position

        Bundle extras = getIntent().getExtras();//gets extras defined in previous screen
        Intent intent = new Intent();

        ((AppProperties) this.getApplication()).addGatt(mGatt, (AppProperties.Type) extras.get("Type"));//adds gatt to gattmap  in app properties (in associated gattlist type)
        setResult(RESULT_OK, intent);//intent was successfull
        finish();//closes intent
    }

}