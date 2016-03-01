package com.example.grant.bluetooth_elicited_brain_stimulation.legacy.CustomAdapters; /**
 * Created by Grant on 10/25/15.
 */

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.grant.bluetooth_elicited_brain_stimulation.legacy.AppProperties;
import com.example.grant.bluetooth_elicited_brain_stimulation.legacy.CustomClasses.Pair;
import com.example.grant.bluetooth_elicited_brain_stimulation.R;
import com.example.grant.bluetooth_elicited_brain_stimulation.legacy.CustomClasses.Serialize;

import java.util.Iterator;
import java.util.List;

/*
* displays pairs from app properties namesAndAdresses pair list
* */
public class PairAdapter extends ArrayAdapter<Pair<String, String>> {
    //UI elements
    private Context context;
    private PairAdapter myPairAdapter;
    private AppProperties appProperties;

    //Device descriptor
    private AppProperties.Type type;

    private List<Pair<String, String>> nameAddress;
    private Serialize serialize;


    /*
    * initializes pair adapter with nameAdress  pair list, context, appProperties, and associated type
    * */
    public PairAdapter(List<Pair<String, String>> nameAddress, Context context, AppProperties appProperties, AppProperties.Type type) {
        super(context, R.layout.row_string_button_button_layout, nameAddress);
        this.nameAddress = nameAddress;
        this.context = context;
        this.appProperties = appProperties;
        this.type = type;
        this.myPairAdapter = this;
        serialize= new Serialize(context);
    }

    /*
    * clears associated local nameAdress list
    * */
    public void clear() {
        this.nameAddress.clear();
    }

    /*
    * checks if device address is in nameAddress list
    * */
    public Boolean contains(BluetoothDevice bt) {
        for (Iterator<Pair<String, String>> it = nameAddress.iterator(); it.hasNext(); ) {
            if (it.next().second.equals(bt.getAddress())) return true;

        }
        return false;
    }
/*
* Updates name address list
*NOTE: call this instead of notifyDataSetChanged()
* */
    public void updateNameAddressList(List<Pair<String, String>> s) {
        nameAddress.clear();
        nameAddress.addAll(s);
        this.notifyDataSetChanged();
    }

    /*
    * gets
    * */
    public View getView(final int position, View convertView, ViewGroup parent) {
        // verify the convertView is not null
        if (convertView == null) {
            // This a new view we inflate the new layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_string_button_button_layout, parent, false);
        }
        // Now we can fill the layout with the right values
        TextView tv = (TextView) convertView.findViewById(R.id.name);
        ImageButton remove = (ImageButton) convertView.findViewById(R.id.deleteButton);
        ImageButton rename = (ImageButton) convertView.findViewById(R.id.renameButton);
        TextView distView = (TextView) convertView.findViewById(R.id.dist);


        Pair<String, String> p = nameAddress.get(position);
        String name = p.first;
        final String address = p.second;

        //sets on click for view based on position
        remove.setOnClickListener(new View.OnClickListener() {
            /*
            * removes associated gatt form app properties address list and disconnects it
            * */
            @Override
            public void onClick(View v) {
                appProperties.removeGatt(address, type);
                myPairAdapter.updateNameAddressList(appProperties.getNameAddressListCopy(type));
                if(appProperties.isInConnectedAdresses(address,type)) //if its connected disconnect
                    appProperties.getGatt(address, type).disconnect();

                //serializes devices to reflect change in profile
                serialize.writeProfileToFile(true);

            }
        });
        rename.setOnClickListener(new View.OnClickListener() {
            /*
            * on click creates an dialogue box to rename
            * */
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Rename");

                // Set up the input
                final EditText input = new EditText(context);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    /*
                    * sets name to input and serializes to file to reflect change
                    * */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        appProperties.rename(input.getText().toString(), address, type);
                        myPairAdapter.updateNameAddressList(appProperties.getNameAddressListCopy(type));
                        serialize.writeProfileToFile(true);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    /*
                    * closes dialogue and does not rename
                    * */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        tv.setText(name);
        distView.setText(("   " + address));


        return convertView;
    }

}
