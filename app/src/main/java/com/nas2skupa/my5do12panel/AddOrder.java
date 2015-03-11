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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ranko on 9.3.2015..
 */
public class AddOrder extends BaseActivity {
    private ArrayList<User> users = new ArrayList<User>();
    private ArrayList<Service> services = new ArrayList<Service>();
    private UsersAdapter usersAdapter;
    private ServicesAdapter servicesAdapter;
    private Spinner usersSpinner;
    private Spinner servicesSpinner;
    private User currentUser = null;
    private Service currentService = null;

    public static TextView date;
    public static TextView time;

    private static Calendar appointment = Calendar.getInstance();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("d.M.yyyy.");
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("H:mm");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_order);

        usersSpinner = (Spinner) findViewById(R.id.users);
        servicesSpinner = (Spinner) findViewById(R.id.services);

        usersSpinner.setEmptyView(findViewById(R.id.empty_users));
        servicesSpinner.setEmptyView(findViewById(R.id.empty_services));

        usersAdapter = new UsersAdapter(this, users);
        servicesAdapter = new ServicesAdapter(this, services);

        this.usersSpinner.setAdapter(usersAdapter);
        this.servicesSpinner.setAdapter(servicesAdapter);

        getUsers();
        getServices();


        date = (TextView) findViewById(R.id.date);
        time = (TextView) findViewById(R.id.time);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });

        date.setText(dateFormat.format(appointment.getTime()));
        time.setText(timeFormat.format(appointment.getTime()));
    }

    public void AddNewOrder(View v) {
        if (currentUser == null || currentService == null) return;
        final SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        String id = prefs.getString("id", "");
        Uri uri = new Uri.Builder().encodedPath("http://nas2skupa.com/5do12/providers/addOrderApp.aspx")
                .appendQueryParameter("uid", id)
                .appendQueryParameter("userId", currentUser.id)
                .appendQueryParameter("sId", currentService.id)
                .appendQueryParameter("date", new SimpleDateFormat("d.M.yyyy").format(appointment.getTime()))
                .appendQueryParameter("hour", String.valueOf(appointment.get(Calendar.HOUR_OF_DAY)))
                .appendQueryParameter("minute", String.valueOf(appointment.get(Calendar.MINUTE)))
                .build();
        new HttpRequest(this, uri, false)
                .setOnHttpResultListener(new HttpRequest.OnHttpResultListener() {
                    @Override
                    public void onHttpResult(String result) {
                        Log.d("resp", result);
                        if (result.matches("^\\d+$"))
                            Toast.makeText(AddOrder.this, "Zadani termin je uspješno dodan u kalendar.", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(AddOrder.this, "Došlo je do greške prilikom zadavanja novog termina. Molimo pokušajte ponovo kasnije.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void cancelAddOrder(View v) {
        Intent i = new Intent(this, Organizer.class);
        startActivity(i);
    }

    private void getUsers() {
        final SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        String id = prefs.getString("id", "");
        Uri uri = new Uri.Builder().encodedPath("http://nas2skupa.com/5do12/providers/listUsers.aspx")
                .appendQueryParameter("id", id)
                .build();
        new HttpRequest(getApplicationContext(), uri, true)
                .setOnHttpResultListener(new HttpRequest.OnHttpResultListener() {
                    @Override
                    public void onHttpResult(String result) {
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = new JSONObject(result);
                            JSONArray jsonArray = jsonObj.getJSONArray("users");
                            for (int i = 0; i < jsonArray.length(); i++)
                                users.add(new User(jsonArray.getJSONObject(i)));
                            usersAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void getServices() {
        final SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        String id = prefs.getString("id", "");
        Uri uri = new Uri.Builder().encodedPath("http://nas2skupa.com/5do12/providers/listService.aspx")
                .appendQueryParameter("id", id)
                .build();
        new HttpRequest(getApplicationContext(), uri, true)
                .setOnHttpResultListener(new HttpRequest.OnHttpResultListener() {
                    @Override
                    public void onHttpResult(String result) {
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = new JSONObject(result);
                            JSONArray jsonArray = jsonObj.getJSONArray("services");
                            for (int i = 0; i < jsonArray.length(); i++)
                                services.add(new Service(jsonArray.getJSONObject(i)));
                            servicesAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public class UsersAdapter extends ArrayAdapter<User> {
        public UsersAdapter(Context context, ArrayList<User> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            currentUser = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, parent, false);
            }
            TextView item = (TextView) convertView.findViewById(R.id.spinner_item_text);
            item.setText(currentUser.name + " " + currentUser.surname);
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            User user = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.spiner_dropdown_item, parent, false);
            }
            TextView item = (TextView) convertView.findViewById(R.id.spinner_item_text);
            item.setText(user.name + " " + user.surname);
            return convertView;
        }
    }

    public class ServicesAdapter extends ArrayAdapter<Service> {
        public ServicesAdapter(Context context, ArrayList<Service> services) {
            super(context, 0, services);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            currentService = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, parent, false);
            }
            TextView item = (TextView) convertView.findViewById(R.id.spinner_item_text);
            item.setText(currentService.name);
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            Service service = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.spiner_dropdown_item, parent, false);
            }
            TextView item = (TextView) convertView.findViewById(R.id.spinner_item_text);
            item.setText(service.name);
            return convertView;
        }
    }


    /* Date Time */

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            int year = appointment.get(Calendar.YEAR);
            int month = appointment.get(Calendar.MONTH);
            int day = appointment.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            datePickerDialog.getDatePicker().setCalendarViewShown(false);
            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            appointment.set(year, month, day);
            date.setText(dateFormat.format(appointment.getTime()));
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            int hour = appointment.get(Calendar.HOUR_OF_DAY);
            int minute = appointment.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            final TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
//            timePickerDialog.
            return timePickerDialog;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            appointment.set(Calendar.HOUR_OF_DAY, hourOfDay);
            appointment.set(Calendar.MINUTE, minute);
            time.setText(timeFormat.format(appointment.getTime()));
        }
    }
}
