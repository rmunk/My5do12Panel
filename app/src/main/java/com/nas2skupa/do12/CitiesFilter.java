package com.nas2skupa.do12;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by Ranko on 21.11.2014..
 */
public class CitiesFilter {
    private ArrayList<String> cities = new ArrayList<String>();
    private ArrayList<String> districts = new ArrayList<String>();
    private ArrayAdapter<String> citiesAdapter;
    private ArrayAdapter<String> districtsAdapter;
    private Spinner citiesSpinner;
    private Spinner districtsSpinner;

    public CitiesFilter(Context context, Spinner citiesSpinner, Spinner districtsSpinner) {
        this.citiesSpinner = citiesSpinner;
        this.districtsSpinner = districtsSpinner;

        cities.add("GRAD");
        districts.add("KVART");
        citiesAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, cities);
        districtsAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, districts);

        citiesSpinner.setAdapter(citiesAdapter);
        districtsSpinner.setAdapter(districtsAdapter);
    }
}
