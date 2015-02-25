package com.nas2skupa.my5do12panel;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by Ranko on 22.11.2014..
 */
public class HttpRequest {
    private boolean silent;
    private Context context;
    private ProgressDialog progressDialog;

    private Boolean isNetAvailable(Context con) {

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (wifiInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public HttpRequest(Context context, Uri uri, boolean silent) {
        this.context = context;
        this.silent = silent;
        new HttpWorker().execute(uri);
    }

    private class HttpWorker extends AsyncTask<Uri, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!isNetAvailable(context)) {
                Toast.makeText(context, "Mreža je nedostupna, molimo omogućite pristup mreži.", Toast.LENGTH_SHORT).show();
                cancel(true);
                return;
            }
            if (silent) return;
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Molimo pričekajte...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Uri... params) {
            ServiceHandler serviceHandler = new ServiceHandler();
            return serviceHandler.makeServiceCall(params[0].toString(), ServiceHandler.GET);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!silent && progressDialog.isShowing())
                progressDialog.dismiss();
            onHttpResultListener.onHttpResult(result);
        }
    }

    private OnHttpResultListener onHttpResultListener;

    public interface OnHttpResultListener {
        public void onHttpResult(String result);
    }

    public void setOnHttpResultListener(OnHttpResultListener listener) {
        onHttpResultListener = listener;
    }
}
