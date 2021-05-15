package com.city.fixit.ServerCommunication;

import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import com.city.fixit.Utils.Constants;
import com.city.fixit.Utils.FLog;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class CustomOkHttpClient {

    private static final String TAG = "CustomOkHttpClient";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static boolean sendCreateAccountRequestSync(Context context, Callback callback,
                                                       String name, String email, String phone, String pass) {
        if(!isNetworkAvailable(context)) {
            FLog.d(TAG, "Network not available!");
            return false;
        }

        HttpUrl url = new HttpUrl.Builder()
                .scheme(Constants.SERVER_SCHEME_HTTPS)
                .host(Constants.SERVER_HOST)
                .addPathSegment(Constants.SERVER_USER)
                .addPathSegment(Constants.SERVER_REGISTER)
                .build();

        FLog.d(TAG, "URL: " + url);

        String json = JsonParser.createAccountJson(name, email, phone, pass);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(JSON, json))
                .build();

        FLog.d(TAG, "Enqueuing new retrofit callback!");
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(callback);

        return true;
    }

    private static Boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null)
                return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                                                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }
}
