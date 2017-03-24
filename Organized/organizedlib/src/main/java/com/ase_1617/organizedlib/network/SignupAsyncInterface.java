package com.ase_1617.organizedlib.network;

/**
 * Interface to provide information on fetched event data
 * when new information is fetched
 */

public interface SignupAsyncInterface {

    void onSignUpSuccess(String name, String email, String password);

    void onSignUpError(String error);
}
