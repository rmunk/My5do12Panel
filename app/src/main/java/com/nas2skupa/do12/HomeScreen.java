package com.nas2skupa.do12;
 
import com.navdrawer.SimpleSideDrawer;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class HomeScreen extends BaseActivity {   
	
	SimpleSideDrawer slide_me;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);
        //repositionBtn(null);
    }

	public void getSubcats(View v){
	String cat=null;
    switch (v.getId()) {
    case R.id.zdravljebtn:
        cat="1";
        break;
    case R.id.servisbtn:
        cat="2";
        break;
    case R.id.ljepotabtn:
        cat="3";
        break;
    case R.id.intelektbtn:
        cat="4";
        break;
    case R.id.dombtn:
        cat="5";
        break;
    case R.id.sportbtn:
        cat="6";
        break;
    case R.id.turizambtn:
        cat="7";
        break;
    case R.id.prijevozbtn:
        cat="8";
        break;
    case R.id.kulturabtn:
        cat="9";
        break;
		}
		Intent i = new Intent(this, Subcats.class).putExtra("cat", cat);
	    startActivity(i);	
	}
 
}