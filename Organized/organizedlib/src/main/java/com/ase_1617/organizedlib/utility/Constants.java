package com.ase_1617.organizedlib.utility;

import android.util.Log;

/**
 * Created by bob on 25.03.17.
 *
 * Class containing the global app constants.
 *
 * SERVER_URL_BASE, CLIENT_ID and CLIENT_SECRET are not final due to testing purposes.
 */

public class Constants {

    //Network
    public static String SERVER_URL_BASE = "http://192.168.178.20";
    public static String TOKEN_URL = SERVER_URL_BASE+ ":8000/o/token/";
    public static String SIGNUP_URL = SERVER_URL_BASE + ":8000/api/actor/";
    public static String NEW_EVENTS_URL = SERVER_URL_BASE + ":8000/api/appointment/";
    public static String FEEDBACK_URL_START = SERVER_URL_BASE + ":8000/api/appointment/";
    public static String FEEDBACK_URL_END = "/response";

    //OAuth2
    public static String CLIENT_ID = "xzawb3Fb11Khp17HGexcHmRn8AVrnsvd9LQHxyts";
    public static String CLIENT_SECRET = "Y7spPAMwyJN6WaajI8kX8TendqUeltBTTO0mDBJ68qdMvK2VCJK5ta8ZJRoTojjyrXD58xOXyC0JPzj5eA1ef0UdXgitHdw5a0rnWGhmVENxzqsiS4RvlGw1Q3gizbuu";

    //Shared preferences
    public static final String PREFS_NAME = "LoginPrefs";
    public static final String PREFS_ACCESS_TOKEN_KEY = "access_token";
    public static final String PREFS_IP_ADDRESS_KEY = "ip_adress";
    public static final String PREFS_CLIENT_ID_KEY = "client_id";
    public static final String PREFS_CLIENT_SECRET_KEY = "client_secret";

    //Error messages
    public static final String INVALID_LOGIN_INPUT = "Either email or password was invalid.";
    public static final String AUTH_ERROR_MESSAGE = "Authentication failed. Please try again.";
    public static final String FEEDBACK_ERROR_MESSAGE = "Data transfer failed. Please try again later.";
    public static final String LOGIN_ERROR_MESSAGE = "Login failed. Please try again later.";
    public static final String SIGNUP_ERROR_MESSAGE = "Sign up failed. Please try again later.";
    public static final String FETCHING_ERROR_MESSAGE = "Fetching new events failed. Please try again later.";

    //Event answers for the server
    public static final String EVENT_ANSWER_POSITIVE = "yes";
    public static final String EVENT_ANSWER_NEGATIVE = "no";

    //Server response
    public static final int FEEDBACK_SUCCESS_CODE = 204;

    //Setter methods for testing purposes
    //Needed because of changing server config
    public static void setServerUrlBase(String serverUrlBase){
        SERVER_URL_BASE = "http://" + serverUrlBase;

        updateURLs();
    }

    private static void updateURLs() {
        TOKEN_URL = SERVER_URL_BASE+ ":8000/o/token/";
        SIGNUP_URL = SERVER_URL_BASE + ":8000/api/actor/";
        NEW_EVENTS_URL = SERVER_URL_BASE + ":8000/api/appointment/";
        FEEDBACK_URL_START = SERVER_URL_BASE + ":8000/api/appointment/";
        FEEDBACK_URL_END = "/response";
    }

    public static void setClientId(String clientId){
        CLIENT_ID = clientId;
    }

    public static void setClientSecret(String clientSecret){
        CLIENT_SECRET = clientSecret;
    }

}
