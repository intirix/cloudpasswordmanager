package com.intirix.cloudpasswordmanager.pages.login;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.services.session.StorageType;

/**
 * Created by jeff on 7/15/17.
 * https://stackoverflow.com/questions/1625249/android-how-to-bind-spinner-to-custom-object-list
 */

public class StorageTypeAdapter extends ArrayAdapter<StorageType> {

    private Context context;

    private StorageType[] values;

    public StorageTypeAdapter(Context context, int textViewResourceId,
                       StorageType...values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    public int getCount(){
        return values.length;
    }

    public int getIndexOfStorageType(StorageType t) {
        for (int i = 0; i <values.length; i++) {
            if (t.equals(values[i])) {
                return i;
            }
        }
        return -1;
    }

    public StorageType getItem(int position){
        return values[position];
    }

    public long getItemId(int position){
        return position;
    }


    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView)LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item, null);
        label.setText(context.getText(values[position].getLabelResource()));
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = (TextView)LayoutInflater.from(context).inflate(R.layout.storage_type_dropdown_item, null);
        label.setText(context.getText(values[position].getLabelResource()));

        return label;
    }
}
