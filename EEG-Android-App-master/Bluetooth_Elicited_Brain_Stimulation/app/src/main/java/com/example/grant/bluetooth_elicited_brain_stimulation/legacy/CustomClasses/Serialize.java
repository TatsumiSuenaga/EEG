package com.example.grant.bluetooth_elicited_brain_stimulation.legacy.CustomClasses;

import android.content.Context;
import android.os.Environment;
import android.util.Log;


import com.example.grant.bluetooth_elicited_brain_stimulation.legacy.AppProperties;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Grant on 11/12/15.
 */



/*
* serialize allows user to either write device recordings to csv or create/overwrite a profile
* */
public class Serialize {
    //UI elements
    private Context context;

    //Split up hasmaps
    private HashMap <Integer,Integer> elePos;
    private HashMap <Integer,Integer> transPos;
    private HashMap<Integer,String> eleNames;
    private HashMap<Integer,String> transNames;
    private HashMap<Integer,ArrayList<Pair<String,Float>>> eleLists;
    private HashMap<Integer,ArrayList<HashMap<String,Integer>>> transLists;

    //Integers
    private int numberOfTransmitters=0;
    private int numberOfElectrodes=0;

    //Strings
    static final String CSV_SEPARATOR = ",";
    /*
    * initializes serialize with given context
    * */
    public Serialize(Context context) {
        this.context = context.getApplicationContext();
    }

    /*
    * sets all positions of each transmitter position in transPos to 0 again
    * */
    private void restartTransmitterPos(){
        for(int t=0;t<numberOfTransmitters;t++){
            transPos.remove(t);
            transPos.put(t,0);
        }
    }

/*
* electrode and transmitter recording to csv
* */
    public void writeToCSV(HashMap<String,ArrayList<Pair<String,Float>>>  electrodes, HashMap <String,ArrayList<HashMap<String,Integer>>> transmitters) {
       //path is set to test_results folder created on app open
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/bebs/test_results");

        //Date is retrieved  along with app properties
        AppProperties appProperties = (AppProperties) context;
        String time= new Date().toString();

        //Initializes hash maps
        eleNames= new HashMap<>();
        transNames=new HashMap<>();
        elePos=new HashMap<>();
        transPos=new HashMap<>();
        eleLists=new HashMap<>();
        transLists=new HashMap<>();

        //populates electrode related hashmaps
        for(Map.Entry<String,ArrayList<Pair<String,Float>>> e: electrodes.entrySet()){
            eleNames.put(numberOfElectrodes,e.getKey());
            elePos.put(numberOfElectrodes,0);
            eleLists.put(numberOfElectrodes,e.getValue());
            numberOfElectrodes++;
        }
        //populates transmitter related hashmaps
        for(Map.Entry<String,ArrayList<HashMap<String,Integer>>> e: transmitters.entrySet()) {
            transNames.put(numberOfTransmitters,e.getKey());
            transPos.put(numberOfTransmitters,0);
            transLists.put(numberOfTransmitters,e.getValue());
            numberOfTransmitters++;
        }

        int newPos;
        //goes through each electrode recording list
        for(int e=0;e<numberOfElectrodes;e++){
            File f = new File(path, appProperties.getCurrentProfileName() +eleNames.get(e)+ time+".csv");//name is a combination of profile name, electrode name, and time stamp
            try{//Trys to write recordings to file
                BufferedWriter bw = new BufferedWriter(new FileWriter(f.getAbsolutePath()));
            while(elePos.get(e)<eleLists.get(e).size()){//cycles through all electrode recordings in associated electrode recording list
                StringBuffer oneLine = new StringBuffer();
                oneLine.append(eleLists.get(e).get(elePos.get(e)).first);//timestamp
                oneLine.append(CSV_SEPARATOR);//csv seperator
                oneLine.append(eleLists.get(e).get(elePos.get(e)).second);//value
                for(int t=0;t<numberOfTransmitters;t++){// cycles through each transmitter
                    if(transLists.get(t).size()<transPos.get(t)){//if there is a transmission at selected position in recording then adds either 0 or 1 to csv
                        int eleListPosition=transLists.get(t).get(transPos.get(t)).get(eleNames.get(t));
                        oneLine.append(CSV_SEPARATOR);
                        if(eleListPosition== elePos.get(e)){
                            oneLine.append(1);
                        }
                        else{
                            oneLine.append(0);
                        }
                        //updates position
                        newPos= transPos.get(t)+1;
                        transPos.remove(t);
                        transPos.put(t,newPos);
                    }
                }
                //writes and creates new line
                bw.write(oneLine.toString());
                bw.newLine();

                //Updates electrode position
                newPos= elePos.get(e)+1;
                elePos.remove(e);
                elePos.put(e,newPos);
            }
                //flushes buffered writer
                bw.flush();
                bw.close();
            }
            catch (Exception error){
                Log.e("Problem Serializing:", f.getName() + " could not be serialized");
            }
            restartTransmitterPos();
        }
    }

    /*
    * writes devices to profile with option to overwrite
    * */
    public void writeProfileToFile(Boolean overwrite) {
        AppProperties appProperties = (AppProperties) context;
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/bebs/profiles");
        HashMap<AppProperties.Type, List<Pair<String, String>>> myDevices = new <AppProperties.Type, List<Pair<String, String>>>HashMap();

        //creates map of lists of all devices
        for (AppProperties.Type type : AppProperties.Type.values()) {
            myDevices.put(type, appProperties.getNameAddressListCopy(type));
        }
        //defualt profile name
        File f = new File(path, appProperties.getCurrentProfileName());
        FileOutputStream fos;

        try {
            for (int x = 0; !overwrite && f.exists(); x++) {
                f = new File(path, appProperties.getCurrentProfileName() + "_" + x);
            }
            //creates new fileoutput stream with the given name
            fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            //writes the devices
            oos.writeObject(myDevices);
            oos.close();

        } catch (Exception e) {
            Log.i("Creation Failed", f.getName());
        }
    }

}
