package com.city.fixit.Utils;

public class Constants {

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int CAMERA_REQUEST_CODE = 100;
    public static final int LOCATION_REQUEST_FINE_CODE = 99;
    public static final int LOCATION_REQUEST_COARSE_CODE = 101;
    public static final int GENERIC_REQUEST_CODE = 103;

    public static final String EXTRA_IMAGE = "IntentImageExtra";
    public static final String EXTRA_LOCATION = "IntentLocationExtra";
    public static final String EXTRA_ON_FAILURE = "IntentFailureExtra";
    public static final String EXTRA_ON_ERROR = "IntentErrorExtra";
    public static final String EXTRA_ON_SUCCESS = "IntentExtraSuccess";

    public static final String[] REPORT_TYPES = {
        "Selecione", "Depredação", "Via (Rua/Asfalto)", "Vazamento (Agua/Esgoto)", "Acumulo de Lixo", "Enchente"
    };
    public static final String[] SERVER_EXPECTED_TYPE = {
        "Depredation", "Road", "Leak", "Garbage", "Flood"
    };

    public static final String SERVER_SCHEME_HTTPS = "https";
    public static final String SERVER_HOST = "fixit-city.herokuapp.com";
    public static final String SERVER_AUTHORIZATION = "Authorization";

    public static final String SERVER_USER = "user";
    public static final String SERVER_REGISTER = "register";
    public static final String SERVER_LOGIN = "login";
    public static final String SERVER_REPORT = "report";
    public static final String SERVER_NEW_REPORT = "new";
    public static final String SERVER_VALIDATE = "validate";
    public static final String SERVER_INFO = "info";
    public static final String SERVER_STATISTICS = "stats";
    public static final String SERVER_UPDATE_REGISTER = "updateRegister";

    public static final int HTTP_CONFLICT = 409;
    public static final int HTTP_SERVER_ERROR = 500;
    public static final int HTTP_FORBIDDEN = 403;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_UNAUTHORIZED = 401;

    public static final String PHONE_REGEX = "^\\+55 \\(([0-9]+)\\) ([0-9]){5}-([0-9]){4}$";

    public static final String USER_SHARED_PREFERENCES = "UserSharedPreferences";
    public static final String USER_TOKEN_KEY = "UserTokenKey";
    public static final String USER_REMEMBER_OPTION = "UserRememberOption";

    public static final String INTENT_EXTRA_NAME = "userExtraName";
    public static final String INTENT_EXTRA_PHONE = "userExtraPhone";
}
