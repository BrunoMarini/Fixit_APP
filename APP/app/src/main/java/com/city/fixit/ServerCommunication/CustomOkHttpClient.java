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
import com.city.fixit.Utils.Utils;
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

    public static boolean sendUserAuthRequest(Context context, Callback callback, String token) {
        if(!isNetworkAvailable(context)) {
            FLog.d(TAG, "Network not available!");
            return false;
        }

        HttpUrl url = new HttpUrl.Builder()
                .scheme(Constants.SERVER_SCHEME_HTTPS)
                .host(Constants.SERVER_HOST)
                .addPathSegment(Constants.SERVER_USER)
                .addPathSegment(Constants.SERVER_VALIDATE)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", Utils.createBearerToken(token))
                .post(RequestBody.create(JSON, ""))
                .build();

        FLog.d(TAG, "Enqueuing retrofit callback!");
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(callback);

        return true;
    }

    public static boolean sendCreateAccountRequest(Context context, Callback callback,
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

        String json = JsonParser.createAccountJson(name, email, phone, pass);
        performRequest(url, json, callback);
        return true;
    }

    public static boolean sendLoginRequest(Context context, Callback callback,
                                            String email, String pass) {
        if(!isNetworkAvailable(context)) {
            FLog.d(TAG, "Network not available!");
            return false;
        }
        HttpUrl url = new HttpUrl.Builder()
                .scheme(Constants.SERVER_SCHEME_HTTPS)
                .host(Constants.SERVER_HOST)
                .addPathSegment(Constants.SERVER_USER)
                .addPathSegment(Constants.SERVER_LOGIN)
                .build();

        String json = JsonParser.loginJson(email, pass);
        performRequest(url, json, callback);
        return true;
    }

    public static boolean sendReportRequest(Context context, Callback callback,
                    String type, String description, double lat, double log, String image) {
        if(!isNetworkAvailable(context)) {
            FLog.d(TAG, "Network not available");
            return false;
        }

        String auth = Utils.loadToken(context);
        if(auth == null) {
            FLog.d(TAG, "Token null");
            return false;
        }

        HttpUrl url = new HttpUrl.Builder()
                .scheme(Constants.SERVER_SCHEME_HTTPS)
                .host(Constants.SERVER_HOST)
                .addPathSegment(Constants.SERVER_REPORT)
                .addPathSegment(Constants.SERVER_NEW_REPORT)
                .build();
        String json = JsonParser.reportJson(type, description, lat, log, image);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", Utils.createBearerToken(auth))
                .post(RequestBody.create(JSON, json))
                .build();
        FLog.d(TAG, "Enqueuing retrofit callback!");
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(callback);
        return true;
    }

    private static void performRequest(HttpUrl url, String postJson, Callback callback) {
        FLog.d(TAG, "URL: " + url);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(JSON, postJson))
                .build();

        FLog.d(TAG, "Enqueuing new retrofit callback!");
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(callback);
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
