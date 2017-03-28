package com.ase_1617.organizedlib.network;

/**
 * Interface to provide information on account creation process data.
 */

public interface SignupAsyncInterface {

    void onSignUpSuccess(String email, String password);

    void onSignUpError(String error);
}
