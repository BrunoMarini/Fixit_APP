package com.city.fixit.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import java.util.ArrayList;

public class PermissionsManager {
    private static final String TAG = "PermissionsManager";

    public static boolean checkAllPermissions(Context ctx, Activity act) {
        ArrayList<String> permissions = new ArrayList<>();

        if(!isLocationPermissionGranted(ctx, act))
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if(!isCoarsePermissionGranted(ctx, act))
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        if(!isCameraPermissionGranted(ctx, act))
            permissions.add(Manifest.permission.CAMERA);

        if(permissions.size() == 0) {
            FLog.d(TAG, "checkAllPermissions(): All permissions granted! returning true");
            return true;
        }

        String[] p = new String[permissions.size()];
        for(int i = 0; i < permissions.size(); i++) {
            p[i] = permissions.get(i);
        }
        act.requestPermissions(p, Constants.GENERIC_REQUEST_CODE);
        FLog.d(TAG, "checkAllPermissions(): Requesting permission again!");
        return false;
    }

    private static boolean isLocationPermissionGranted(Context ctx, Activity activity) {
        if(ctx.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        FLog.d(TAG, "Permission FINE_LOCATION granted!");
        return true;
    }

    private static boolean isCoarsePermissionGranted(Context ctx, Activity activity) {
        if(ctx.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        FLog.d(TAG, "Permission COARSE_LOCATION granted!");
        return true;
    }

    private static boolean isCameraPermissionGranted(Context ctx, Activity activity) {
        if(ctx.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        FLog.d(TAG, "Permission CAMERA granted!");
        return true;
    }

    public static boolean checkGrantResult(int[] g) {
        for (int value : g) {
            if (value != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }
}
