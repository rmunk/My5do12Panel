package com.nas2skupa.my5do12panel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.IntentService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Ranko on 3/19/2015.
 */
public class AppService extends IntentService {
    private static final int TIMEOUT = 10000;
    private String lastResponse;

    public AppService() {
        super("5do12 Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        while (true) {
            try {
                final SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
                String userId = prefs.getString("id", "");
                ServiceHandler sh = new ServiceHandler();
                Uri uri = new Uri.Builder().encodedPath("http://nas2skupa.com/5do12/getProOrders.aspx")
                        .appendQueryParameter("proId", userId)
                        .build();
                String result = sh.makeServiceCall(uri.toString(), ServiceHandler.GET);
                Log.d("Response: ", "> " + result);

                if (!result.equals(lastResponse)) {
//                    Toast.makeText(this, "Novi termin primljen.", Toast.LENGTH_LONG).show();
                    final EditText note = new EditText(this);
                    AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Potvrda termina")
                        .setMessage("Unesite poruku za korisnika:")
                        .setView(note)
                        .setPositiveButton("Potvrdi termin", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
//                                    sendConfirmation(orderId, "1", note.getText().toString());
//                                    orderConfirmation.setVisibility(View.GONE);
                            }
                        })
                        .setNegativeButton("Odustani", null).create();
                    dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    dialog.show();
                }

                lastResponse = result;

                Thread.sleep(TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
