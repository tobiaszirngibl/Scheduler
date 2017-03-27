package com.ase_1617.organizedlib.utility;

/**
 * Created by bob on 25.03.17.
 */

public interface Constants {

    String SERVER_URL_BASE = "http://192.168.178.20";
    String TOKEN_URL = SERVER_URL_BASE+ ":8000/o/token/";
    String SIGNUP_URL = SERVER_URL_BASE + ":8000/api/actor/";


    String CLIENT_ID= "tfbVGsAUgvsrTBIFyZe7RBrcImX2Cazywt3rVR3x";
    String CLIENT_SECRET = "2uFuKJjALs166co29sBzGRvUtXv2sCazjhp1ZhqtOXIWgeOafryp6Ysu51M7Vri1m42HvCfwYB5rNHPZWnu1fBwlptjRJqwVqBflzf8oJLJEdTCeStPdbDtRs8zrzxSm";

    String PREFS_NAME = "LoginPrefs";

    String INVALID_LOGIN_INPUT = "Either email or password was invalid.";


}
