package com.ase_1617.organizedlib.network;

/**
 * Interface to provide information on an email and password combination used to create
 * a new organized account or the error message when the signup process succeeded or failed.
 */

public interface SignupAsyncInterface {

    void onSignUpSuccess(String email, String password);

    void onSignUpError(String error);
}
