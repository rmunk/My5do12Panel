package com.nas2skupa.my5do12panel;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

public class SplashScreen extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
//        sendNotification();
        new getCities().execute(null, null, null);
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, LoginScreen.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

                // close this activity
                finish();
            }
        }, 3000);
    }

    private class getCities extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String jsonStr = "";
            try {
                ServiceHandler sh = new ServiceHandler();
                String url = "http://nas2skupa.com/5do12/getCities.aspx";
                jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
                Log.d("Response: ", "> " + jsonStr);
                if (jsonStr != null) {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray jsonArray = jsonObj.getJSONArray("city");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject cityObj = jsonArray.getJSONObject(i);
                        Globals.cities.put(cityObj.getString("city"), new City(cityObj));
                    }
                }
            } catch (Exception ex) {
                Log.d("Error :", ex.getMessage());
            }
            return null;
        }
    }

    public void nextScreen(View view) {
        Intent i = new Intent(this, LoginScreen.class);
        startActivity(i);

    }
}