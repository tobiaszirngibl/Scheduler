package com.ase_1617.organizedlib.network;

/**
 * Interface to provide information on an used email and password combination to create
 * a new organized account when the signup process succeeded or failed.
 */

public interface SignupAsyncInterface {

    void onSignUpSuccess(String email, String password);

    void onSignUpError(String error);
}
