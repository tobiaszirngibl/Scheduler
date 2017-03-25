package com.ase_1617.organizedlib.network;

/**
 * Interface to provide information on fetched event data
 * when new information is fetched
 */

public interface AuthenticateAsyncInterface {

    void onAuthenticationSuccess(String response);

    void onAuthenticationError(String error);
}
