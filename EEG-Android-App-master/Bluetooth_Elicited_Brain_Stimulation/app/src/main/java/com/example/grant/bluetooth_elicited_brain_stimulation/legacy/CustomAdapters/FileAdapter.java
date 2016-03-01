package com.example.grant.bluetooth_elicited_brain_stimulation.legacy.CustomAdapters;

import android.app.AlertDialog;
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


import java.io.File;


import com.example.grant.bluetooth_elicited_brain_stimulation.legacy.AppProperties;
import com.example.grant.bluetooth_elicited_brain_stimulation.R;

/**
 * Created by Grant on 11/18/15.
 */
/*
* displays files at a given path
* */
public class FileAdapter extends ArrayAdapter<File> {
    //files related
    private File[] files;
    private File path;

    //UI elements
    private Context context;
    private AppProperties appProperties;
    private FileAdapter myFileAdapter;

    /*
    * file adapter is initialized with context, appProperties, associated file pathway for files
    * */
    public FileAdapter(Context context,AppProperties appProperties, File path) {
        super(context, R.layout.row_string_button_button_layout);
        this.path = path;
        this.files = path.listFiles();
        this.context = context;
        this.appProperties = appProperties;
    }

    /*
    * retrieves  array of files  at given path
    * */
    public File[] fileNames(File path) {

        File file[] = path.listFiles();

        if (file == null)
            file = new File[0];

        return file;
    }

    /*
    * updates file array
    * NOTE: call this rather than notifyDataSetChanged
    * */
    public void updateFileList() {
        files = fileNames(path);
        this.getCount();
        this.notifyDataSetChanged();
    }
    /*
    * counts number of files in files array
    * */
    public int getCount(){
        if(files==null){//if null then array has no files
            return 0;
        }
        return files.length;
    }

    /*gets file at position x
    *
    * */
    public File getItem(int x){
        return files[x];
    }
    /*
    * gets view by file position in array
    * */
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            // This a new view we inflate the new layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_string_button_button_layout, parent, false);
        }
        //Sets UI elements
        TextView tv = (TextView) convertView.findViewById(R.id.name);
        tv.setText(files[position].getName());
        ImageButton remove = (ImageButton) convertView.findViewById(R.id.deleteButton);
        ImageButton rename = (ImageButton) convertView.findViewById(R.id.renameButton);

        remove.setOnClickListener(new View.OnClickListener() {
            /*
            * removes file from phone  and updates file list
            * */
            @Override
            public void onClick(View v) {
                files[position].delete();
                updateFileList();
            }
        });

        rename.setOnClickListener(new View.OnClickListener() {
            /**
             * creates dialogue that allows user to rename associated file to new name
             */

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
                    * finds file and renames it and then updates list
                    * */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File[] files = path.listFiles();
                        for (File f : files) {
                            if (f.getName().equals(files[position].getName())) {
                                f.renameTo(new File(path, input.getText().toString()));
                                break;
                            }
                        }
                        updateFileList();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    /*
                    * closes dialogue without renaming
                    * */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
        return convertView;
    }
}
