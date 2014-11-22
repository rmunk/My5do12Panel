package com.nas2skupa.do12;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Ranko on 20.11.2014..
 */
public class City {
    public String id;
    public String countryId;
    public String name;
    public ArrayList<District> districts = new ArrayList<District>();

    public City(JSONObject object) throws JSONException {
        id = object.getString("id");
        countryId = object.getString("countryId");
        name = object.getString("city");
        JSONArray jsonArray = object.getJSONArray("quarts");
        for (int i = 0; i < jsonArray.length(); i++)
            districts.add(new District(jsonArray.getJSONObject(i)));
    }
}
