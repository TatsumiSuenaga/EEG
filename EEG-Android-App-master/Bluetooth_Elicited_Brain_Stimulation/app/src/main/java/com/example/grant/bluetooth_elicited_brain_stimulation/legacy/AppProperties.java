package com.example.grant.bluetooth_elicited_brain_stimulation.legacy;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

import com.example.grant.bluetooth_elicited_brain_stimulation.legacy.CustomClasses.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * Created by Grant on 10/27/15.
 */

/*
* AppProperties contains all connected devices  and persists over all activities it also hosts
* different methods to interact with associated devices.
*
* Things to Note:
*
* connected Devices and connected addresses should reflect the same devices and should have equal
* length.  however namesAndAddresses will not necessarily reflect connected devices
* (these are previously connected devices or devices that are still in process of being connected)
*
* to avoid adding to these device list outside of the correct usages we have decided in many cases
* to make these hashmaps private. However copies or iterators of associated lists in hashmaps can be
* retrieved from functions defined bellow.
*
* AppProperties exists the lifetime of the application. When the application is closed app properties
* will be destroyed.
* */
public class AppProperties extends Application {

    //Device hashmaps
    private HashMap<Type, ArrayList<BluetoothGatt>> connectedDevices;
    private HashMap<Type, ArrayList<Pair<String, String>>> namesAndAddresses;
    private HashMap<Type, ArrayList<String>> connectedAddresses;

    //BLE
    private BluetoothAdapter myBluetoothAdapter;

    //Strings
    private String currentProfileName;

    //possible types (if new device type is added add it here)
    public enum Type {
        Transmitter, Receiver, Electrode
    }

    //gets the current profile name
    public String getCurrentProfileName() {
        return currentProfileName;
    }

    //sets current profile name
    public void setCurrentProfileName(String name) {
        currentProfileName = name;
    }

    //sets bluetooth adapter
    public void setMyBluetoothAdapter(BluetoothAdapter bta) {
        myBluetoothAdapter = bta;
    }

    //checks whether device is connected with given adress
    public boolean isInConnectedAdresses(String address,Type type){
        return connectedAddresses.get(type).contains(address);
    }

    //returns iterator of connectedGatt
    public Iterator<BluetoothGatt> getConnectedGattIterator(Type type) {
        return connectedDevices.get(type).iterator();
    }

    //disconnects and closes all connected gatts in connectedDevices hashmap
    public void disconnectAllConnected(){
        for(AppProperties.Type type: AppProperties.Type.values()){
            for(BluetoothGatt mGatt: getConnectedGattListCopy(type)){
                mGatt.disconnect();
                mGatt.close();
            }
        }
    }

    //gets gatt with associated adress and type returns null if none exists
    public BluetoothGatt getGatt(String address, Type type){
       for(BluetoothGatt mGatt: connectedDevices.get(type)){
           if(mGatt.getDevice().getAddress().equals(address)){
               return mGatt;
           }
       }
        return null;
    }

    //returns copy o f name and address list
    public ArrayList<Pair<String, String>> getNameAddressListCopy(Type type) {
        ArrayList<Pair<String, String>> nameAddressListCopy = new ArrayList<Pair<String, String>>();
        Pair<String, String> e;
        for (Iterator<Pair<String, String>> it = namesAndAddresses.get(type).iterator(); it.hasNext(); ) {
            e = it.next();
            nameAddressListCopy.add(new Pair<String, String>(e.first, e.second));
        }
        return nameAddressListCopy;
    }

    //gets connectedAddressListCopy of type
    public ArrayList<String> getConnectedAddressListCopy(Type type) {
        ArrayList<String> connectedAddressListCopy = new ArrayList<String>();
        String e;
        for (Iterator<String> it = connectedAddresses.get(type).iterator(); it.hasNext(); ) {
            e = it.next();
            connectedAddressListCopy.add(e);
        }
        return connectedAddressListCopy;
    }

    //gets connectedGattListCopy of type
    public List<BluetoothGatt> getConnectedGattListCopy(Type type) {
        BluetoothGatt e;
        ArrayList<BluetoothGatt> connectedGattCopy = new ArrayList<BluetoothGatt>();
        for (Iterator<BluetoothGatt> it = connectedDevices.get(type).iterator(); it.hasNext(); ) {
            e = it.next();
            connectedGattCopy.add(e);
        }
        return connectedGattCopy;
    }

    //returns size of nameAddress list of type
    public int getNameAdresstListSize(Type type) {return namesAndAddresses.get(type).size();}


    //gets connected gatt list size of type
    public int getConnectedGattListSize(Type type) {
        return connectedDevices.get(type).size();
    }

    // finds the name of the assoicated gatt in namesAndAdresses if it is not found returns empty string
    public String findAssociatedName(BluetoothGatt gatt, Type type) {
        Pair<String, String> e;
        for (Iterator<Pair<String, String>> it = namesAndAddresses.get(type).iterator(); it.hasNext(); ) {
            e = it.next();
            if (e.second.equals(gatt.getDevice().getAddress())) {//if address is equal to device adress returns the name
                return e.first;
            }
        }
        return "";
    }

    //gets the adapter
    public BluetoothAdapter getAdapter() {
        return myBluetoothAdapter;
    }

    //resets all hashmaps and profile name
    public void resetConnectedDevices() {
        connectedDevices = new HashMap<Type, ArrayList<BluetoothGatt>>();
        connectedAddresses = new HashMap<Type, ArrayList<String>>();
        namesAndAddresses = new HashMap<Type, ArrayList<Pair<String, String>>>();
        currentProfileName= "No name";

    }

    //sets connected gatt array of type
    public void setConnectedGattList(ArrayList<BluetoothGatt> g, Type type) {
        ArrayList<String> AddressList = new ArrayList<String>();
        if (connectedDevices != null) {
            if (connectedDevices.containsKey(type)) {//if it contains list removes it and replaces it with new one
                connectedDevices.remove(type);
                connectedAddresses.remove(type);
            }
        }
        connectedDevices.put(type, g);

        for (Iterator<BluetoothGatt> it = g.iterator(); it.hasNext(); ) {//adds adresses to adress list if list contains adresses
            AddressList.add(it.next().getDevice().getAddress());
        }
        connectedAddresses.put(type, AddressList);
    }
    //sets names and adresses list of type. Again the devices associated with these may not be connected.
    public void setNamesAndAddresses(ArrayList<Pair<String,String>> namesAddressPairs,Type type){
        if (namesAndAddresses != null) {
            if (namesAndAddresses.containsKey(type)) {
                namesAndAddresses.remove(type);
                namesAndAddresses.remove(type);
            }
        }
        namesAndAddresses.put(type, namesAddressPairs);
    }

    //adds gatt to ConnectedDevices map in list of Type type returns true on success and false on fail.
    public boolean addGatt(BluetoothGatt g, Type type) {
        Boolean successOrFail = false;
        Boolean addToNamesAddresses=true;
        if (connectedDevices != null) {
            if (connectedDevices.containsKey(type)) {//has been set
                if (!connectedDevices.get(type).contains(g)) {//
                    connectedDevices.get(type).add(g);
                    connectedAddresses.get(type).add(g.getDevice().getAddress());
                    for(Iterator <Pair<String,String>> iterator=namesAndAddresses.get(type).iterator(); iterator.hasNext();){//if the device does not exist in names and adresses  it is added.
                        Pair myPair= iterator.next();
                        if (g.getDevice().getAddress().equals(myPair.second)){
                            addToNamesAddresses=false;
                            break;
                        }
                    }
                    if(addToNamesAddresses)//adds to names and adresses if device wasn't already in list
                        namesAndAddresses.get(type).add(new Pair<String, String>(g.getDevice().getName(), g.getDevice().getAddress()));

                    successOrFail = true;
                }
            }
        }
        return successOrFail;
    }

    //renames device with given adress and type to new name. returns true on sucess and false on failure.
    public boolean rename(String newName, String address, Type type) {
        Pair<String, String> p;
        Boolean successOrFail = false;
        for (Iterator<Pair<String, String>> it = namesAndAddresses.get(type).iterator(); it.hasNext(); ) {
            p = it.next();
            if (p.second.equals(address)) {//if device exists then it is renamed
                namesAndAddresses.get(type).remove(p);
                namesAndAddresses.get(type).add(new Pair<String, String>(newName, address));
                successOrFail = true;
                break;
            }
        }
        return successOrFail;
    }

    //removes gatt of with address address and Type type and also remove same device from names and addresses. returns true on success and false on fail.
    public boolean removeGatt(String address, Type type) {
        Boolean successOrFail = false;
        Pair<String, String> p;
        BluetoothGatt g;
        for (Iterator<Pair<String, String>> it = namesAndAddresses.get(type).iterator(); it.hasNext(); ) {
            p = it.next();
            if (p.second.equals(address)) {//if it  exists in list then it is removed from names and addresses
                namesAndAddresses.get(type).remove(p);
                successOrFail = true;
                break;
            }
        }
        if (successOrFail) {//if it succeeds in removing device from names and adresses it remove from connectedDevices
            for (Iterator<BluetoothGatt> it = connectedDevices.get(type).iterator(); it.hasNext(); ) {
                g = it.next();
                if (g.getDevice().getAddress().equals(address)) {
                    connectedDevices.get(type).remove(g);
                    connectedAddresses.get(type).remove(g.getDevice().getAddress());
                    break;
                }
            }
        }
        return successOrFail;
    }

    //replaces BluetoothGatt mgatt of Type type  with Bluetooth
    public boolean replaceGatt(BluetoothGatt mGatt, BluetoothGatt mNewGatt,Type type){
        Boolean successOrFail=false;
        if(connectedDevices.get(type).remove(mGatt)){
            successOrFail=connectedDevices.get(type).add(mNewGatt);
        }
        return successOrFail;
    }

    //if device is in devices list returns true else false
    public boolean inDeviceLists(BluetoothDevice b) {
        if (namesAndAddresses != null && b != null) {
           for(Type type: Type.values()){//looks through each type
              for(Pair<String,String> pairs:namesAndAddresses.get(type)){//and every address
                  if (pairs.second.equals(b.getAddress())) {
                      return true;
                  }
              }
           }
        }
        return false;
    }

    //if device of Type type  with address mAdress then returns true else false
    public boolean deviceIsConnected(Type type,String mAddress){
        Boolean connected=false;
        ArrayList<String> mConnectedAddresses=connectedAddresses.get(type);
        if(mConnectedAddresses.contains(mAddress)){
            return connectedDevices.get(type).get(mConnectedAddresses.indexOf(mAddress)).connect();
        }
            return false;
    }
    //checks if all devices in nameAddress are  in the connected devices list
    public Boolean connectedMatchesNameAdress(){
        for(AppProperties.Type type : AppProperties.Type.values()){
            if(connectedDevices.get(type).size()!=namesAndAddresses.get(type).size()){
                return false;
            }
        }
        return true;
    }
}
