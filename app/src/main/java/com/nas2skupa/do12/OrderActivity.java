package com.nas2skupa.do12;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class OrderActivity extends BaseActivity {

Button datePickerShowDialogButton = null;
Button timePickerShowDialogButton = null;

private ProgressDialog pDialog;
private String url = "http://nas2skupa.com/5do12/getWorking.aspx?id=";
private String color;
// subcats JSONArray
JSONArray working = null;
ArrayList<HashMap<String, String>> workingList;
 
@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderold2);
        // Button to show datepicker
        datePickerShowDialogButton = 
        (Button) this.findViewById(
           R.id.activity_datepickertest_datebutton
        );
        
        datePickerShowDialogButton.setOnClickListener(
          new View.OnClickListener() {
             public void onClick(View v) {
                 showDatePicker();
             }
          });
        // Button to show timepicker
        timePickerShowDialogButton = 
        (Button) this.findViewById(
           R.id.activity_timepickertest_datebutton
        );
        
        timePickerShowDialogButton.setOnClickListener(
          new View.OnClickListener() {
             public void onClick(View v) {
                 showTimePicker();
             }
          });
        Bundle extras = getIntent().getExtras();
        color = extras.getString("color");
        String ID = extras.getString("ID");
        url+=ID;
    }
 
    /**
     * Builds a custom dialog based on the defined layout 
     * 'res/layout/datepicker_layout.xml' and shows it
     */
    public void showDatePicker() {/*
        // Initializiation
        LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
        final AlertDialog.Builder dialogBuilder = 
        new AlertDialog.Builder(this);
        Locale loc=new Locale("hr", "HR");
        View customView = inflater.inflate(R.layout.datepicker_layout, null);
        dialogBuilder.setView(customView);
        final Calendar now = Calendar.getInstance();
        final DatePicker datePicker = 
            (DatePicker) customView.findViewById(R.id.dialog_datepicker);
        final TextView dateTextView = 
            (TextView) customView.findViewById(R.id.dialog_dateview);
        final SimpleDateFormat dateViewFormatter = 
            new SimpleDateFormat("EEEE, dd.MM.yyyy", loc);
        final SimpleDateFormat formatter = 
            new SimpleDateFormat("dd.MM.yyyy", loc);
        // Minimum date
        Calendar minDate = Calendar.getInstance();
        try {
            minDate.setTime(formatter.parse("12.12.2010"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        datePicker.setMinDate(minDate.getTimeInMillis());
        // View settings
        dialogBuilder.setTitle("Odaberite datum");
        Calendar choosenDate = Calendar.getInstance();
        int year = choosenDate.get(Calendar.YEAR);
        int month = choosenDate.get(Calendar.MONTH);
        int day = choosenDate.get(Calendar.DAY_OF_MONTH);
        try {
            Date choosenDateFromUI = formatter.parse(
                datePickerShowDialogButton.getText().toString()
            );
            choosenDate.setTime(choosenDateFromUI);
            year = choosenDate.get(Calendar.YEAR);
            month = choosenDate.get(Calendar.MONTH);
            day = choosenDate.get(Calendar.DAY_OF_MONTH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Calendar dateToDisplay = Calendar.getInstance();
        dateToDisplay.set(year, month, day);
        dateTextView.setText(
            dateViewFormatter.format(dateToDisplay.getTime())
        );
        // Buttons
        dialogBuilder.setNegativeButton(
            "Danas", 
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    datePickerShowDialogButton.setText(
                        formatter.format(now.getTime())
                    );
                    dialog.dismiss();
                }
            }
        );
        dialogBuilder.setPositiveButton(
            "Odaberi", 
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Calendar choosen = Calendar.getInstance();
                    choosen.set(
                        datePicker.getYear(), 
                        datePicker.getMonth(), 
                        datePicker.getDayOfMonth()
                    );
                    datePickerShowDialogButton.setText(
                        dateViewFormatter.format(choosen.getTime())
                    );
                    dialog.dismiss();
                 // Calling async task to get json
                    new GetProvider().execute();
                    
                }
            }
        );
        final AlertDialog dialog = dialogBuilder.create();
        // Initialize datepicker in dialog atepicker
        datePicker.init(
            year, 
            month, 
            day, 
            new DatePicker.OnDateChangedListener() {
                public void onDateChanged(DatePicker view, int year, 
                    int monthOfYear, int dayOfMonth) {
                    Calendar choosenDate = Calendar.getInstance();
                    choosenDate.set(year, monthOfYear, dayOfMonth);
                    dateTextView.setText(
                        dateViewFormatter.format(choosenDate.getTime())
                    );
                    if (choosenDate.compareTo(now) < 0) {
                        dateTextView.setTextColor(
                            Color.parseColor("#ff0000")
                        );
                        ((Button) dialog.getButton(
                        AlertDialog.BUTTON_POSITIVE))
                            .setEnabled(false);
                    } else {
                        dateTextView.setTextColor(
                            Color.parseColor("#000000")
                        );
                        ((Button) dialog.getButton(
                        AlertDialog.BUTTON_POSITIVE))
                            .setEnabled(true);
                    }
                }
            }
        );
        // Finish
        dialog.show();*/
    }
    
    
    /**
     * Builds a custom dialog based on the defined layout 
     * 'res/layout/datepicker_layout.xml' and shows it
     */
    public void showTimePicker() {
       
    }
    
    
    
    public void setSpinner(){

        
    }
    
    private class GetProvider extends AsyncTask<Void, Void, Void> {
		 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(OrderActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
 
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
 
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
 
            Log.d("Response: ", "> " + jsonStr);
 
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                     
                    // Getting JSON Array node
                    working = jsonObj.getJSONArray("working");

                    
                    
                   for (int i = 0; i < working.length(); i++) {
                        JSONObject c = working.getJSONObject(i);
                         
                        String day = c.getString("day");
                        String startHour = c.getString("startHour");
                        String endHour = c.getString("endHour");
                        // tmp hashmap for detail view 
                        HashMap<String, String> wh = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        wh.put("day", day);
                        wh.put("startHour", startHour);
                        wh.put("endHour", endHour);
 
                        workingList.add(wh);
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
            /**
             * Updating parsed JSON data into ListView
             * */
            setSpinner();
        }
        
        
        
        
    }
    
    
}