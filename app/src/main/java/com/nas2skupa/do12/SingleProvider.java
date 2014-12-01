package com.nas2skupa.do12;


import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
    JSONObject provider = null;
    JSONArray pricelists = null;
    JSONObject pricelist = null;
    JSONArray phones = null;
    JSONObject phone = null;
    JSONArray payopt = null;
    JSONObject paying = null;
    private ProgressDialog pDialog;
    private String proId;
    ArrayList<PricelistClass> listArray = new ArrayList<PricelistClass>();
    List<Integer> payingArr = new ArrayList<Integer>();
    List<String> phonesArr = new ArrayList<String>();
    View footer = null;
    private RatingBar ratingBar;
    private RatingBar ratingBarBig;
    private float userRating;
    ImageView btnFav, btnVise;
    Dialog favDialog;
    LinearLayout moreLayout;
    Boolean detailOn = false, isFav=false;
    ProviderClass proClass;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_provider);
        // getting intent data
        Intent in = getIntent();
        Bundle bundle = in.getExtras();
        proClass = bundle.getParcelable("providerclass");
        color = bundle.getString("color");
        proId=proClass.proID;
        url += proId;

        btnFav = (ImageView) findViewById(R.id.tofav);
        btnVise = (ImageView) findViewById(R.id.details);
        btnFav.setOnClickListener(clickHandler);
        btnVise.setOnClickListener(clickHandler);
        // Displaying all values on the screen

        if(proClass.proFav.equals("1")){
            isFav=true;
            btnFav.setImageResource(R.drawable.fav_icon_enabled);
        }
        // Calling async task to get json
        new GetProvider().execute();
        footer = (View) getLayoutInflater().inflate(R.layout.service_listview_footer_row, null);
        listView1 = (ListView) findViewById(R.id.listView1);
        listView1.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                PricelistAdapter.PricelistHolder holder = (PricelistAdapter.PricelistHolder)view.getTag();
                PricelistClass pricelistclass = (PricelistClass)holder.priceObj;
                Bundle b = new Bundle();
                b.putParcelable("pricelistclass", pricelistclass);
                b.putParcelable("providerclass", proClass);
                int[] payArray = new int[payingArr.size()];
                for(int i = 0; i < payingArr.size(); i++) payArray[i] = payingArr.get(i);
                b.putIntArray("paying",payArray);
                b.putString("color", color);
                Intent in = new Intent(getApplicationContext(),
                        OrderActivity.class);
                in.putExtras(b);
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

    View.OnClickListener clickHandler = new View.OnClickListener() {
        public void onClick(View v) {
            if (v == btnFav) {
                new addToFav().execute(null,null,null);
            }

            if (v == btnVise) {
                moreLayout = (LinearLayout) findViewById(R.id.moreLayout);
                TextView lblAbout = (TextView) findViewById(R.id.about_label);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)btnVise.getLayoutParams();
                if (detailOn==false) {
                    lblAbout.setMaxLines(Integer.MAX_VALUE);
                    moreLayout.setVisibility(View.VISIBLE);
                    detailOn=true;
                    btnVise.setImageResource(R.drawable.more_arrow_up);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    btnVise.setLayoutParams(params);
                }else{
                    lblAbout.setMaxLines(4);
                    moreLayout.setVisibility(View.GONE);
                    detailOn=false;
                    btnVise.setImageResource(R.drawable.more_arrow_down);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,0);
                    btnVise.setLayoutParams(params);
                }
            }
        }
    };
    private class addToFav extends AsyncTask<Object, Object, Object> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SingleProvider.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }
        @Override
        protected Object doInBackground(Object[] params) {
            String msg = "";
            try {
                final SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
                String userId = prefs.getString("id", "");
                ServiceHandler sh = new ServiceHandler();
                String setFav="1";
                if(isFav==true){
                    setFav="0";

                }
                String url = "http://nas2skupa.com/5do12/setFav.aspx?userId=" + userId + "&proId=" + provider.getString(TAG_ID) + "&fav="+setFav;
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
            if (pDialog.isShowing())
                pDialog.dismiss();
            Log.d("Post exe",o.toString());
            if(isFav==false){
                isFav=true;
                btnFav.setImageResource(R.drawable.fav_icon_enabled);
            }else{
                isFav=false;
                btnFav.setImageResource(R.drawable.fav_icon);
            }
        }
    }

    public void rateProvider(View view) {
        ratingBar.setVisibility(View.GONE);
        ratingBarBig.setVisibility(View.VISIBLE);
        btnFav.setVisibility(View.GONE);
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
                String url = "http://nas2skupa.com/5do12/setRate.aspx?userId=" + userId + "&proId=" + provider.getString(TAG_ID) + "&rate=" + (int) userRating;
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
            btnFav.setVisibility(View.VISIBLE);
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
                        provider = providers.getJSONObject(i);
                        pricelists = provider.getJSONArray("cjenik");
                        for (int j = 0; j < pricelists.length(); j++) {
                            JSONObject p = pricelists.getJSONObject(j);
                            String serviceID = p.getString("serviceID");
                            String service = p.getString("service");
                            String action = p.getString("action");
                            String termin = p.getString("termin");
                            int akcija = 0;
                            String price = p.getString("reg_price") + " " + getString(R.string.currency);
                            if (action.equals("1")) {
                                price = p.getString("action_price") + " " + getString(R.string.currency);
                                akcija = R.drawable.akcija_icon;
                            }
                            Log.d("ServiceID in populate:"+service,serviceID);
                            PricelistClass currPrice = new PricelistClass(serviceID, service, price,termin, akcija);
                            listArray.add(currPrice);
                        }
                        payopt = provider.getJSONArray("payopt");
                        for (int j = 0; j < payopt.length(); j++) {
                            JSONObject p = payopt.getJSONObject(j);
                            payingArr.add(Integer.parseInt(p.getString("paying")));
                        }
                        phones = provider.getJSONArray("phones");
                        for (int j = 0; j < phones.length(); j++) {
                            JSONObject p = phones.getJSONObject(j);
                            phonesArr.add(p.getString("phone"));
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
            TextView lblPhone = (TextView) findViewById(R.id.phones);
            TextView lblAddress = (TextView) findViewById(R.id.address);
            TextView lblWeb = (TextView) findViewById(R.id.web);
            TextView lblEmail = (TextView) findViewById(R.id.email);
            TextView lblWorking = (TextView) findViewById(R.id.working);
            LinearLayout llMore = (LinearLayout) findViewById(R.id.moreLayout);
            lblAbout.setBackgroundColor(Color.parseColor(color));
            llMore.setBackgroundColor(Color.parseColor(color));
            try {
                lblName.setText(provider.getString(TAG_NAME));
                lblAbout.setText(provider.getString(TAG_ABOUT));
                String phoneString="";
                for(int i = 0; i < phonesArr.size(); i++){
                    phoneString+=phonesArr.get(i);
                    if((i+1)< phonesArr.size()) phoneString+=System.getProperty("line.separator");
                }
                lblPhone.setText(phoneString);
                lblAddress.setText(provider.getString("address"));
                lblWeb.setText(Html.fromHtml(
                        "<a href=\"" + provider.getString("web") + "\">" + provider.getString("web") + "</a> "));
                lblWeb.setMovementMethod(LinkMovementMethod.getInstance());
                lblEmail.setText(Html.fromHtml(
                        "<a href=\"mailto:" + provider.getString("email") + "\">" + provider.getString("email") + "</a> "));
                lblWorking.setText(provider.getString("workingH"));
                int[] payArray = new int[payingArr.size()];
                for(int i = 0; i < payingArr.size(); i++) payArray[i] = payingArr.get(i);
                for(int i=0;i<payArray.length;i++){
                    ImageView payO;
                    if(payArray[i]==2){
                        payO = (ImageView) findViewById(R.id.visa);
                        payO.setVisibility(View.VISIBLE);
                    }
                    if(payArray[i]==3){
                        payO = (ImageView) findViewById(R.id.american);
                        payO.setVisibility(View.VISIBLE);
                    }
                    if(payArray[i]==4){
                        payO = (ImageView) findViewById(R.id.master);
                        payO.setVisibility(View.VISIBLE);
                    }
                    if(payArray[i]==5){
                        payO = (ImageView) findViewById(R.id.diners);
                        payO.setVisibility(View.VISIBLE);
                    }
                }
                try {
                    float rating = Float.parseFloat(provider.getString(TAG_RATING));
                    ratingBar.setRating(rating);
                } catch (NumberFormatException e) {
                }
            }catch (JSONException e){
                Log.e("JSON EXC", e.toString());
            }


            adapter = new PricelistAdapter(SingleProvider.this,
                    R.layout.listview_service_row, listArray);

            listView1.setAdapter(adapter);
        }


    }
}
