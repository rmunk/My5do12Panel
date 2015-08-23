package com.nas2skupa.my5do12panel;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Ranko on 9.3.2015..
 */
public class AddUser extends BaseActivity {
    public static EditText firstName;
    public static EditText lastName;
    public static EditText birthday;
    public static EditText address;
    public static Spinner city;
    public static Spinner district;
    public static EditText cell;
    public static EditText email;
    public static RadioButton female;
    public static RadioButton male;
    public ImageButton accept;
    public ImageButton cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_user);
        CitiesFilter citiesFilter = new CitiesFilter(this, (Spinner) findViewById(R.id.city), (Spinner) findViewById(R.id.district), 0);
        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
        birthday = (EditText) findViewById(R.id.birthday);
        address = (EditText) findViewById(R.id.address);
        city = (Spinner) findViewById(R.id.city);
        district = (Spinner) findViewById(R.id.district);
        cell = (EditText) findViewById(R.id.cell);
        email = (EditText) findViewById(R.id.email);
        female = (RadioButton) findViewById(R.id.female);
        male = (RadioButton) findViewById(R.id.male);
        accept = (ImageButton) findViewById(R.id.accept);
        cancel = (ImageButton) findViewById(R.id.cancel);
    }

    public void AddNewUser(View v) {
        final SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        String id = prefs.getString("id", "");

        City curr_city = Globals.cities.get(city.getSelectedItem().toString());
        District curr_district = curr_city.districts.get(district.getSelectedItem().toString());
        Uri uri = new Uri.Builder().encodedPath("http://nas2skupa.com/5do12/providers/addUserApp.aspx")
                .appendQueryParameter("name", firstName.getText().toString())
                .appendQueryParameter("surname", lastName.getText().toString())
                .appendQueryParameter("birth", birthday.getText().toString())
                .appendQueryParameter("address", address.getText().toString())
                .appendQueryParameter("quart", curr_district.id)
                .appendQueryParameter("city", curr_city.id)
                .appendQueryParameter("cell", cell.getText().toString())
                .appendQueryParameter("email", email.getText().toString())
                .appendQueryParameter("gender", male.isChecked() ? "1" : "0")
                .appendQueryParameter("uid", id)
                .build();
        new HttpRequest(this, uri, false)
                .setOnHttpResultListener(new HttpRequest.OnHttpResultListener() {
                    @Override
                    public void onHttpResult(String result) {
                        Log.d("resp", result);
                        if (result.matches("^\\d+$"))
                            Toast.makeText(AddUser.this, "Novi korisnik je uspješno dodan u bazu.", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(AddUser.this, "Došlo je do greške prilikom dodavanja novog korisnika. Molimo pokušajte ponovo kasnije.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void cancelAddUser(View v) {
        Intent i = new Intent(this, Organizer.class);
        startActivity(i);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            datePickerDialog.getDatePicker().setCalendarViewShown(false);
            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            birthday.setText(String.format("%d.%d.%d0", day, month, year));
        }
    }
}
