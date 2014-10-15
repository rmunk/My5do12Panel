package com.nas2skupa.do12;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class OldProviderList extends BaseListActivity{
	public RatingBar rating;
	private ProgressDialog pDialog;
	// URL to get contacts JSON
    private String url = "http://nas2skupa.com/5do12/getPro.aspx?id=";
    // JSON Node names
    private static final String TAG_ARRAY = "provider";
    private static final String TAG_ID = "ID";
    private static final String TAG_NAME = "name";
 // subcats JSONArray
    JSONArray providers = null;
 
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> providerList;
    
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prolist);
        Intent in = getIntent();
        String ID = in.getStringExtra("ID");
        url+=ID;
        providerList = new ArrayList<HashMap<String, String>>();
        ListView lv = getListView();
     // Listview on item click listener
        lv.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // getting values from selected ListItem
                String name = ((TextView) view.findViewById(R.id.name))
                        .getText().toString();
                String subID = ((TextView) view.findViewById(R.id.subcatID))
                        .getText().toString();
            	//String name = "NEKO IME";
                // Starting detail view
                Intent in = new Intent(getApplicationContext(),
                        SingleProvider.class);
                in.putExtra(TAG_ID, subID);
                in.putExtra(TAG_NAME, name);
                startActivity(in);
            	//Toast.makeText(Subcats.this, TAG_NAME+"="+ name, Toast.LENGTH_SHORT).show();
            }
        });
 
        // Calling async task to get json
        new GetProvider().execute();
    }

	private class GetProvider extends AsyncTask<Void, Void, Void> {
		 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(OldProviderList.this);
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
 
                        // tmp hashmap for detail view 
                        HashMap<String, String> subcat = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        subcat.put(TAG_ID, id);
                        subcat.put(TAG_NAME, name);
 
                        providerList.add(subcat);
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
            ListAdapter adapter = new SimpleAdapter(
                    OldProviderList.this, providerList,
                    R.layout.list_item, new String[] { TAG_ID, TAG_NAME }, new int[] { R.id.subcatID, R.id.name });
 
            setListAdapter(adapter);
        }
        
        
        
        
    }
	
}
