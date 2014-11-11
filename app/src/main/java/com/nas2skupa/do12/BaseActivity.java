package com.nas2skupa.do12;

import com.navdrawer.SimpleSideDrawer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends Activity {
	SimpleSideDrawer slide_me;
	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getActionBar().setIcon(android.R.color.transparent);
		getMenuInflater().inflate(R.menu.main, menu);
        
        slide_me = new SimpleSideDrawer(this);
        slide_me.setRightBehindContentView(R.layout.sidemenu);
        TextView poc = (TextView) findViewById(R.id.pocetnabtn);
        TextView prof = (TextView) findViewById(R.id.profilbtn);
        poc.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
    			homeActivity();
    			finish();
            }
        });
        prof.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
        		//Intent i = new Intent(this, Profile.class);
        	    //startActivity(i);
        	    finish();
            }
        });
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.home:
			homeActivity();
			return true;

		case R.id.list:
			listActivity();
			return true;

		case R.id.fav:
			favActivity();
			return true;

		case R.id.vazno:
			vaznoActivity();
			return true;
			
		case R.id.option:
			optionActivity();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}
	public void homeActivity() {
		Intent i = new Intent(this, HomeScreen.class);
	    startActivity(i);
	}

	public void listActivity() {
		//Toast.makeText(this, "List Option Selexted", Toast.LENGTH_SHORT).show();
    	Intent i = new Intent(this, Organizer.class);
	    startActivity(i);
	}

	public void favActivity() {

        Intent i = new Intent(this, Favorites.class);
        startActivity(i);
	}

	public void vaznoActivity() {
		Toast.makeText(this, "Va�no Option Selected", Toast.LENGTH_SHORT).show();
	}
	
	public void optionActivity() {
		slide_me.toggleRightDrawer();
	}
}
