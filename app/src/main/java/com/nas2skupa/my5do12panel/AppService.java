package com.nas2skupa.my5do12panel;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import java.util.List;

/**
 * Created by Ranko on 3/19/2015.
 */
public class AppService extends IntentService {
    public static final String DATA_TRANSMIT_ACTION = "com.nas2skupa.my5do12panel.NEW_DATA_RECEIVED";
    public static final String REQUEST_DATA_ACTION = "com.nas2skupa.my5do12panel.REQUEST_DATA";
    private static final int TIMEOUT = 10000;
    private String userId;
    private int year;
    private int month;
    private String lastResponse;
    private ParamsReceiver paramsReceiver;

    private Uri getUri() {
        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath("http://nas2skupa.com/5do12/getProOrders.aspx")
                .appendQueryParameter("proId", userId);
        if (year > 2014 && month > 0)
            builder.appendQueryParameter("year", String.valueOf(year))
                    .appendQueryParameter("month", String.valueOf(month));
        return builder.build();
    }

    public AppService() {
        super("5do12 Service");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        final SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        userId = prefs.getString("id", "");

        year = intent.getIntExtra("year", 0);
        month = intent.getIntExtra("month", 0);

        paramsReceiver = new ParamsReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(REQUEST_DATA_ACTION);
        registerReceiver(paramsReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        this.unregisterReceiver(paramsReceiver);
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        while (true) {
            try {
                String result = new ServiceHandler().makeServiceCall(getUri().toString(), ServiceHandler.GET);
                Log.d("Response: ", "> " + result);

                if (!result.equals(lastResponse)) {

                    Intent dataIntent = new Intent();
                    dataIntent.setAction(DATA_TRANSMIT_ACTION);
                    dataIntent.putExtra("orders", result);
                    sendBroadcast(dataIntent);

                    ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
                    List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                    ComponentName componentInfo = taskInfo.get(0).topActivity;

                    if (!componentInfo.getClassName().equals(Organizer.class.getName())) {
                        Intent dialogIntent = new Intent(this, NewOrderDialog.class);
                        dialogIntent.putExtra("orders", result);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(dialogIntent);
                    }

                    lastResponse = result;
                }
                Thread.sleep(TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class ParamsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            year = intent.getIntExtra("year", year);
            month = intent.getIntExtra("month", month);

            new HttpRequest(getApplicationContext(), getUri(), true)
                    .setOnHttpResultListener(new HttpRequest.OnHttpResultListener() {
                        @Override
                        public void onHttpResult(String result) {
                            Intent dataIntent = new Intent();
                            dataIntent.setAction(DATA_TRANSMIT_ACTION);
                            dataIntent.putExtra("orders", result);
                            sendBroadcast(dataIntent);
                            lastResponse = result;
                        }
                    });
        }
    }
}
