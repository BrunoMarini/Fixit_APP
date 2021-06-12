package com.city.fixit.Report;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.city.fixit.R;
import com.city.fixit.ServerCommunication.CustomOkHttpClient;
import com.city.fixit.ServerCommunication.JsonParser;
import com.city.fixit.Utils.Constants;
import com.city.fixit.Utils.FLog;
import com.city.fixit.Utils.Utils;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class FormActivity extends Activity implements Callback {
    private static final String TAG = "FormActivity";

    private Context mContext;
    private Callback mCallback;
    private Bitmap mBitmap;
    private Location mLocation;

    private Spinner mSpinnerTypes;
    private ImageView mImageView;
    private EditText mEtDesc;
    private Button mBtnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        mContext = this;
        mCallback = this;
        mImageView = findViewById(R.id.ivReport);

        Intent i = getIntent();
        if(i.hasExtra(Constants.EXTRA_IMAGE) && i.hasExtra(Constants.EXTRA_LOCATION)) {
            mBitmap = (Bitmap)i.getExtras().get(Constants.EXTRA_IMAGE);
            mLocation = (Location) i.getExtras().get(Constants.EXTRA_LOCATION);
            FLog.d(TAG, "Image retrieved successfully!");
            mImageView.setImageBitmap(mBitmap);
        } else {
            FLog.d(TAG, "Error! No image found!");
            //TODO: Will this case be possible?
        }

        mSpinnerTypes = findViewById(R.id.spinnerTypes);
        setSpinnerItems();
        mEtDesc = findViewById(R.id.etUserDescription);

        mBtnSend = findViewById(R.id.btnSubmit);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBarStatus(true);
                String option = mSpinnerTypes.getSelectedItem().toString();
                String desc = mEtDesc.getText().toString();
                if(desc.isEmpty())
                    desc = "";
                if(option == Constants.REPORT_TYPES[0]) {
                    loadingBarStatus(false);
                    showAlertDialog("Erro!", "Escolha o tipo de problema!");
                    return;
                }
                option = Constants.SERVER_EXPECTED_TYPE[mSpinnerTypes.getSelectedItemPosition() - 1];
                if(!CustomOkHttpClient.sendReportRequest(mContext, mCallback,
                        option, desc,
                        mLocation.getLatitude(),
                        mLocation.getLongitude(),
                        Utils.convertBitmapToBase64(mBitmap))) {
                    loadingBarStatus(false);
                    showAlertDialog("Erro!", "Impossível conectar ao servidor,\n Verifique sua conexão!");
                }
            }
        });
        loadingBarStatus(false);
    }

    private void setSpinnerItems() {
        ArrayAdapter <String> arrayAdapter = new ArrayAdapter<>(mContext,
                R.layout.support_simple_spinner_dropdown_item, Constants.REPORT_TYPES);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mSpinnerTypes.setAdapter(arrayAdapter);
    }

    private void showAlertDialog(String title, String message) {
        showAlertDialog(title, message, false);
    }

    private void showAlertDialog(String title, String message, final boolean finish) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(message);
        dialog.setTitle(title);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(finish) {
                    startActivity(new Intent(mContext, HomeActivity.class));
                    finish();
                }
            }
        });
        FormActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public void onFailure(Request request, IOException e) {
        loadingBarStatus(false);
        FLog.d(TAG, "Device not able to connect with server! " + e.getMessage());
        showAlertDialog("Error!", "Impossível conectar com o servidor!\n Por favor tente novamente mais tarde!");
    }

    @Override
    public void onResponse(Response response) throws IOException {
        loadingBarStatus(false);
        if(response.isSuccessful()) {
            FLog.d(TAG, "Successful response!");
            showAlertDialog("Sucesso!", "Obrigado por ajudar a cidade! \n" +
                                                    "Precisamos de mais cidadãos como você :)", true);
        } else {
            String serverResponse = JsonParser.getResponseMessage(response.body().string());
            FLog.d(TAG, "Error! Server response: " + serverResponse);
            if(response.code() == Constants.HTTP_UNAUTHORIZED) {
                Utils.performLogout(mContext);
            }
            showAlertDialog("Error!", serverResponse, true);
        }
    }

    private void loadingBarStatus(final Boolean status){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.loadingPanel).setVisibility(status ? View.VISIBLE : View.GONE);
                mBtnSend.setEnabled(!status);
                mEtDesc.setEnabled(!status);
                mSpinnerTypes.setEnabled(!status);
            }
        });
    }
}