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

    public static String reportJson(String type, String description, double lat, double lng, String image) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put("type", type);
            jsonObject.put("description", description);
            jsonArray.put(lng); jsonArray.put(lat);
            jsonObject.put("coordinates", jsonArray);
            jsonObject.put("image", image);
        } catch (JSONException e) {
            FLog.e(TAG, "Exception during report JSON: " + e.getMessage());
        }
        return jsonObject.toString();
    }

    public static String updateRegisterJson(String name, String phone, String oldPass, String newPass) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (name != null && !name.isEmpty()) jsonObject.put("name", name);
            if (phone != null && !phone.isEmpty()) jsonObject.put("phone", phone);
            if (oldPass != null && !oldPass.isEmpty()) jsonObject.put("oldPassword", oldPass);
            if (newPass != null && !newPass.isEmpty()) jsonObject.put("newPassword", newPass);
        } catch (JSONException e) {
            FLog.e(TAG, "Exception during updateRegister JSON: " + e.getMessage());
        }
        FLog.d(TAG, "JSON: " + jsonObject.toString());
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

    public static String getResponseName(String res) {
        try {
            JSONObject jsonObject = new JSONObject(res);
            return jsonObject.getString("name");
        } catch (JSONException e) {
            FLog.e(TAG, "Error parsing response JSON! " + e.getMessage());
        }
        return null;
    }

    public static String getResponseEmail(String res) {
        try {
            JSONObject jsonObject = new JSONObject(res);
            return jsonObject.getString("email");
        } catch (JSONException e) {
            FLog.e(TAG, "Error parsing response JSON! " + e.getMessage());
        }
        return null;
    }

    public static String getResponsePhone(String res) {
        try {
            JSONObject jsonObject = new JSONObject(res);
            return jsonObject.getString("phone");
        } catch (JSONException e) {
            FLog.e(TAG, "Error parsing response JSON! " + e.getMessage());
        }
        return null;
    }

    public static int getResponseViews(String res) {
        try {
            JSONObject jsonObject = new JSONObject(res);
            return Integer.parseInt(jsonObject.getString("reportViews"));
        } catch (JSONException e) {
            FLog.e(TAG, "Error parsing response JSON! " + e.getMessage());
        }
        return 0;
    }

    public static int getResponseSolved(String res) {
        try {
            JSONObject jsonObject = new JSONObject(res);
            return Integer.parseInt(jsonObject.getString("reportSolved"));
        } catch (JSONException e) {
            FLog.e(TAG, "Error parsing response JSON! " + e.getMessage());
        }
        return 0;
    }

    public static int getResponseReports(String res) {
        try {
            JSONObject jsonObject = new JSONObject(res);
            return Integer.parseInt(jsonObject.getString("reportNumber"));
        } catch (JSONException e) {
            FLog.e(TAG, "Error parsing response JSON! " + e.getMessage());
        }
        return 0;
    }
}
