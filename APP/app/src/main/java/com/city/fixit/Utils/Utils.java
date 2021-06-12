package com.city.fixit.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Base64;

import com.city.fixit.UserAuth.MainActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class Utils {
    private static final String TAG = "Utils";

    public static boolean saveToken(Context context, String token) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        if(sharedPreferences == null) return false;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(editor == null) return false;
        editor.putString(Constants.USER_TOKEN_KEY, token);
        editor.apply();
        return true;
    }

    public static String loadToken(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        if(sharedPreferences == null) return null;
        return sharedPreferences.getString(Constants.USER_TOKEN_KEY, null);
    }

    public static boolean clear(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        if(sharedPreferences == null) return false;
        sharedPreferences.edit().remove(Constants.USER_TOKEN_KEY).apply();
        sharedPreferences.edit().remove(Constants.USER_REMEMBER_OPTION).apply();
        return true;
    }

    public static boolean saveRememberMeOption(Context context, boolean option) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        if(sharedPreferences == null) return false;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(editor == null) return false;
        editor.putBoolean(Constants.USER_REMEMBER_OPTION, option);
        editor.apply();
        return true;
    }

    public static boolean loadRememberMeOption(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        if(sharedPreferences == null) return false;
        return sharedPreferences.getBoolean(Constants.USER_REMEMBER_OPTION, false);
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(Constants.USER_SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    public static String prepareErrorMessage(ArrayList<String> errors) {
        StringBuilder finalMessage = new StringBuilder(errors.get(0));
        for(int i = 1; i < errors.size(); i++) {
            finalMessage.append("\n").append(errors.get(i));
        }
        return finalMessage.toString();
    }

    public static String permissionString(int code) {
        switch (code) {
            case Constants.CAMERA_REQUEST_CODE:
                return "Camera";
            case Constants.LOCATION_REQUEST_FINE_CODE:
                return "LocationFine";
            case Constants.LOCATION_REQUEST_COARSE_CODE:
                return "LocationCoarse";
            case Constants.GENERIC_REQUEST_CODE:
                return "GenericRequest";
            default:
                return "Undefined";
        }
    }

    public static String convertBitmapToBase64(Bitmap b) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    public static String createBearerToken(String token) {
        return "Bearer " + token;
    }

    public static void performLogout(Context context) {
        if(Utils.clear(context)) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        } else {
            FLog.e(TAG, "Internal error during app logout");
        }
    }
}
