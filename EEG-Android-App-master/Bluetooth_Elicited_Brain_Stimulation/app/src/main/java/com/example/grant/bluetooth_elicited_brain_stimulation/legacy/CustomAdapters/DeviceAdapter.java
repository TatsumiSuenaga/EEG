package com.example.grant.bluetooth_elicited_brain_stimulation.legacy.CustomAdapters;
/**
 * Created by Grant on 10/25/15.
 */

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.grant.bluetooth_elicited_brain_stimulation.R;

import java.util.List;
/*
* view that displays displays devices in list
* */
public class DeviceAdapter extends ArrayAdapter<BluetoothDevice> {

    private List<BluetoothDevice> btList;
    private Context context;

    /*
    * initializes device adapter with list and context
    * */
    public DeviceAdapter(List<BluetoothDevice> btList, Context context) {
        super(context, R.layout.row_string_string_layout, btList);
        this.btList = btList;
        this.context = context;
    }

    /*clears associated bt list */
    public void clear() {
        this.btList.clear();
    }

    /*
    * checks whether device is contained in btList
    * */
    public Boolean contains(BluetoothDevice bt) {
        return this.btList.contains(bt);
    }

    /*
    * gets view for each position.
    * */
    public View getView(int position, View convertView, ViewGroup parent) {

        // verifies convertView is not null
        if (convertView == null) {
            // This a new view we inflate the new layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_string_string_layout, parent, false);
        }
        // Now we can fill the layout with the right values
        TextView tv = (TextView) convertView.findViewById(R.id.name);
        TextView distView = (TextView) convertView.findViewById(R.id.dist);
        BluetoothDevice d = btList.get(position);
        String name = d.getName();
        if (name != null)
            tv.setText(d.getName());
        else
            tv.setText("no name");


        distView.setText("   " + d.getAddress());

        return convertView;
    }

}
