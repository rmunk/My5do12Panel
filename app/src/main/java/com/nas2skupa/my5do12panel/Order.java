package com.nas2skupa.my5do12panel;

import android.util.Log;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ranko on 2.11.2014..
 */
public class Order {
    public String id;
    public String proId;
    public String uName;
    public String uSurname;
    public String userId;
    public String serviceId;
    public String serviceName;
    public String servicePrice;
    public Date date;
    public Date startTime;
    public Date endTime;
    public String userConfirm;
    public String providerConfirm;
    public String userNote;
    public String providerNote;
    public String color;

    public Order(JSONObject jsonObj) {
        DateFormat df = new SimpleDateFormat("M/d/yyyy hh:mm:ss a");
        DateFormat tf = new SimpleDateFormat("HH:mm:ss");

        try {
            this.id = jsonObj.optString("id");
            this.proId = jsonObj.optString("proId");
            this.uName = jsonObj.optString("uName");
            this.uSurname = jsonObj.optString("uSurname");
            this.userId = jsonObj.optString("userId");
            this.serviceId = jsonObj.optString("serviceId");
            this.serviceName = jsonObj.optString("serviceName");
            this.servicePrice = jsonObj.optString("servicePrice");
            this.date = df.parse(jsonObj.optString("date"));
            this.startTime = tf.parse(jsonObj.optString("startTime"));
            this.endTime = tf.parse(jsonObj.optString("endTime"));
            this.userConfirm = jsonObj.optString("userConfirm");
            this.providerConfirm = jsonObj.optString("providerConfirm");
            this.userNote = jsonObj.optString("userNote");
            this.providerNote = jsonObj.optString("providerNote");
            this.color = jsonObj.optString("color");
        } catch (ParseException e) {
            Log.e("Order", "Invalid JSON data object");
            e.printStackTrace();
        }
    }
}
