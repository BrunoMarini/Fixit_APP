package com.city.fixit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.city.fixit.ServerCommunication.CustomOkHttpClient;
import com.city.fixit.ServerCommunication.JsonParser;
import com.city.fixit.Utils.Constants;
import com.city.fixit.Utils.FLog;
import com.santalu.maskedittext.MaskEditText;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
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
        mEtEmail = findViewById(R.id.etEmail);
        mEtPhone = findViewById(R.id.etPhone);
        mEtPassword = findViewById(R.id.etPassword);
        mEtConfirmPass = findViewById(R.id.etConfirmPass);

        mBtnRegister = findViewById(R.id.btnRegisterUser);
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FLog.d(TAG, "Starting register!");
                clearPreviousErrors();

                String name = mEtName.getText().toString();
                String email = mEtEmail.getText().toString();
                String phone = (mEtPhone.getText() != null ? mEtPhone.getText().toString() : "");
                String pass = mEtPassword.getText().toString();
                String confirm = mEtConfirmPass.getText().toString();

                if(isInputValid(name, email, phone, pass, confirm)) {
                    FLog.d(TAG, "Valid inputs! Proceeding with the requests!");
                    boolean ret = CustomOkHttpClient.sendCreateAccountRequestSync(mContext, mCallback, name, email, phone, pass);
                    if(!ret) {
                        showAlertDialog("Erro!", "Impossível conectar com o servidor,\n" +
                                                                    "Verifique sua conexão!");
                    }
                }
            }
        });

    }

    private boolean isInputValid(String name, String email, String phone, String pass, String confirm) {
        StringBuilder s = new StringBuilder();
        if(name.length() < 4) {
            mEtName.setBackgroundResource(R.drawable.edit_text_error_shape);
            s.append("Seu nome deve conter pelo menos 4 caracteres!\n");
        }

        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEtEmail.setBackgroundResource(R.drawable.edit_text_error_shape);
            s.append("Email não valido!\n");
        }

        if(!Pattern.compile(Constants.PHONE_REGEX).matcher(phone).matches()) {
            mEtPhone.setBackgroundResource(R.drawable.edit_text_error_shape);
            s.append("Telefone não valido!\n");
        }

        if(pass.length() < 4) {
            mEtPassword.setBackgroundResource(R.drawable.edit_text_error_shape);
            mEtConfirmPass.setText("");
            s.append("Senha deve conter mais de 4 caracteres!\n");
        } else if (!pass.equals(confirm)){
            mEtPassword.setBackgroundResource(R.drawable.edit_text_error_shape);
            mEtConfirmPass.setBackgroundResource(R.drawable.edit_text_error_shape);
            s.append("As senhas inseridas são diferentes!\n");
        }

        if(s.length() > 0) {
            Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
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
        if(response.isSuccessful()) {
            FLog.d(TAG, "Successful response!");
            showAlertDialog("Sucesso!","Usuário cadastrado com sucesso!\n" +
                                                        "Aguardando confirmação por email!");
            // TODO: Start login activity :)
        } else {
            FLog.d(TAG, "Error! Server Response: " +
                    JsonParser.getResponseMessage(response.body().string()));
            switch (response.code()) {
                case Constants.HTTP_CONFLICT:
                    showAlertDialog("Erro!", "Usuário já cadastrado na base de dados!");
                    break;
                case Constants.HTTP_SERVER_ERROR:
                    showAlertDialog("Erro!", "Aconteceu um erro imprevisto no servidor!");
                    break;
            }
        }
    }

    @Override
    public void onFailure(Request request, IOException e) {
        FLog.d(TAG, "Device not able to connect with server! " + e.getMessage());
        showAlertDialog("Error!", "Impossível conectar com o servidor!\n" +
                                                    "Por favor tente novamente mais tarde!");
    }

    private void showAlertDialog(String title, String message) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(message);
        dialog.setTitle(title);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing when user press ok?
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
}