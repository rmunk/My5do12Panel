package com.nas2skupa.my5do12panel;


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
    @SuppressLint("NewApi")
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

    // For testing notifications receiver
    private void sendNotification() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                String msg = "";
                try {
                    final SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
                    String registrationId = prefs.getString("registration_id", "");
                    String message = "bla";
                    ServiceHandler sh = new ServiceHandler();
                    String url = "http://nas2skupa.com/5do12/confirmOrder.aspx?orderId=" + 3 + "&confirmed=" + 1 + "&note=bla";
                    msg = sh.makeServiceCall(url, ServiceHandler.GET);
                    Log.d("Response: ", "> " + msg);
                } catch (Exception ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
            }
        }.execute(null, null, null);
    }


    public void nextScreen(View view) {
        Intent i = new Intent(this, LoginScreen.class);
        startActivity(i);

    }
}