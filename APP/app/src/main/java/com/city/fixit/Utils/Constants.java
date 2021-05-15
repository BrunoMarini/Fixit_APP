package com.city.fixit.Utils;

public class Constants {

    public final static String SERVER_SCHEME_HTTPS = "https";
    public final static String SERVER_HOST = "fixit-city.herokuapp.com";

    public final static String SERVER_USER = "user";
    public final static String SERVER_REGISTER = "register";
    public final static String SERVER_LOGIN = "login";

    public static final int HTTP_CONFLICT = 409;
    public static final int HTTP_SERVER_ERROR = 500;
    public static final int HTTP_FORBIDDEN = 403;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_UNAUTHORIZED = 401;

    public final static String PHONE_REGEX = "^\\+55 \\(([0-9]+)\\) ([0-9]){5}-([0-9]){4}$";

    public final static int PASSWORD_MIN_LENGTH = 4;

    public static final String USER_SHARED_PREFERENCES = "UserSharedPreferences";
    public static final String USER_TOKEN_KEY = "UserTokenKey";
}
