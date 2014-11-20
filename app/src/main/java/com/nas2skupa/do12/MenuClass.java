package com.nas2skupa.do12;

import com.navdrawer.SimpleSideDrawer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import com.navdrawer.SimpleSideDrawer;

public class MenuClass extends Activity {
	SimpleSideDrawer slide_me;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        slide_me = new SimpleSideDrawer(this);
        slide_me.setRightBehindContentView(R.layout.sidemenu);
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

		case R.id.akcije:
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
		Toast.makeText(this, this+" JAJAJAJAJAJ", Toast.LENGTH_SHORT).show();
		Intent i = new Intent(this, HomeScreen.class);
	    startActivity(i);
	}

	public void listActivity() {
		//Toast.makeText(this, "List Option Selexted", Toast.LENGTH_SHORT).show();
    	Intent i = new Intent(this, Organizer.class);
	    startActivity(i);
	}

	public void favActivity() {
		Toast.makeText(this, this+"dsa", Toast.LENGTH_SHORT).show();
	}

	public void vaznoActivity() {
		Toast.makeText(this, "Važno Option Selexted", Toast.LENGTH_SHORT).show();
	}

	public void optionActivity() {
		slide_me.toggleRightDrawer();
	}
}
