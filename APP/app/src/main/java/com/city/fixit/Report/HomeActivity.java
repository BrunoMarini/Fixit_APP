package com.city.fixit.Report;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.city.fixit.R;
import com.city.fixit.Utils.Constants;
import com.city.fixit.Utils.FLog;
import com.city.fixit.Utils.PermissionsManager;
import com.city.fixit.Utils.Utils;

public class HomeActivity extends Activity {

    private static final String TAG = "HomeActivity";

    private Context mContext;
    private volatile Location mLocation = null;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            mLocation = location;
        }
    };

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mContext = this;
        if(PermissionsManager.checkAllPermissions(mContext, (Activity) mContext)) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, mLocationListener);
        } else {
            FLog.d(TAG, "Permission not granted! Cant register Location Listener!");
        }

        Button btn = findViewById(R.id.btnNewReport);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PermissionsManager.checkAllPermissions(mContext, (Activity)mContext)
                        && mLocation != null) {
                    startReportFlow();
                } else {
                    showAlertDialog("Erro!", "Permissão necessária para continuar!");
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        FLog.d(TAG, "Permissions request response!");
        if (requestCode == Constants.CAMERA_REQUEST_CODE
                || requestCode == Constants.LOCATION_REQUEST_FINE_CODE
                    || requestCode == Constants.LOCATION_REQUEST_COARSE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                FLog.d(TAG, "Permission granted: " + Utils.permissionString(requestCode));
                if(PermissionsManager.checkAllPermissions(mContext, (Activity)mContext)) {
                    FLog.d(TAG, "Permission granted!");
                    showAlertDialog("Permissao Concedida!", "Tente executar a operação novamente!");
                } else {
                    FLog.d(TAG, "User did not consent all permissions!");
                    showAlertDialog("Erro", "Não é possível realizar essa ação sem " +
                                                "todas as permissões concedidas!");
                }
            } else {
                FLog.d(TAG, "Permissions not granted! " + Utils.permissionString(requestCode));
            }
        }
    }

    private void startReportFlow() {
        FLog.d(TAG, "New report will be performed!");
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), Constants.REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FLog.d(TAG, "Image captured!");
        if(requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if(extras != null && extras.get("data") != null && mLocation != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                Intent intent = new Intent(mContext, FormActivity.class);
                intent.putExtra(Constants.EXTRA_IMAGE, imageBitmap);
                intent.putExtra(Constants.EXTRA_LOCATION, mLocation);
                startActivity(intent);
            } else {
                showAlertDialog("Erro", "Erro ao carregar a imagem e/ou localização");
            }
        }
    }

    private void showAlertDialog(String title, String message) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(message);
        dialog.setTitle(title);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Do nothing when ok pressed?
            }
        });
        HomeActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });
    }
}