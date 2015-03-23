package com.nas2skupa.my5do12panel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Ranko on 3/22/2015.
 */
public class NewOrderDialog extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_order_dialog);

        final Context context = this;
        Intent intent = getIntent();


        Button showButton = (Button) findViewById(R.id.show_new_order);
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Organizer.class);
                startActivity(intent);
            }
        });

        Button confirmButton = (Button) findViewById(R.id.confirm_new_order);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                sendConfirmation("", "1", "");
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

    private void sendConfirmation(String orderId, String confirmed, String note) {
        Uri uri = new Uri.Builder().encodedPath("http://nas2skupa.com/5do12/confirmOrder.aspx")
                .appendQueryParameter("orderId", orderId)
                .appendQueryParameter("confirmed", confirmed)
                .appendQueryParameter("note", note)
                .build();
        new HttpRequest(this, uri, false)
                .setOnHttpResultListener(new HttpRequest.OnHttpResultListener() {
                    @Override
                    public void onHttpResult(String result) {
                        Toast.makeText(NewOrderDialog.this, "Potvrda termina je poslana.", Toast.LENGTH_SHORT).show();
                        NewOrderDialog.this.finish();
                    }
                });
    }
}
