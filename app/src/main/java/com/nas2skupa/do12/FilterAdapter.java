package com.nas2skupa.do12;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Ranko on 20.11.2014..
 */
public class FilterAdapter extends ArrayAdapter<String> {
    ArrayList<String> items = new ArrayList<String>();

    public FilterAdapter(Context context, int resource) {
        super(context, resource);
        setDropDownViewResource(resource);
    }


}
