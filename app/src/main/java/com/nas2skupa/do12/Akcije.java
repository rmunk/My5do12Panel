package com.nas2skupa.do12;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Akcije extends BaseActivity {

    private ListView listView1;
    private Spinner citiesSpinner;
    private Spinner districtsSpinner;
	public RatingBar rating;
	private ProgressDialog pDialog;
	ArrayList<Provider> listArray=new ArrayList<Provider>();
	// URL to get contacts JSON
    private String url = "http://nas2skupa.com/5do12/getAkc.aspx?u=";
    // JSON Node names
    private static final String TAG_ARRAY = "akcije";
    private static final String TAG_ID = "ID";
    private static final String TAG_NAME = "name";
    private static final String TAG_CAT = "category";
    //private String[] catSet = new String[2];
    private String color;
    private Globals Globs;
 // subcats JSONArray
    JSONArray providers = null;
    View header = null;
    View filter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.akclist);
       // Bundle extras = getIntent().getExtras();
        //String subcat = extras.getString("subcat");
        //color = extras.getString("color");
        //String ID = extras.getString("ID");
        final SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        String userId = prefs.getString("id", "");

        url+=userId;


        // Calling async task to get json
        new GetProvider().execute();
        header = (View)getLayoutInflater().inflate(R.layout.listview_header_row, null);
        filter = (View)getLayoutInflater().inflate(R.layout.listview_filter_row, null);
        getSubCatSettings("akcija", "#AAB812", header);

        listView1 = (ListView)findViewById(R.id.listView1);
     // Listview on item click listener
        
        listView1.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            @SuppressLint("NewApi") public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // getting values from selected ListItem
                String proID = ((TextView) view.findViewById(R.id.providerID))
                        .getText().toString();
                String catID = ((TextView) view.findViewById(R.id.catID))
                        .getText().toString();
                // Starting detail view
                catSettings=getCatSett(Integer.parseInt(catID));

               Intent in = new Intent(getApplicationContext(),
                       SingleProvider.class);
               in.putExtra(TAG_ID, proID);
               in.putExtra("color", catSettings[1]);
               startActivity(in);
               //Toast.makeText(Favorites.this, "neko ime="+catID, Toast.LENGTH_SHORT).show();
            }
        });
       
        
        
    }
    @SuppressLint("NewApi")
	public void getSubCatSettings(String subcatTitle, String color, View header){
		TextView naslov = (TextView) header.findViewById(R.id.subcatname);
		naslov.setText(subcatTitle);
		Typeface face = Typeface.createFromAsset(getAssets(),"fonts/Helvetica.ttf");
		naslov.setTypeface(face);
		naslov.setTextSize(16);
		naslov.setAllCaps(true);
		naslov.setTextColor(Color.parseColor("#FFFFFF"));
		naslov.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		String bckclr=color; 
		naslov.setBackgroundColor(Color.parseColor(bckclr));
    }
    private class GetProvider extends AsyncTask<Void, Void, Void> {
		 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Akcije.this);
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
                    providers = jsonObj.getJSONArray(TAG_ARRAY);

                    
                    
                   for (int i = 0; i < providers.length(); i++) {
                        JSONObject c = providers.getJSONObject(i);
                         
                        String id = c.getString(TAG_ID);
                       String name = c.getString(TAG_NAME);
                       String catID = c.getString(TAG_CAT);
                        String favore = c.getString("fav");
                        String action = c.getString("akcija");
                        int fav=R.drawable.blank;
                        int akcija=R.drawable.blank;
                        if(favore.equals("1")){
                        	fav=R.drawable.fav_icon;
                        }
                        if(action.equals("1")){
                        	akcija=R.drawable.akcija_icon;
                        }
                       float rating = 0;
                       try {
                           rating = Float.parseFloat(c.getString("rating"));
                       }catch (NumberFormatException e) {}
                       listArray.add(new Provider(fav, name, id, akcija, rating, catID));
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
            ProviderAdapter adapter = new ProviderAdapter(Akcije.this,
            		R.layout.listview_item_row, listArray);

            listView1.addHeaderView(header);
            listView1.addHeaderView(filter);
            listView1.setAdapter(adapter);

            ArrayAdapter<String> citiesAdapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_spinner_dropdown_item, Globals.getCities());
            citiesSpinner = (Spinner)findViewById(R.id.cities);
            districtsSpinner = (Spinner)findViewById(R.id.districts);
            citiesSpinner.setAdapter(citiesAdapter);
        }
        
        
        
        
    }
}