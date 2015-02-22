package com.nas2skupa.my5do12panel;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PricelistAdapter extends ArrayAdapter<PricelistClass>{

    Context context;
    int layoutResourceId;   
    ArrayList<PricelistClass> data = null;
   
    public PricelistAdapter(Context context, int layoutResourceId, ArrayList<PricelistClass> data) {
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
            holder.akcijaIcon = (ImageView)row.findViewById(R.id.akcijaServiceIcon);
            holder.service = (TextView)row.findViewById(R.id.txtService);
            holder.price = (TextView)row.findViewById(R.id.txtPrice);
           
            row.setTag(holder);
        }
        else
        {
            holder = (PricelistHolder)row.getTag();
        }
       
        PricelistClass pricelist = data.get(position);
        holder.service.setText(pricelist.plName);
        holder.price.setText(pricelist.plPrice);
        holder.akcijaIcon.setImageResource(pricelist.akcijaIcon);
        holder.priceObj = pricelist;
        row.setTag(holder);
        return row;
    }
   
    public static class PricelistHolder
    {
        ImageView akcijaIcon;
        TextView service;
        TextView price;
        Parcelable priceObj;
    }
}