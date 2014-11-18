package com.nas2skupa.do12;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PricelistAdapter extends ArrayAdapter<Pricelist>{

    Context context;
    int layoutResourceId;   
    ArrayList<Pricelist> data = null;
   
    public PricelistAdapter(Context context, int layoutResourceId, ArrayList<Pricelist> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PricelistHolder holder = null;
       
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
           
            holder = new PricelistHolder();
            holder.serviceID=(TextView)row.findViewById(R.id.serviceID);
            holder.akcijaIcon = (ImageView)row.findViewById(R.id.akcijaServiceIcon);
            holder.service = (TextView)row.findViewById(R.id.txtService);
            holder.price = (TextView)row.findViewById(R.id.txtPrice);
           
            row.setTag(holder);
        }
        else
        {
            holder = (PricelistHolder)row.getTag();
        }
       
        Pricelist pricelist = data.get(position);
        holder.serviceID.setText(pricelist.ID);
        holder.service.setText(pricelist.service);
        holder.price.setText(pricelist.price);
        holder.akcijaIcon.setImageResource(pricelist.action);
       
        return row;
    }
   
    static class PricelistHolder
    {
        TextView serviceID;
        ImageView akcijaIcon;
        TextView service;
        TextView price;
    }
}