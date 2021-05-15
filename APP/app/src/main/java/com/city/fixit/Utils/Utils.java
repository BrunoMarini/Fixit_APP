package com.city.fixit.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.ArrayList;
import java.util.stream.Collectors;

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

    public static String prepareErrorMessage(ArrayList<String> errors) {
        StringBuilder finalMessage = new StringBuilder(errors.get(0));
        for(int i = 1; i < errors.size(); i++) {
            finalMessage.append("\n").append(errors.get(i));
        }
        return finalMessage.toString();
    }
}
