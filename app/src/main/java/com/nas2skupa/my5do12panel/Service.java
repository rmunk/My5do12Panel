package com.nas2skupa.my5do12panel;

import org.json.JSONObject;

/**
 * Created by ranko on 2.11.2014..
 */
public class Service {
    public String id;
    public String name;
    public String surname;

    public Service(JSONObject jsonObj) {
        this.id = jsonObj.optString("id").trim();
        this.name = jsonObj.optString("service").trim();
    }

    @Override
    public boolean equals(Object o) {
        Service that = (Service) o;
        return this.id.equals(that.id);
    }
}
