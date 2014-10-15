package com.nas2skupa.do12;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract.*;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class LoginScreen extends Activity{

	 
    private ProgressDialog pDialog;
 
    // URL to get contacts JSON
    private String url;
    private Boolean loged=false;
    
 
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
	
	@SuppressLint("NewApi") @Override
    public void onCreate(Bundle savedInstanceState) {
		getActionBar().setIcon(android.R.color.transparent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
     }

    public void tryLogin(View view) throws MalformedURLException, IOException{
    	//Intent i = new Intent(this, HomeScreen.class);
    	//startActivity(i);
    	new GetUser().execute();
    }
    
    /**
     * Async task class to get json by making HTTP call
     * */
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
            EditText userText = (EditText)findViewById(R.id.username);
            String userTxt = userText.getText().toString();
            EditText passText = (EditText)findViewById(R.id.password);
            String passTxt = passText.getText().toString();
            userTxt="test";
            passTxt="pass";
            // Making a request to url and getting response
            url = "http://nas2skupa.com/5do12/getUsers.aspx?u="+userTxt+"&p="+passTxt;
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            
            Log.d("Response: ", "> " + jsonStr);
 
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                     
                    // Getting JSON Array node
                    user = jsonObj.getJSONArray(TAG_ARRAY);
 
                   for (int i = 0; i < user.length(); i++) {
                        JSONObject c = user.getJSONObject(i);
                         
                        String id = c.getString(TAG_ID);
                        String uname = c.getString(TAG_UNAME);
                        String name = c.getString(TAG_NAME);
                        String sname = c.getString(TAG_SURNAME);
                        String birth = c.getString(TAG_BIRTH);
                        String homeadd = c.getString(TAG_HOMEADD);
                        String businessadd = c.getString(TAG_BUSINESSADD);
                        String cell = c.getString(TAG_CELL);
                        String email = c.getString(TAG_EMAIL);
                        String gender = c.getString(TAG_GENDER);
                        ((Globals) getApplication()).setUserDetails(id, uname, name, sname, birth, homeadd, businessadd, cell, email, gender);
                        loged=true;

                    }
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

            if(loged){
            	Intent in = new Intent(LoginScreen.this, HomeScreen.class);
            	startActivity(in);
            }else{

            	Toast.makeText(LoginScreen.this, "Neispravni username i/ili password!", Toast.LENGTH_SHORT).show();
            }
        }
        
        
        
        
    }
        
}
