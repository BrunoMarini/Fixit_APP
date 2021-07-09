package com.city.fixit.Utils;

import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.city.fixit.R;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class InputValidator {

    private static String TAG = "InputValidator";

    private static final int NAME_MINIMUM_LENGTH = 4;
    private static final int PASSWORD_MIN_LENGTH = 4;

    private static final String NAME_VALIDATION_ERROR = "Seu nome deve conter pelo menos " + NAME_MINIMUM_LENGTH + " caracteres!";
    private static final String EMAIL_VALIDATION_ERROR = "Email não valido!";
    private static final String PHONE_VALIDATION_ERROR = "Telefone não valido!";
    private static final String PASSWORD_VALIDATION_ERROR = "Senha deve conter mais de " + PASSWORD_MIN_LENGTH + " caracteres!";
    private static final String DIFFERENT_PASSWORDS_VALIDATION_ERROR = "As senhas inseridas são diferentes!";

    public static boolean isNameValid(String name, @Nullable ArrayList<String> errors) {
        if (name.isEmpty() || name.length() < NAME_MINIMUM_LENGTH) {
            if (errors != null) errors.add(NAME_VALIDATION_ERROR);
            return false;
        }
        return true;
    }

    public static boolean isEmailValid(String email, @Nullable ArrayList<String> errors) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (errors != null) errors.add(EMAIL_VALIDATION_ERROR);
            return false;
        }
        return true;
    }

    public static boolean isPhoneValid(String phone, @Nullable ArrayList<String> errors) {
        if (phone.isEmpty() || !Pattern.compile(Constants.PHONE_REGEX).matcher(phone).matches()) {
            if (errors != null) errors.add(PHONE_VALIDATION_ERROR);
            return false;
        }
        return true;
    }

    public static boolean isPasswordValid(String pass, @Nullable ArrayList<String> errors) {
        if (pass.isEmpty() || pass.length() < PASSWORD_MIN_LENGTH) {
            if (errors != null) errors.add(PASSWORD_VALIDATION_ERROR);
            return false;
        }
        return true;
    }

    public static boolean isConfirmPasswordValid(String pass, String confirm, @Nullable ArrayList<String> errors) {
        if (pass.isEmpty() || confirm.isEmpty() || !pass.equals(confirm)) {
            if (errors != null) errors.add(DIFFERENT_PASSWORDS_VALIDATION_ERROR);
            return false;
        }
        return true;
    }

    public static boolean isLoginInputValid(String email, String pass, ArrayList<String> errors) {
        return InputValidator.isEmailValid(email, errors)
                    && InputValidator.isPasswordValid(pass, errors);
    }
}
