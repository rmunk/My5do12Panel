package com.nas2skupa.my5do12panel;

import android.os.Bundle;
import android.widget.Spinner;

/**
 * Created by Ranko on 9.3.2015..
 */
public class AddUser extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_user);
        CitiesFilter citiesFilter = new CitiesFilter(this, (Spinner) findViewById(R.id.city), (Spinner) findViewById(R.id.district), 0);

    }
}
