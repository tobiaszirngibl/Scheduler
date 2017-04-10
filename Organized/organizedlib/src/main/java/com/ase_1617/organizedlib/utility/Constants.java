package com.ase_1617.organizedlib.utility;

/**
 * Created by bob on 25.03.17.
 */

public interface Constants {

    //Network
    String SERVER_URL_BASE = "http://192.168.178.20";
    String TOKEN_URL = SERVER_URL_BASE+ ":8000/o/token/";
    String SIGNUP_URL = SERVER_URL_BASE + ":8000/api/actor/";
    String NEW_EVENTS_URL = SERVER_URL_BASE + ":8000/api/appointment/";
    String FEEDBACK_URL_START = SERVER_URL_BASE + ":8000/api/appointment/";
    String FEEDBACK_URL_END = "/response";

    //OAuth2
    String CLIENT_ID= "xzawb3Fb11Khp17HGexcHmRn8AVrnsvd9LQHxyts";
    String CLIENT_SECRET = "Y7spPAMwyJN6WaajI8kX8TendqUeltBTTO0mDBJ68qdMvK2VCJK5ta8ZJRoTojjyrXD58xOXyC0JPzj5eA1ef0UdXgitHdw5a0rnWGhmVENxzqsiS4RvlGw1Q3gizbuu";

    //Shared preferences
    String PREFS_NAME = "LoginPrefs";
    String PREFS_ACCESS_TOKEN_KEY = "access_token";

    //Error messages
    String INVALID_LOGIN_INPUT = "Either email or password was invalid.";
    String AUTH_ERROR_MESSAGE = "Authentication failed. Please try again.";
    String FEEDBACK_ERROR_MESSAGE = "Data transfer failed. Please try again later.";
    String LOGIN_ERROR_MESSAGE = "Login failed. Please try again later.";
    String SIGNUP_ERROR_MESSAGE = "Sign up failed. Please try again later.";

    //
    String EVENT_ANSWER_POSITIVE = "yes";
    String EVENT_ANSWER_NEGATIVE = "no";

    //Server response
    int FEEDBACK_SUCCESS_CODE = 204;
}
