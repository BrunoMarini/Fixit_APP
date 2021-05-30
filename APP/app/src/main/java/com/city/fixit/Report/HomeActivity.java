package com.city.fixit.Report;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.city.fixit.R;
import com.city.fixit.UserAuth.MainActivity;
import com.city.fixit.UserAuth.UserInfoActivity;
import com.city.fixit.UserAuth.UserStatisticsActivity;
import com.city.fixit.Utils.Constants;
import com.city.fixit.Utils.FLog;
import com.city.fixit.Utils.PermissionsManager;
import com.city.fixit.Utils.Utils;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements LocationListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";

    private Context mContext;
    private volatile Location mLocation = null;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;

    private Button mBtnNew;
    private boolean mPressedOnce = false;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = this;

        mContext = this;
        if(PermissionsManager.checkAllPermissions(mContext, (Activity) mContext)) {
            FLog.d(TAG, "All permissions granted! Registering Location!");
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, mLocationListener);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    0, 0, mLocationListener);
        } else {
            FLog.d(TAG, "Permission not granted! Cant register Location Listener!");
        }

        mBtnNew = findViewById(R.id.btnNewReport);
        mBtnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FLog.d(TAG, "onClick(): New Report pressed!");
                if(PermissionsManager.checkAllPermissions(mContext, (Activity) mContext)) {
                    startReportFlow();
                } else {
                    showAlertDialog("Erro!", "Permissão necessária para continuar!\n" +
                            "Vá em configurações -> APPs Procure o FixIt e conceda as permissões necessárias!");
                }
            }
        });

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mToolbar = findViewById(R.id.toolbar);

        setupNavView();
        setOnBackPressedTwice();
        loadingBarStatus(false);
    }

    private void setupNavView() {
        setSupportActionBar(mToolbar);

        mNavigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                                                mToolbar, R.string.navigation_drawer_open,
                                                    R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void setOnBackPressedTwice() {
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    return;
                }

                if (mPressedOnce) {
                    //TODO: Call logout
                    finish();
                }
                mPressedOnce = true;
                Toast.makeText(mContext, "Pressione VOLTAR novamente para sair", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPressedOnce = false;
                    }
                }, 2000);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        FLog.d(TAG, "Permissions request response!");
        if (requestCode == Constants.GENERIC_REQUEST_CODE) {
            if (grantResults.length > 0 && PermissionsManager.checkGrantResult(grantResults)) {
                FLog.d(TAG, "Permission granted: " + Utils.permissionString(requestCode));
                if(PermissionsManager.checkAllPermissions(mContext, (Activity)mContext)) {
                    FLog.d(TAG, "Permission granted!");
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            100, 100, mLocationListener);
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
        super.onActivityResult(requestCode, resultCode, data);
        FLog.d(TAG, "Image captured!");
        if (requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            loadingBarStatus(true);
            Bundle extras = data.getExtras();
            if (extras != null && extras.get("data") != null) {
                int t = 20;
                do {
                    FLog.d(TAG, "Loop waiting for location " + t);
                    t--;
                    synchronized (mContext) {
                        if (mLocation != null) {
                            break;
                        }
                    }
                    try { Thread.sleep(200); } 
                    catch (InterruptedException e) { e.printStackTrace(); }
                } while (t > 0);

                if (mLocation != null) {
                    mLocationManager.removeUpdates(mLocationListener);
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    Intent intent = new Intent(mContext, FormActivity.class);
                    intent.putExtra(Constants.EXTRA_IMAGE, imageBitmap);
                    intent.putExtra(Constants.EXTRA_LOCATION, mLocation);
                    loadingBarStatus(false);
                    startActivity(intent);
                } else {
                    boolean gpsEnabled = false;
                    boolean networkEnabled = false;
                    try {
                        gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        networkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                    } catch (Exception ex) {
                        FLog.d(TAG, "Error getting services provided!");
                    }
                    FLog.d(TAG, "Location null! GpsEnabled: " + gpsEnabled + " Network: " + networkEnabled);
                    loadingBarStatus(false);
                    showAlertDialog("Erro!", "Não foi possível pegar sua localizacao atual!\nSua internet e sua localização estão ativos?");
                }
            } else {
                loadingBarStatus(false);
                showAlertDialog("Erro", "Erro ao carregar a imagem!\nPor favor tente novamente!");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationManager.removeUpdates(mLocationListener);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        FLog.d(TAG, "onLocationChanged");
        synchronized (mContext) {
            FLog.d(TAG, "updated");
            mLocation = location;
        }
    }

    private void loadingBarStatus(final Boolean status){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.loadingPanel).setVisibility(status ? View.VISIBLE : View.GONE);
                mBtnNew.setEnabled(!status);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_home:
                openWebSite();
                break;
            case R.id.user_info:
                startActivity(new Intent(mContext, UserInfoActivity.class));
                break;
            case R.id.user_statistics:
                startActivity(new Intent(mContext, UserStatisticsActivity.class));
                break;
            case R.id.change_user_info:
                //TODO: do
                break;
            case R.id.app_logout:
                logout();
                break;
        }
        return true;
    }

    private void openWebSite() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://fixit-city.herokuapp.com/"));
        startActivity(browserIntent);
    }

    private void logout() {
        if(Utils.clear(mContext)) {
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            showAlertDialog("Erro", "Aconteceu um erro interno!");
        }
    }
}