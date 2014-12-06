package com.nas2skupa.do12;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class ProfilActivity extends BaseActivity {

	private ProgressDialog pDialog;
    TextView header = null;
    TextView pUsername, pName, pAddress, pMobile, pEmail, pPasswordlbl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil);
        final SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        String userId = prefs.getString("id", "");
        header = (TextView) findViewById(R.id.subcatname);
        getSubCatSettings("profil", "#adbb00");
        pUsername=(TextView) findViewById(R.id.pUsername);
        pUsername.setText(prefs.getString("username",""));
        pName=(TextView) findViewById(R.id.pName);
        pName.setText(prefs.getString("name","")+" "+prefs.getString("surname",""));
        pAddress=(TextView) findViewById(R.id.pAdresa);
        pAddress.setText(prefs.getString("home_add","")+System.getProperty("line.separator")+prefs.getString("quartName","")+", "+prefs.getString("cityName",""));
        pMobile=(TextView) findViewById(R.id.pMobile);
        pMobile.setText(prefs.getString("cell",""));
        pEmail=(TextView) findViewById(R.id.pEmail);
        pEmail.setText(prefs.getString("email",""));
        pPasswordlbl=(TextView) findViewById(R.id.pPasswordlbl);
        pAddress.setOnClickListener(clickHandler);
        pPasswordlbl.setOnClickListener(clickHandler);

    }

    View.OnClickListener clickHandler = new View.OnClickListener() {
        public void onClick(View v) {
            if (v == pPasswordlbl) {
            }

            if (v == pAddress) {
            }
        }
    };

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