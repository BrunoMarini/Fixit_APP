package com.city.fixit.UserAuth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.city.fixit.R;
import com.city.fixit.ServerCommunication.CustomOkHttpClient;
import com.city.fixit.ServerCommunication.JsonParser;
import com.city.fixit.Utils.Constants;
import com.city.fixit.Utils.FLog;
import com.city.fixit.Utils.Utils;
import com.santalu.maskedittext.MaskEditText;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class RegisterActivity extends Activity implements Callback {

    private static final String TAG = "RegisterActivity";

    private Button mBtnRegister;

    private Context mContext;
    private Callback mCallback;
    private EditText mEtName;
    private EditText mEtEmail;
    private MaskEditText mEtPhone;
    private EditText mEtPassword;
    private EditText mEtConfirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mContext = this;
        mCallback = this;
        mEtName = findViewById(R.id.etName);
        mEtEmail = findViewById(R.id.etLoginEmail);
        mEtPhone = findViewById(R.id.etPhone);
        mEtPassword = findViewById(R.id.etLoginPassword);
        mEtConfirmPass = findViewById(R.id.etConfirmPass);

        mBtnRegister = findViewById(R.id.btnRegisterUser);
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FLog.d(TAG, "Starting register!");
                loadingBarStatus(true);
                clearPreviousErrors();

                String name = mEtName.getText().toString();
                String email = mEtEmail.getText().toString();
                String phone = (mEtPhone.getText() != null ? mEtPhone.getText().toString() : "");
                String pass = mEtPassword.getText().toString();
                String confirm = mEtConfirmPass.getText().toString();

                if(isInputValid(name, email, phone, pass, confirm)) {
                    FLog.d(TAG, "Valid inputs! Proceeding with the requests!");
                    boolean ret = CustomOkHttpClient.sendCreateAccountRequest(mContext, mCallback, name, email, phone, pass);
                    if(!ret) {
                        loadingBarStatus(false);
                        showAlertDialog("Erro!", "Impossível conectar ao servidor,\n Verifique sua conexão!");
                    }
                }
            }
        });
        loadingBarStatus(false);
    }

    private boolean isInputValid(String name, String email, String phone, String pass, String confirm) {
        ArrayList<String> s = new ArrayList<>();
        if(name.length() < 4) {
            mEtName.setBackgroundResource(R.drawable.edit_text_error_shape);
            s.add("Seu nome deve conter pelo menos 4 caracteres!");
        }

        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEtEmail.setBackgroundResource(R.drawable.edit_text_error_shape);
            s.add("Email não valido!");
        }

        if(!Pattern.compile(Constants.PHONE_REGEX).matcher(phone).matches()) {
            mEtPhone.setBackgroundResource(R.drawable.edit_text_error_shape);
            s.add("Telefone não valido!");
        }

        if(pass.length() < Constants.PASSWORD_MIN_LENGTH) {
            mEtPassword.setBackgroundResource(R.drawable.edit_text_error_shape);
            mEtConfirmPass.setText("");
            s.add("Senha deve conter mais de 4 caracteres!");
        } else if (!pass.equals(confirm)){
            mEtPassword.setBackgroundResource(R.drawable.edit_text_error_shape);
            mEtConfirmPass.setBackgroundResource(R.drawable.edit_text_error_shape);
            s.add("As senhas inseridas são diferentes!");
        }

        if(s.size() > 0) {
            loadingBarStatus(false);
            Toast.makeText(mContext, Utils.prepareErrorMessage(s), Toast.LENGTH_LONG).show();
            FLog.d(TAG, "Stopped! User inserted invalid input!");
            return false;
        }
        return true;
    }

    private void clearPreviousErrors() {
        mEtName.setBackgroundResource(R.drawable.edit_text_shape);
        mEtEmail.setBackgroundResource(R.drawable.edit_text_shape);
        mEtPhone.setBackgroundResource(R.drawable.edit_text_shape);
        mEtPassword.setBackgroundResource(R.drawable.edit_text_shape);
        mEtConfirmPass.setBackgroundResource(R.drawable.edit_text_shape);
    }

    @Override
    public void onResponse(Response response) throws IOException {
        loadingBarStatus(false);
        if(response.isSuccessful()) {
            FLog.d(TAG, "Successful response!");
            showAlertDialog("Sucesso!","Usuário cadastrado com sucesso!\n" +
                                                        "Aguardando confirmação por email!\n" +
                                                            "(Não se esqueça de checar sua caixa de SPAM!)", true);
        } else {
            String serverMessage = JsonParser.getResponseMessage(response.body().string());
            FLog.d(TAG, "Error! Server Response: " + serverMessage);
            showAlertDialog("Erro!", serverMessage);
        }
    }

    @Override
    public void onFailure(Request request, IOException e) {
        loadingBarStatus(false);
        FLog.d(TAG, "Device not able to connect with server! " + e.getMessage());
        showAlertDialog("Error!", "Impossível conectar com o servidor!\n Por favor tente novamente mais tarde!");
    }

    private void showAlertDialog(String title, String message) {
        showAlertDialog(title, message, false);
    }

    private void showAlertDialog(String title, String message, final boolean logged) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(message);
        dialog.setTitle(title);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(logged) {
                    startActivity(new Intent(mContext, LoginActivity.class));
                    finish();
                }
            }
        });
        RegisterActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });
    }

    private void loadingBarStatus(final Boolean status){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.loadingPanel).setVisibility(status ? View.VISIBLE : View.GONE);
                mEtName.setEnabled(!status);
                mEtEmail.setEnabled(!status);
                mEtPhone.setEnabled(!status);
                mEtPassword.setEnabled(!status);
                mEtConfirmPass.setEnabled(!status);
                mBtnRegister.setEnabled(!status);
            }
        });
    }
}