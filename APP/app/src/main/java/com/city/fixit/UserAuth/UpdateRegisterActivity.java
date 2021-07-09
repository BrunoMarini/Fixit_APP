package com.city.fixit.UserAuth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.fragment.app.DialogFragment;

import com.city.fixit.R;
import com.city.fixit.ServerCommunication.CustomOkHttpClient;
import com.city.fixit.ServerCommunication.JsonParser;
import com.city.fixit.Utils.Constants;
import com.city.fixit.Utils.FLog;
import com.city.fixit.Utils.InputValidator;
import com.city.fixit.Utils.Utils;
import com.santalu.maskedittext.MaskEditText;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;

public class UpdateRegisterActivity extends Activity implements Callback {

    private final String TAG = "UpdateRegisterActivity";

    private int PHONE_COUNTRY_SUBSTRING = 4;

    private Context mContext;
    private Callback mCallback;

    private boolean mIsNameEnabled, mIsPhoneEnabled, mIsPassEnabled;

    private String mOldName;
    private String mOldPhone;

    private EditText mEtName;
    private MaskEditText mEtPhone;
    private EditText mEtNewPass;
    private EditText mEtConfirmPass;

    private ImageButton mBtnEnableName;
    private ImageButton mBtnEnablePhone;
    private ImageButton mBtnEnablePassword;

    private RelativeLayout mConfirmPassLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_register);

        mContext = this;
        mCallback = this;

        mIsNameEnabled = mIsPassEnabled = mIsPhoneEnabled = false;

        mEtName = findViewById(R.id.et_update_name);
        mEtPhone = findViewById(R.id.et_update_phone);
        mEtNewPass = findViewById(R.id.et_update_password);
        mEtConfirmPass = findViewById(R.id.et_update_confirm_password);

        mConfirmPassLayout = findViewById(R.id.rl_update_confirm_password);

        mBtnEnableName = findViewById(R.id.btn_update_name);
        mBtnEnableName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performUpdateButtonClick(mEtName);
            }
        });
        mBtnEnablePhone = findViewById(R.id.btn_update_phone);
        mBtnEnablePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performUpdateButtonClick(mEtPhone);
            }
        });
        mBtnEnablePassword = findViewById(R.id.btn_update_password);
        mBtnEnablePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performUpdateButtonClick(mEtNewPass);
                if (isEditTextEnabled(mEtNewPass, false)) {
                    mConfirmPassLayout.setVisibility(View.VISIBLE);
                } else {
                    mConfirmPassLayout.setVisibility(View.GONE);
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mOldName = extras.getString(Constants.INTENT_EXTRA_NAME, null);
            mOldPhone = extras.getString(Constants.INTENT_EXTRA_PHONE, null);
        }
        if(TextUtils.isEmpty(mOldName) || TextUtils.isEmpty(mOldPhone)) {
            requestUserInfo();
        } else {
            loadingBarStatus(false);
        }

        Button btn = findViewById(R.id.btn_send_update_request);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBarStatus(true);
                ArrayList<String> e = new ArrayList<>();
                if (isInputValid(e)) {
                    askUserCurrentPassword();
                } else {
                    loadingBarStatus(false);
                    showAlertMessage(Utils.prepareErrorMessage(e));
                }
            }
        });
    }

    public boolean isInputValid(ArrayList<String> errors) {
        boolean ret = true;
        if (mIsNameEnabled) {
            if (ret) ret = InputValidator.isNameValid(mEtName.getText().toString(), errors);
        }
        if (mIsPhoneEnabled) {
            if (ret) ret = InputValidator.isPhoneValid(mEtPhone.getText().toString(), errors);
        }
        if (mIsPassEnabled) {
            String pass = mEtNewPass.getText().toString();
            if (ret) ret = InputValidator.isPasswordValid(pass, errors)
                                && InputValidator.isConfirmPasswordValid(pass,
                                                mEtConfirmPass.getText().toString(), errors);
        }
        return ret;
    }

    private void askUserCurrentPassword() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText et = new EditText(mContext);
        et.setHint(R.string.hint_password);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alert.setTitle(R.string.tv_old_password);
        //alert.setMessage("Entre com a senha atual");
        alert.setView(et);

        alert.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CustomOkHttpClient.sendUpdateRegister(mContext, mCallback,
                        (mIsNameEnabled ? mEtName.getText().toString() : null),
                        (mIsPhoneEnabled ? mEtPhone.getText().toString() : null),
                        et.getText().toString(),
                        (mIsPassEnabled ? mEtNewPass.getText().toString() : null));
            }});

        alert.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadingBarStatus(false);
            }});

        alert.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        disableEditText(mEtName);
        disableEditText(mEtPhone);
        disableEditText(mEtNewPass);
    }

    private void enableEditText(EditText editText) {
        editText.setFocusableInTouchMode(true);
        editText.setFocusable(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);
        editText.requestFocus();
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setText("");
    }

    private boolean isEditTextEnabled(EditText et, boolean changeValue) {
        boolean ret = false;
        if (et.getId() == mEtName.getId()) {
            ret = mIsNameEnabled;
            if (changeValue) mIsNameEnabled = !mIsNameEnabled;
        } else if (et.getId() == mEtPhone.getId()) {
            ret = mIsPhoneEnabled;
            if (changeValue) mIsPhoneEnabled = !mIsPhoneEnabled;
        } else if (et.getId() == mEtNewPass.getId()) {
            ret = mIsPassEnabled;
            if (changeValue) mIsPassEnabled = !mIsPassEnabled;
        }
        return ret;
    }

    private void performUpdateButtonClick(EditText et) {
        if (isEditTextEnabled(et, true)) {
            disableEditText(et);
        } else {
            enableEditText(et);
            openKeyboard(et);
        }
    }

    private void openKeyboard(EditText et) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(et.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }

    private void requestUserInfo() {
        if(!CustomOkHttpClient.sendGetUserInfoRequest(mContext, mCallback)) {
            loadingBarStatus(false);
            showAlertMessage("Impossível conectar ao servidor,\n Verifique sua conexão!");
        }
    }

    private void showAlertMessage(String content) {
        showAlertMessage("Erro!", content, false);
    }
    private void showAlertMessage(String title, String content, final boolean finishAfterOk) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(title);
        dialog.setMessage(content);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (finishAfterOk)
                    finish();
            }
        });
        UpdateRegisterActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public void onFailure(Request request, IOException e) {
        FLog.d(TAG, "Device not able to connect with server! " + e.getMessage());
        loadingBarStatus(false);
        showAlertMessage("Impossível conectar com o servidor!\n Por favor tente novamente mais tarde!");
    }

    @Override
    public void onResponse(Response response) throws IOException {
        if(response.isSuccessful()) {
            FLog.d(TAG, "Response successful!");
            String res = response.body().string();
            FLog.d(TAG, res);
            String message = JsonParser.getResponseMessage(res);
            if (message != null) {
                showAlertMessage("Sucesso!", message, true);
                loadingBarStatus(false);
            }

            String name = JsonParser.getResponseName(res);
            String phone = JsonParser.getResponsePhone(res);
            if (name != null && phone != null)
                updateUi(name, phone);
        } else {
            String serverMessage = JsonParser.getResponseMessage(response.body().string());
            FLog.d(TAG, "Error! Server Response: " + "[" + response.code() + "] " + serverMessage);
            loadingBarStatus(false);
            showAlertMessage(serverMessage);
        }
    }

    private void updateUi(final String name, final String phone) {
        mOldName = name;
        mOldPhone = phone.substring(PHONE_COUNTRY_SUBSTRING);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEtName.setHint(mOldName);
                mEtPhone.setHint(mOldPhone);
                loadingBarStatus(false);
            }
        });
    }

    private void loadingBarStatus(final Boolean status){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEtName.setEnabled(!status);
                mEtPhone.setEnabled(!status);
                mEtNewPass.setEnabled(!status);
                mEtConfirmPass.setEnabled(!status);
                mBtnEnableName.setEnabled(!status);
                mBtnEnablePhone.setEnabled(!status);
                mBtnEnablePassword.setEnabled(!status);
                findViewById(R.id.loadingPanel).setVisibility(status ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}