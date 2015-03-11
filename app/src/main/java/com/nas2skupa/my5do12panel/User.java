package com.nas2skupa.my5do12panel;

import org.json.JSONObject;

/**
 * Created by ranko on 2.11.2014..
 */
public class User {
    public String id;
    public String name;
    public String surname;

    public User(JSONObject jsonObj) {
        this.id = jsonObj.optString("id").trim();
        this.name = jsonObj.optString("name").trim();
        this.surname = jsonObj.optString("surname").trim();
    }

    @Override
    public boolean equals(Object o) {
        User that = (User) o;
        return this.id.equals(that.id);
    }
}
