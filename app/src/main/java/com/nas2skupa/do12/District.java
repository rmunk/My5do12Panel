package com.nas2skupa.do12;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ranko on 20.11.2014..
 */
public class District {
    public String id;
    public String cityId;
    public String name;

    public District(JSONObject object) throws JSONException {
        id = object.getString("id");
        cityId = object.getString("cityId");
        name = object.getString("quart");
    }
}
