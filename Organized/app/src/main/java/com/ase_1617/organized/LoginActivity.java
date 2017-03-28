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

import com.ase_1617.organized.activities.EventFeedActivity;
import com.ase_1617.organized.activities.SignupActivity;
import com.ase_1617.organizedlib.network.AuthenticateAsyncInterface;
import com.ase_1617.organizedlib.network.AuthenticateAsyncTask;
import com.ase_1617.organizedlib.utility.Constants;

/**
 * The login activity of the app.
 * The user can enter an email and a password and log in which starts the validation process.
 * The user data is sent to and verified by the oauth server.
 * If the entered data is valid the server sends an access token
 * which allows the app to request protected data from the server.
 * The user is redirected to the EventFeedActivity.
 * Otherwise an error alert is shown.
 * The user can click a hint which redirects to the Signup acitivity.
 *
 * source: http://sourcey.com/beautiful-android-login-and-signup-screens-with-material-design/
 */

public class LoginActivity extends AppCompatActivity implements AuthenticateAsyncInterface{
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    private SharedPreferences userData;

    private EditText _emailText;
    private EditText _passwordText;
    private Button _loginButton;
    private TextView _signupLink;

    private String userName;
    private String userMail;
    private String userPass;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _signupLink = (TextView) findViewById(R.id.link_signup);

        //TODO:Auomatischer Login einbinden oder löschen
        //checkLoginStatus();

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

    //TODO:Auomatischer Login einbinden oder löschen
    private void checkLoginStatus() {
        userData = getSharedPreferences(Constants.PREFS_NAME, 0);
        userName = userData.getString("userName", "Default");
        userMail = userData.getString("userMail", "Default");
        userPass = userData.getString("userPass", "Default");

        if(userMail != "Default" && userPass != "Default" && validate(userMail, userPass)){
            Log.v(TAG, "Automatic login restored usermail: "+userMail+" --- userpass: "+userPass);
            showLoginInfo();
        }
    }

    //TODO:Auomatischer Login einbinden oder löschen
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

    /**
     * Validate the user data and cancel the login if validation failed
     * or authenticate the user if data was valid.
     */
    public void login() {
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (!validate(email, password)) {
            onLoginFailed();
            return;
        }

        authenticateUser(email, password);
    }

    /**
     * Start a new authenticateasynctask to authenticate the user and
     * grant access to protected server data.
     * @param email User login email
     * @param password User login password
     */
    private void authenticateUser(String email, String password) {

        //TODO:Auomatischer Login einbinden oder löschen
        //userMail = userData.getString("userMail", "Default");
        //userPass = userData.getString("userPass", "Default");

        AuthenticateAsyncTask authenticateAsyncTask = new AuthenticateAsyncTask(this);
        authenticateAsyncTask.authenticateAsyncInterface = this;
        authenticateAsyncTask.execute(Constants.TOKEN_URL, email, password, Constants.CLIENT_ID, Constants.CLIENT_SECRET);
    }

    /**
     * After successfully creating a new organized account
     * Authenticate the new created account and login the user.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                userData = getSharedPreferences(Constants.PREFS_NAME, 0);
                userMail = userData.getString("userMail", "Default");
                userPass = userData.getString("userPass", "Default");

                authenticateUser(userMail, userPass);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        this.finish();

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
        userData = getSharedPreferences(Constants.PREFS_NAME, 0);

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
