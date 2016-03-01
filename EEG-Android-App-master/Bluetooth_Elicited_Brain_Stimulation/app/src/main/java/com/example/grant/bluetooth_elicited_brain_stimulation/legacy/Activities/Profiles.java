package com.example.grant.bluetooth_elicited_brain_stimulation.legacy.Activities;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.grant.bluetooth_elicited_brain_stimulation.legacy.AppProperties;
import com.example.grant.bluetooth_elicited_brain_stimulation.legacy.CustomAdapters.FileAdapter;
import com.example.grant.bluetooth_elicited_brain_stimulation.legacy.CustomViews.NonScrollableListView;
import com.example.grant.bluetooth_elicited_brain_stimulation.legacy.CustomClasses.Pair;
import com.example.grant.bluetooth_elicited_brain_stimulation.R;
import com.example.grant.bluetooth_elicited_brain_stimulation.legacy.CustomClasses.Serialize;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
/*
* Where user creates/deletes/loads profiles
* */
public class Profiles extends AppCompatActivity {
    // UI elements
    public NonScrollableListView profileView;
    public FileAdapter profileAdapter;
    public ImageButton addButton;
    public Context context;
    public AppProperties appProperties;

    public Serialize serialize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);
        //UI elements and app properties set
        context= this;
        appProperties = (AppProperties) this.getApplication();
        profileView = (NonScrollableListView) findViewById(R.id.profilesView);
        addButton= (ImageButton) findViewById(R.id.addButton);

        init();
    }

    /*
     *creates associated app folders if they don't exist and delclares variables
     */
    private void init() {

        //makes the save directories if they don't exist
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/bebs/profiles");
        File secondaryPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/bebs/test_results");
        if (!path.exists())
            path.mkdirs();
        if (!secondaryPath.exists())
            secondaryPath.mkdirs();


        //Variable declaration
        profileAdapter= new FileAdapter(this,appProperties,path);
        profileView.setAdapter(profileAdapter);
        profileView.setEnabled(true);
        profileView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        serialize= new Serialize(this);

        //Sets listeners
        profileView.setOnItemClickListener(new OnItemClickListener() {//set on click of profile view
            /*
            * on item click loads a profile and proceeds to device manager activity screen
            * */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                load(profileAdapter.getItem(position));
                Intent i = new Intent(Profiles.this, DeviceManager.class);
                startActivity(i);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {//sets add buttons on click listner
            /*
            * on click creates a new alert box to add and name profile
            * */
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Name Profile");


                // Set up the input
                final EditText input = new EditText(context);
                // Specify the type of input expected;
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    /*
                    * on click creates profile with given name of input
                    */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        create(input.getText().toString());
                        profileAdapter.updateFileList();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    /*
                    * close dialogue box and doesn't add profile
                    * */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

    }

/*
* creates new profile with given name and empty devices hashmap
*
* */
    private void create(String name) {
        appProperties.resetConnectedDevices();
        appProperties.setCurrentProfileName(name);
        Serialize serialize= new Serialize(this);
        for (final AppProperties.Type type : AppProperties.Type.values()) {
            appProperties.setNamesAndAddresses(new ArrayList<Pair<String, String>>(), type);
        }
        serialize.writeProfileToFile(false);
    }
    /*
  * Checks if the the device you are using is ble (compatible)/ready and if not give appropriate options to enable
   * (or closes app if not compatable)
  * */
    public boolean isBLEReady(){
        boolean isReady=true;
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            // Device does not support Bluetooth
            new AlertDialog.Builder(this)
                    .setTitle("Incompatible Device")
                    .setMessage("Your phone does not support BluetoothLE")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else if (!appProperties.getAdapter().isEnabled()) {
            //Device needs to enable its bluetooth so preform an action request
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
            isReady = false;
        }
        return isReady;
    }


    /*
    * Loads given (profile) file properties to appProperties
    * */
    private void load(File myFile) {
        //clears all data that is currently in  app properties and checks if device is ble ready
        appProperties.resetConnectedDevices();
        appProperties.setCurrentProfileName(myFile.getName());
        appProperties.setMyBluetoothAdapter(((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter());
        isBLEReady();

        try{// tries to load file
            FileInputStream fis = new FileInputStream(myFile);
            ObjectInputStream ois = new ObjectInputStream(fis);

            HashMap < AppProperties.Type, ArrayList < Pair < String, String >>> myMap=(HashMap < AppProperties.Type, ArrayList < Pair < String, String >>>)ois.readObject();
            ois.close();
            for (final AppProperties.Type type : AppProperties.Type.values()){//sets all properties from file to appProperties
                appProperties.setConnectedGattList(new ArrayList<BluetoothGatt>(), type);
                appProperties.setNamesAndAddresses(myMap.get(type), type);
            }
        }
        catch (Exception e){

        }
    }
}
