package com.nas2skupa.do12;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;

public class LoginScreen extends Activity {

    static final String TAG = "5do12";

    private ProgressDialog pDialog;

    // URL to get contacts JSON
    private String url;
    private Boolean loged = false;

    // JSON Node names
    private static final String TAG_ARRAY = "user";
    private static final String TAG_ID = "id";
    private static final String TAG_UNAME = "username";
    private static final String TAG_NAME = "name";
    private static final String TAG_SURNAME = "surname";
    private static final String TAG_BIRTH = "birth";
    private static final String TAG_HOMEADD = "home_add";
    private static final String TAG_BUSINESSADD = "business_add";
    private static final String TAG_CELL = "cell";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_GENDER = "gender";

    // subcats JSONArray
    JSONArray user = null;

    SharedPreferences prefs;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        String userId = prefs.getString(TAG_ID, "");
        if (userId.isEmpty()) {
            getActionBar().setIcon(android.R.color.transparent);
            setContentView(R.layout.login);
        } else {
            Intent in = new Intent(this, HomeScreen.class);
            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
        }
    }

    public void tryLogin(View view) throws MalformedURLException, IOException {
        new GetUser().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetUser extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(LoginScreen.this);
            pDialog.setMessage("Trying to login...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            EditText userText = (EditText) findViewById(R.id.username);
            String userTxt = userText.getText().toString();
            EditText passText = (EditText) findViewById(R.id.password);
            String passTxt = passText.getText().toString();
            // Making a request to url and getting response
            url = "http://nas2skupa.com/5do12/getUsers.aspx?u=" + userTxt + "&p=" + passTxt;
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    user = jsonObj.getJSONArray(TAG_ARRAY);

                    if (user.length() == 0)
                        return null;
                    if (user.length() > 1)
                        Log.e(TAG, "More than one user found in database!!! Getting last one...");

                    JSONObject c = user.getJSONObject(user.length() - 1);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(TAG_ID, c.getString(TAG_ID));
                    editor.putString(TAG_UNAME, c.getString(TAG_UNAME));
                    editor.putString(TAG_NAME, c.getString(TAG_NAME));
                    editor.putString(TAG_SURNAME, c.getString(TAG_SURNAME));
                    editor.putString(TAG_BIRTH, c.getString(TAG_BIRTH));
                    editor.putString(TAG_HOMEADD, c.getString(TAG_HOMEADD));
                    editor.putString(TAG_BUSINESSADD, c.getString(TAG_BUSINESSADD));
                    editor.putString(TAG_CELL, c.getString(TAG_CELL));
                    editor.putString(TAG_EMAIL, c.getString(TAG_EMAIL));
                    editor.putString(TAG_GENDER, c.getString(TAG_GENDER));
                    editor.commit();
                    loged = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (loged) {
                Intent in = new Intent(LoginScreen.this, HomeScreen.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
            } else {
                Toast.makeText(LoginScreen.this, "Neispravni username i/ili password!", Toast.LENGTH_SHORT).show();
            }
        }


    }

}
