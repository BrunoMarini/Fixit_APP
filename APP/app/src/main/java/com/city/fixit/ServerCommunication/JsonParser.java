package com.city.fixit.ServerCommunication;

import com.city.fixit.Utils.FLog;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser {
    public static final String TAG = "JsonParser";

    public static String createAccountJson(String name, String email, String phone, String pass) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("name", name);
            jsonObject.put("email", email);
            jsonObject.put("phone", phone);
            jsonObject.put("password", pass);
        } catch (JSONException e) {
            FLog.e(TAG, "Exception during create account JSON: " + e.getMessage());
        }
        return jsonObject.toString();
    }

    public static String getResponseMessage(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getString("message");
        } catch (JSONException e) {
            FLog.e(TAG, "Error parsing response JSON! " + e.getMessage());
        }
        return null;
    }
}
