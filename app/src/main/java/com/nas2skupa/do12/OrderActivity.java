package com.nas2skupa.do12;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.content.SharedPreferences;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class OrderActivity extends BaseActivity{

    // Widget GUI
    Button btnCalendar, btnTimePicker, btnNaruci;
    EditText  txtNote;
    TextView txtDate, txtTime, txtService,name_label;

    // Variable for storing current date and time
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String serviceID,providerID,userId, provider, service, color;
    private ProgressDialog pDialog;
    ProviderClass proClass;
    PricelistClass priceClass;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order);

        Intent in = getIntent();
        Bundle bundle = in.getExtras();
        proClass = bundle.getParcelable("providerclass");
        priceClass = bundle.getParcelable("pricelistclass");
        color = bundle.getString("color");

        serviceID = priceClass.plID;
        providerID = proClass.proID;
        provider = proClass.proName;
        service = priceClass.plName;
        final SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        userId = prefs.getString("id", "");

        txtService = (TextView) findViewById(R.id.usluga);
        txtService.setText(service);
        name_label=(TextView) findViewById(R.id.name_label);
        name_label.setText(provider);

        btnNaruci = (Button) findViewById(R.id.btnNaruci);
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtNote = (EditText) findViewById(R.id.txtNote);


        txtDate.setOnClickListener(clickHandler);
        txtTime.setOnClickListener(clickHandler);
        btnNaruci.setOnClickListener(clickHandler);


    }

    View.OnClickListener clickHandler = new View.OnClickListener() {
        public void onClick(View v) {
            if (v == txtDate) {

                // Process to get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                // Launch Date Picker Dialog
                DatePickerDialog dpd = new DatePickerDialog(OrderActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // Display Selected date in textbox
                                txtDate.setText(dayOfMonth + "-"
                                        + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
            }
            if (v == txtTime) {

                // Process to get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog tpd = new TimePickerDialog(OrderActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                // Display Selected time in textbox
                                txtTime.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                tpd.show();
            }
            if (v == btnNaruci){
                new setOrder().execute();
            }
        }
    };

    private class setOrder extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(OrderActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @SuppressLint("DefaultLocale")
        @Override
        protected Void doInBackground(Void... arg0) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://nas2skupa.com/5do12/clientOrder.aspx");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("uid", userId));
                nameValuePairs.add(new BasicNameValuePair("proid", providerID));
                nameValuePairs.add(new BasicNameValuePair("datum", txtDate.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("vrijeme", txtTime.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("serviceid", serviceID));
                nameValuePairs.add(new BasicNameValuePair("userNote", txtNote.getText().toString()));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                HttpEntity resEntity = response.getEntity();
                String responseBody;
                if (resEntity != null) {
                     responseBody = EntityUtils.toString(response.getEntity());
                }else{
                    responseBody="no response";
                }
                Log.d("uid",userId);
                Log.d("proId",providerID);
                Log.d("DATE",txtDate.getText().toString());
                Log.d("TIME",txtTime.getText().toString());
                Log.d("sID",serviceID);
                Log.d("NOTE",txtNote.getText().toString());
                Log.d("RESPONSE",responseBody);


            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
            return null;

        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

        }
    }

    
    
}