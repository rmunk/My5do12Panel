package com.nas2skupa.do12;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

/**
 * Created by Ranko on 20.11.2014..
 */
public class City {
    public String id;
    public String countryId;
    public String name;
    public LinkedHashMap<String, District> districts = new LinkedHashMap<String, District>();

    public City(JSONObject object) throws JSONException {
        id = object.getString("id");
        countryId = object.getString("countryId");
        name = object.getString("city");
        JSONArray jsonArray = object.getJSONArray("quarts");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject districtObj = jsonArray.getJSONObject(i);
            districts.put(districtObj.getString("quart"), new District(districtObj));
        }
    }
}
