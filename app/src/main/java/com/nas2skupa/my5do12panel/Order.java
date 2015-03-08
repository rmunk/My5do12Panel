package com.nas2skupa.my5do12panel;

import android.util.Log;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
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
    public int userConfirm;
    public int providerConfirm;
    public String userNote;
    public String providerNote;
    public String color;

    public Order(JSONObject jsonObj) {
        DateFormat df = new SimpleDateFormat("M/d/yyyy hh:mm:ss a");
        DateFormat tf = new SimpleDateFormat("HH:mm:ss");

        try {
            this.id = jsonObj.optString("id").trim();
            this.proId = jsonObj.optString("proId").trim();
            this.uName = jsonObj.optString("uName").trim();
            this.uSurname = jsonObj.optString("uSurname").trim();
            this.userId = jsonObj.optString("userId").trim();
            this.serviceId = jsonObj.optString("serviceId").trim();
            this.serviceName = jsonObj.optString("serviceName").trim();
            this.servicePrice = jsonObj.optString("servicePrice").trim();
            this.date = df.parse(jsonObj.optString("date").trim());
            this.startTime = tf.parse(jsonObj.optString("startTime").trim());
            this.endTime = tf.parse(jsonObj.optString("endTime").trim());
            this.userConfirm = Integer.parseInt(jsonObj.optString("userConfirm").trim());
            this.providerConfirm = Integer.parseInt(jsonObj.optString("providerConfirm").trim());
            this.userNote = jsonObj.optString("userNote").trim();
            this.providerNote = jsonObj.optString("providerNote").trim();
            this.color = jsonObj.optString("color").trim();
        } catch (ParseException e) {
            Log.e("Order", "Invalid JSON data object");
            e.printStackTrace();
        }
    }

    public static class OrderTimeComparator implements Comparator<Order> {
        public int compare(Order left, Order right) {
            return left.startTime.compareTo(right.startTime);
        }
    }

    @Override
    public boolean equals(Object o) {
        Order that = (Order) o;
        return this.id.equals(that.id) && this.userConfirm == that.userConfirm && this.providerConfirm == that.providerConfirm;
    }
}
