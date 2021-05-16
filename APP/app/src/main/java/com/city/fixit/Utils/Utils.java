package com.city.fixit.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class Utils {
    public static boolean saveToken(Context context, String token) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(Constants.USER_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if(sharedPreferences == null) return false;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(editor == null) return false;
        editor.putString(Constants.USER_TOKEN_KEY, token);
        editor.apply();
        return true;
    }

    public static String loadToken(Context context) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(Constants.USER_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constants.USER_TOKEN_KEY, null);
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
}
