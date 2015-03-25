package com.nas2skupa.my5do12panel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ranko on 3/22/2015.
 */
public class NewOrderDialog extends Activity {

    private ArrayList<Order> newOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_order_dialog);

        final Context context = this;

        parseServerResult(this.getIntent().getStringExtra("orders"));

        ListView newOrdersContainer = (ListView) findViewById(R.id.new_orders_container);
        newOrdersContainer.setAdapter(new NewOrdersAdapter(context, R.layout.new_order_list_item, newOrders));

        Button showButton = (Button) findViewById(R.id.show_new_order);
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent organizerIntent = new Intent(getIntent());
                organizerIntent.setClass(context, Organizer.class);
                startActivity(organizerIntent);
            }
        });

        Button closeButton = (Button) findViewById(R.id.close_new_order);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    private void parseServerResult(String result) {
        if (result == null || result.isEmpty()) return;
        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONArray jsonArray = jsonObj.getJSONArray("ordersPro");
            newOrders = new ArrayList<Order>();

            for (int i = 0; i < jsonArray.length(); i++) {
                Order order = new Order(jsonArray.getJSONObject(i));
                if (order.providerConfirm > 1 || order.userConfirm > 1) continue;
                newOrders.add(order);
            }
        } catch (JSONException e) {
            Log.e("ActionHttpRequest", "Error parsing server data.");
            e.printStackTrace();
        }
    }
}

class NewOrdersAdapter extends ArrayAdapter<Order> {

    private final Context context;
    private final int resource;
    private final List<Order> orders;

    public NewOrdersAdapter(Context context, int resource, List<Order> orders) {
        super(context, resource, orders);
        this.context = context;
        this.resource = resource;
        this.orders = orders;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Order order = orders.get(position);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.new_order_list_item, parent, false);
        TextView title = (TextView) rowView.findViewById(R.id.new_order_list_item_title);
        TextView details = (TextView) rowView.findViewById(R.id.new_order_list_item_details);
        ImageButton confirm = (ImageButton) rowView.findViewById(R.id.new_order_list_item_confirm);
        ImageButton cancel = (ImageButton) rowView.findViewById(R.id.new_order_list_item_cancel);
        ImageButton reschedule = (ImageButton) rowView.findViewById(R.id.new_order_list_item_reschedule);

        SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
        title.setText(String.format("%s %s, %s - %s", order.uName, order.uSurname, tf.format(order.startTime), tf.format(order.endTime)));
        details.setText(String.format("%s (%s kn)\n%s", order.serviceName, order.servicePrice, order.userNote));
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendConfirmation(order.id, "1", "");
                orders.remove(order);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendConfirmation(order.id, "2", "");
                orders.remove(order);
            }
        });
        reschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent organizerIntent = new Intent(context, Organizer.class);
                context.startActivity(organizerIntent);
            }
        });

        return rowView;

    }


    private void sendConfirmation(String orderId, String confirmed, String note) {
        Uri uri = new Uri.Builder().encodedPath("http://nas2skupa.com/5do12/confirmOrder.aspx")
                .appendQueryParameter("orderId", orderId)
                .appendQueryParameter("confirmed", confirmed)
                .appendQueryParameter("note", note)
                .build();
        new HttpRequest(context, uri, false)
                .setOnHttpResultListener(new HttpRequest.OnHttpResultListener() {
                    @Override
                    public void onHttpResult(String result) {

                    }
                });
    }
}
