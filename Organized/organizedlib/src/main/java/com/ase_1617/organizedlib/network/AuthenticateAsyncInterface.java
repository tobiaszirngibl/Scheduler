package com.ase_1617.organizedlib.network;

/**
 * Interface to provide the response or error message when the
 * authentication process succeeded or failed.
 */

public interface AuthenticateAsyncInterface {

    void onAuthenticationSuccess(String response);

    void onAuthenticationError(String error);
}
