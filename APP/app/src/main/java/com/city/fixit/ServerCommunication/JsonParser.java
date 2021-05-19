package com.city.fixit.ServerCommunication;

import com.city.fixit.Utils.FLog;

import org.json.JSONArray;
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

    public static String loginJson(String email, String pass) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("email", email);
            jsonObject.put("password", pass);
        } catch (JSONException e) {
            FLog.e(TAG, "Exception during login JSON: " + e.getMessage());
        }
        return jsonObject.toString();
    }

    public static String reportJson(String type, String description, double lat, double log, String image) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put("type", type);
            jsonObject.put("description", description);
            jsonArray.put(lat); jsonArray.put(log);
            jsonObject.put("coordinates", jsonArray);
            jsonObject.put("image", image);
        } catch (JSONException e) {
            FLog.e(TAG, "Exception during report JSON: " + e.getMessage());
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

    public static String getResponseToken(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getString("token");
        } catch (JSONException e) {
            FLog.e(TAG, "Error parsing response JSON! " + e.getMessage());
        }
        return null;
    }
}
