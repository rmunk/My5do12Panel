package com.nas2skupa.do12;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import com.nas2skupa.do12.R;

public class SingleContactActivity  extends Activity {
	
	// JSON node keys
	private static final String TAG_NAME = "subcat";
	//private static final String TAG_EMAIL = "email";
	//private static final String TAG_PHONE_MOBILE = "mobile";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_provider);
        
        // getting intent data
        Intent in = getIntent();
        
        // Get JSON values from previous intent
        String name = in.getStringExtra(TAG_NAME);
        //String email = in.getStringExtra(TAG_EMAIL);
        //String mobile = in.getStringExtra(TAG_PHONE_MOBILE);
        
        // Displaying all values on the screen
        TextView lblName = (TextView) findViewById(R.id.name_label);
       // TextView lblEmail = (TextView) findViewById(R.id.email_label);
       // TextView lblMobile = (TextView) findViewById(R.id.mobile_label);
        
        lblName.setText(name);
        //lblEmail.setText(email);
        //lblMobile.setText(mobile);
    }
	@SuppressLint("NewApi") public boolean onCreateOptionsMenu(Menu menu) {
		getActionBar().setIcon(android.R.color.transparent);
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
