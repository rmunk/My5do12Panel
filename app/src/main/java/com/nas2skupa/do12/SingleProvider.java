package com.nas2skupa.do12;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

public class SingleProvider extends BaseActivity {

    // JSON node keys
    private static final String TAG_ARRAY = "provider";
    private static final String TAG_ID = "ID";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_NAME = "name";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_GMAP = "gmap";
    private static final String TAG_ABOUT = "about";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_SUBCAT = "subcat";
    private static final String TAG_RATING = "rating";

    private String color;

    private String url = "http://nas2skupa.com/5do12/getSinglePro.aspx?id=";

    PricelistAdapter adapter;
    private ListView listView1;
    JSONArray providers = null;
    JSONArray pricelists = null;
    private ProgressDialog pDialog;
    private String provider[] = new String[10];
    private String proId;
    ArrayList<Pricelist> listArray = new ArrayList<Pricelist>();
    View footer = null;
    private RatingBar ratingBar;
    private RatingBar ratingBarBig;
    private float userRating;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_provider);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // getting intent data
        Intent in = getIntent();

        // Get JSON values from previous intent
        proId = in.getStringExtra(TAG_ID);
        color = in.getStringExtra("color");
        url += proId;

        // Displaying all values on the screen

        // Calling async task to get json
        new GetProvider().execute();
        footer = (View) getLayoutInflater().inflate(R.layout.service_listview_footer_row, null);
        listView1 = (ListView) findViewById(R.id.listView1);
        listView1.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent in = new Intent(getApplicationContext(),
                        OrderActivity.class);
                String sID = ((TextView) view.findViewById(R.id.serviceID))
                        .getText().toString();
                in.putExtra("sID", sID);
                in.putExtra("proID", proId);
                in.putExtra("color", color);
                startActivity(in);
            }
        });

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#ffadbb02"), PorterDuff.Mode.SRC_IN);
        ratingBarBig = (RatingBar) findViewById(R.id.ratingBarBig);
        LayerDrawable starsBig = (LayerDrawable) ratingBarBig.getProgressDrawable();
        starsBig.getDrawable(2).setColorFilter(Color.parseColor("#ffadbb02"), PorterDuff.Mode.SRC_IN);
        ratingBarBig.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    userRating = rating;
                    new sendRating().execute(null, null, null);
                }
            }
        });
    }

    public void rateProvider(View view) {
        ratingBar.setVisibility(View.GONE);
        ratingBarBig.setVisibility(View.VISIBLE);
    }


    private class sendRating extends AsyncTask<Object, Object, Object> {
        @Override
        protected Object doInBackground(Object[] params) {
            String msg = "";
            try {
                if (userRating == 0) return null;
                final SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
                String userId = prefs.getString("id", "");
                ServiceHandler sh = new ServiceHandler();
                String url = "http://nas2skupa.com/5do12/setRate.aspx?userId=" + userId + "&proId=" + provider[0] + "&rate=" + (int) userRating;
                msg = sh.makeServiceCall(url, ServiceHandler.GET);
                Log.d("Response: ", "> " + msg);
            } catch (Exception ex) {
                Log.d("Error :", ex.getMessage());
            }
            return msg;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            ratingBar.setVisibility(View.VISIBLE);
            ratingBarBig.setVisibility(View.GONE);
            if (o != null) {
                listArray.clear();
                adapter.clear();
                new GetProvider().execute();
            }
        }
    }

    private class GetProvider extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SingleProvider.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @SuppressLint("DefaultLocale")
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
                    providers = jsonObj.getJSONArray(TAG_ARRAY);


                    for (int i = 0; i < providers.length(); i++) {
                        JSONObject c = providers.getJSONObject(i);

                        String id = c.getString(TAG_ID);
                        String name = c.getString(TAG_NAME).toUpperCase();
                        String email = c.getString(TAG_EMAIL);
                        String address = c.getString(TAG_ADDRESS);
                        String gmap = c.getString(TAG_GMAP);
                        String about = c.getString(TAG_ABOUT);
                        String category = c.getString(TAG_CATEGORY);
                        String subcat = c.getString(TAG_SUBCAT);
                        String rating = c.getString(TAG_RATING);
                        provider[0] = id;
                        provider[1] = name;
                        provider[2] = email;
                        provider[3] = address;
                        provider[4] = gmap;
                        provider[5] = about;
                        provider[6] = category;
                        provider[7] = subcat;
                        provider[8] = rating;
                        pricelists = c.getJSONArray("cjenik");
                        for (int j = 0; j < pricelists.length(); j++) {
                            JSONObject p = pricelists.getJSONObject(j);
                            String serviceID = p.getString("serviceID");
                            String service = p.getString("service").substring(0, 1).toUpperCase() + p.getString("service").substring(1).toLowerCase();
                            ;
                            String action = p.getString("action");
                            int akcija = 0;
                            String price = p.getString("reg_price") + " " + getString(R.string.currency);
                            if (action.equals("1")) {
                                price = p.getString("action_price") + " " + getString(R.string.currency);
                                akcija = R.drawable.akcija_icon_small;
                            }
                            listArray.add(new Pricelist(serviceID, service, price, akcija));
                        }
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

            //Toast.makeText(SingleProvider.this, pricelists.length(), Toast.LENGTH_SHORT).show();
            TextView lblName = (TextView) findViewById(R.id.name_label);
            TextView lblAbout = (TextView) findViewById(R.id.about_label);
            lblAbout.setBackgroundColor(Color.parseColor(color));

            lblName.setText(provider[1]);
            lblAbout.setText(provider[5]);
            try {
                float rating = Float.parseFloat(provider[8]);
                ratingBar.setRating(rating);
            } catch (NumberFormatException e) {
            }

            adapter = new PricelistAdapter(SingleProvider.this,
                    R.layout.listview_service_row, listArray);

            listView1.setAdapter(adapter);
        }


    }
}
