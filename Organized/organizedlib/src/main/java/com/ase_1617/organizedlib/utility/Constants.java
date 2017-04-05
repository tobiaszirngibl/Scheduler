package com.ase_1617.organizedlib.utility;

/**
 * Created by bob on 25.03.17.
 */

public interface Constants {

    String SERVER_URL_BASE = "http://192.168.2.102";
    String TOKEN_URL = SERVER_URL_BASE+ ":8000/o/token/";
    String SIGNUP_URL = SERVER_URL_BASE + ":8000/api/actor/";
    String NEW_EVENTS_URL = SERVER_URL_BASE + ":8000/api/appointment/";
    String FEEDBACK_URL_START = SERVER_URL_BASE + ":8000/api/appointment/";
    String FEEDBACK_URL_END = "/response";


    String CLIENT_ID= "M7kgIr6rqeVeY2Q2vCrlKhQYKT9LDVYH2ycEbAbS";
    String CLIENT_SECRET = "GB3oikXcvmQJ9UK2cD5eHpS1H57lN6av6DiGpgg5cbTMXUL7b2lC5XATOlFCdVmzGT4rTa8PHIG9DG9OTzzQxCq9ZTFR1bn9sArMJtS1x0jwcXxL8IDOLVIdR8OB5Lln";

    String PREFS_NAME = "LoginPrefs";
    String PREFS_ACCESS_TOKEN_KEY = "access_token";

    String INVALID_LOGIN_INPUT = "Either email or password was invalid.";
    String AUTH_ERROR_MESSAGE = "Authentication failed. Please try again.";

    String EVENT_ANSWER_POSITIVE = "yes";
    String EVENT_ANSWER_NEGATIVE = "no";

}
