package com.nas2skupa.do12;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfilActivity extends BaseActivity {

	private ProgressDialog pDialog;
    TextView header = null;
    TextView pUsername, pName, pCity, pMobile, pEmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil);
        final SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        String userId = prefs.getString("id", "");
        header = (TextView) findViewById(R.id.subcatname);
        getSubCatSettings("profil", "#000000");
        pUsername=(TextView) findViewById(R.id.pUsername);
        pUsername.setText(prefs.getString("username",""));
        pName=(TextView) findViewById(R.id.pName);
        pName.setText(prefs.getString("name","")+" "+prefs.getString("surname",""));
        pCity=(TextView) findViewById(R.id.pCity);
        City cityObj = Globals.cities.get(prefs.getString("city",""));
        if (cityObj != null) {
            District districtObj = cityObj.districts.get(prefs.getString("district",""));
            if (districtObj != null)
                pCity.setText(cityObj.name+", "+districtObj.name);
        }
        pMobile=(TextView) findViewById(R.id.pMobile);
        pMobile.setText(prefs.getString("cell",""));
        pEmail=(TextView) findViewById(R.id.pEmail);
        pEmail.setText(prefs.getString("email",""));

    }
    @SuppressLint("NewApi")
	public void getSubCatSettings(String subcatTitle, String color){
		TextView naslov = (TextView) findViewById(R.id.subcatname);
		naslov.setText(subcatTitle);
		Typeface face = Typeface.createFromAsset(getAssets(),"fonts/Helvetica.ttf");
		naslov.setTypeface(face);
		naslov.setTextSize(16);
		naslov.setAllCaps(true);
		naslov.setTextColor(Color.parseColor("#FFFFFF"));
		naslov.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		String bckclr=color; 
		naslov.setBackgroundColor(Color.parseColor(bckclr));
    }

}