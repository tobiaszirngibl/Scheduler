package com.ase_1617.organized.activities;

/**
 * Created by bob on 03.01.17.
 *
 * The sign up activity of the app.
 * The user can enter a name, email and password to create a new organized account.
 * The entered data is tested and valid data is sent to the server.
 *
 * source: http://sourcey.com/beautiful-android-login-and-signup-screens-with-material-design/
 *
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ase_1617.organized.R;
import com.ase_1617.organizedlib.network.SignupAsyncInterface;
import com.ase_1617.organizedlib.network.SignupAsyncTask;
import com.ase_1617.organizedlib.utility.Constants;


public class SignupActivity extends AppCompatActivity implements SignupAsyncInterface{
    private static final String TAG = "SignupActivity";

    private SharedPreferences userData;

    private EditText _nameText;
    private EditText _emailText;
    private EditText _passwordText;
    private Button _signupButton;
    private TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        _nameText = (EditText) findViewById(R.id.input_name);
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _signupButton = (Button) findViewById(R.id.btn_signup);
        _loginLink = (TextView) findViewById(R.id.link_login);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    /**
     * Check whether the entered user data is valid and start a new
     * signupasynctask to send the user data to the server that creates a new
     * organized account.
     */
    public void signup() {
        //Fetch the entered user data
        String name = _nameText.getText().toString().trim();
        String email = _emailText.getText().toString().trim();
        String password = _passwordText.getText().toString().trim();

        //Check whether the user data is valid
        //Show an error alert and cancel the signup process if the data is not valid
        if (!validate(name, email, password)) {
            onSignUpError(Constants.INVALID_LOGIN_INPUT);
            return;
        }

        //Start a new signupasynctask to send the valid data to the server and create a new account
        SignupAsyncTask signupAsyncTask = new SignupAsyncTask(this);
        signupAsyncTask.signupAsyncInterface = this;
        signupAsyncTask.execute(Constants.SIGNUP_URL, email, password, name, this);

    }

    private void saveUserData(String name, String email, String password) {

        //TODO:Save user data after correct login

        userData = getSharedPreferences(Constants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = userData.edit();
        editor.putString("userName", name);
        editor.putString("userMail", email);
        editor.putString("userPass", password);

        // Commit the edits
        editor.commit();

        Log.v(TAG, "Login saved "+name+" --- usermail: "+email+" --- userpass: "+password);
    }

    /**
     * Check whether the user data is valid and return the according boolean value
     * @param name The new user name
     * @param email The new user email
     * @param password The new user password
     * @return
     */
    public boolean validate(String name, String email, String password) {
        boolean valid = true;

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

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

    /**
     * If the account is created successfully
     * save the user data locally and
     * finish the activity with result = OK.
     */
    @Override
    public void onSignUpSuccess(String email, String password) {
        userData = getSharedPreferences(Constants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = userData.edit();
        editor.putString("userMail", email);
        editor.putString("userPass", password);

        editor.commit();

        setResult(RESULT_OK, null);
        finish();
    }

    /**
     * If the account creation failed
     * show an error toast.
     * @param error
     */
    @Override
    public void onSignUpError(String error) {
        Toast.makeText(getBaseContext(), error, Toast.LENGTH_LONG).show();
    }
}
