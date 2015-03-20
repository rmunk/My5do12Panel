package com.nas2skupa.my5do12panel;

/**
 * Created by Ranko on 3/20/2015.
 */

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    // Global variables
    private static final String FEED_URL = "http://nas2skupa.com/5do12/getProOrders.aspx";
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    /*
        * Specify the code you want to run in the sync adapter. The entire
        * sync adapter runs in a background thread, so you don't have to set
        * up your own background processing.
        */
    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult) {
        final SharedPreferences prefs = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String userId = prefs.getString("id", "");
        ServiceHandler sh = new ServiceHandler();
        Uri uri = new Uri.Builder().encodedPath("http://nas2skupa.com/5do12/getProOrders.aspx")
                .appendQueryParameter("proId", userId)
                .build();
        String result = sh.makeServiceCall(uri.toString(), ServiceHandler.GET);
    }
}