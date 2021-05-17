package com.city.fixit.UserAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.city.fixit.R;
import com.city.fixit.Report.HomeActivity;
import com.city.fixit.Utils.Constants;
import com.city.fixit.Utils.FLog;
import com.city.fixit.Utils.PermissionsManager;
import com.city.fixit.Utils.Utils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Context mContext = this;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        if(intent.hasExtra(Constants.EXTRA_ON_SUCCESS)) {
            startActivity(new Intent(mContext, HomeActivity.class));
        }
        if(intent.hasExtra(Constants.EXTRA_ON_ERROR)) {
            FLog.d(TAG, "User not valid!");
            //TODO: Show error message?
        }
        if(intent.hasExtra(Constants.EXTRA_ON_FAILURE)) {
            FLog.d(TAG, "Device not able to connect with server!");
            showAlertMessage("Impossível conectar com o servidor!\n Verifique sua conexão e tente novamente mais tarde!");
        }

        Button btnLogin = findViewById(R.id.btnMainLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, LoginActivity.class));
            }
        });
        Button btnRegister = findViewById(R.id.btnMainRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, RegisterActivity.class));
            }
        });

        if (PermissionsManager.checkAllPermissions(mContext, (Activity) mContext)) {
            FLog.d(TAG, "All permissions already granted!");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                                    @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        FLog.d(TAG, "Permissions requested!");
        if (requestCode == Constants.GENERIC_REQUEST_CODE) {
            if (grantResults.length > 0 && PermissionsManager.checkGrantResult(grantResults)) {
                FLog.d(TAG, "Permission granted: " + Utils.permissionString(requestCode));
            } else {
                FLog.d(TAG, "Permissions not granted! " + Utils.permissionString(requestCode));
            }
        }
    }

    private void showAlertMessage(String s) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Error!");
        dialog.setMessage(s);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing when user press ok
            }
        });
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });
    }
}