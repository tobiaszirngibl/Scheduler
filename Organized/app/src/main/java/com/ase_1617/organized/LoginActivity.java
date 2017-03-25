package com.ase_1617.organized;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import com.ase_1617.organized.activities.EventFeedActivity;
import com.ase_1617.organized.activities.EventFeedActivity;
import com.ase_1617.organized.activities.SignupActivity;
import com.ase_1617.organizedlib.network.AuthenticateAsyncInterface;
import com.ase_1617.organizedlib.network.AuthenticateAsyncTask;
import com.ase_1617.organizedlib.scribejava.OrganizedOAuth20Api;
import com.ase_1617.organizedlib.utility.Constants;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * The login activity of the app.
 * The user can enter an email and a password and log in which starts the validation process.
 * If the entered data is valid the user is redirected to the EventFeedActivity.
 * Otherwise an error alert is shown.
 * The user can click a hint which redirects to the Signup acitivity.
 *
 * source: http://sourcey.com/beautiful-android-login-and-signup-screens-with-material-design/
 */

public class LoginActivity extends AppCompatActivity implements AuthenticateAsyncInterface{
    private static final String TAG = "LoginActivity";
    public static final String PREFS_NAME = "LoginPrefs";
    private static final int REQUEST_SIGNUP = 0;

    private final String tokenURL = Constants.serverUrlBase + ":8000/o/token/";
    private final String clientId = "tfbVGsAUgvsrTBIFyZe7RBrcImX2Cazywt3rVR3x";
    private final String clientSecret = "2uFuKJjALs166co29sBzGRvUtXv2sCazjhp1ZhqtOXIWgeOafryp6Ysu51M7Vri1m42HvCfwYB5rNHPZWnu1fBwlptjRJqwVqBflzf8oJLJEdTCeStPdbDtRs8zrzxSm";
    final String secretState = "security_token" + new Random().nextInt(999_999);
    private static final String PROTECTED_RESOURCE_URL = Constants.serverUrlBase + ":8000/api/appointment/";

    private SharedPreferences userData;

    private EditText _emailText;
    private EditText _passwordText;
    private Button _loginButton;
    private TextView _signupLink;

    private String userName;
    private String userMail;
    private String userPass;

    private boolean authenticationError = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _signupLink = (TextView) findViewById(R.id.link_signup);

        checkLoginStatus();

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    private void checkLoginStatus() {
        userData = getSharedPreferences(PREFS_NAME, 0);
        userName = userData.getString("userName", "Default");
        userMail = userData.getString("userMail", "Default");
        userPass = userData.getString("userPass", "Default");

        if(userMail != "Default" && userPass != "Default" && validate(userMail, userPass)){
            Log.v(TAG, "Automatic login restored usermail: "+userMail+" --- userpass: "+userPass);
            showLoginInfo();
        }
    }

    private void showLoginInfo() {
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Automatic log in as " + userMail);
        progressDialog.setCancelable(false);
        progressDialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if(progressDialog != null && progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }

                        loginToApp();

                    }
                }, 2000);
    }

    public void login() {
        Log.d(TAG, "Login");

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (!validate(email, password)) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        authenticateUser(email, password);

    }

    private void authenticateUser(String email, String password) {

        //TODO:Authenticate user per server request

        userMail = userData.getString("userMail", "Default");
        userPass = userData.getString("userPass", "Default");

        Log.v(TAG, "alt: "+userMail+userPass);
        //Log.v(TAG, "neu: "+email+password);
        /*
        if(email.equals(userMail) && password.equals(userPass)){
            authenticationError = false;
        }*/


        AuthenticateAsyncTask authenticateAsyncTask = new AuthenticateAsyncTask(this);
        authenticateAsyncTask.authenticateAsyncInterface = this;
        authenticateAsyncTask.execute(tokenURL, email, password, clientId, clientSecret, secretState, PROTECTED_RESOURCE_URL);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
                onLoginSuccess();

            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);

        authenticationError = true;

        //finish();

        loginToApp();
    }

    private void loginToApp() {
        Intent loginIntent = new Intent(this, EventFeedActivity.class);
        startActivity(loginIntent);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate(String email, String password) {
        boolean valid = true;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    @Override
    public void onAuthenticationSuccess(String response) {
        Log.v(TAG, "Response: "+response);

        SharedPreferences.Editor editor = userData.edit();
        editor.putString("accessToken", response);
        editor.commit();

        onLoginSuccess();
    }

    @Override
    public void onAuthenticationError(String error) {
        Log.v(TAG, "error: "+error);
        onLoginFailed();
    }
}
