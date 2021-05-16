package com.city.fixit.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class PermissionsManager {
    private static final String TAG = "PermissionsManager";

    public static boolean checkAllPermissions(Context ctx, Activity act) {
        boolean result = isLocationPermissionGranted(ctx, act)
                            && isCoarsePermissionGranted(ctx, act)
                                && isCameraPermissionGranted(ctx, act);
        return result;
    }

    public static boolean isLocationPermissionGranted(Context ctx, Activity activity) {
        if(ctx.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    Constants.LOCATION_REQUEST_FINE_CODE);
            return false;
        }
        return true;
    }

    public static boolean isCoarsePermissionGranted(Context ctx, Activity activity) {
        if(ctx.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(
                    new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                    Constants.LOCATION_REQUEST_COARSE_CODE);
            return false;
        }
        return true;
    }

    public static boolean isCameraPermissionGranted(Context ctx, Activity activity) {
        if(ctx.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(
                    new String[] { Manifest.permission.CAMERA },
                    Constants.CAMERA_REQUEST_CODE);
            return false;
        }
        return true;
    }
}
