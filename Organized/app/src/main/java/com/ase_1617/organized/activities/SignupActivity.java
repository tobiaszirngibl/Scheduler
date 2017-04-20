package com.ase_1617.organized.activities;

/**
 * The sign up activity of the app.
 * The user can enter a name, email and password to create a new organized account.
 * The entered data is tested and valid data is sent to the server.
 * The user can click a hint which redirects to the Signup acitivity.
 *
 * source: http://sourcey.com/beautiful-android-login-and-signup-screens-with-material-design/
 *
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    private SharedPreferences userData;

    private EditText _nameText;
    private EditText _emailText;
    private EditText _passwordText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Button _signupButton;
        TextView _loginLink;

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
                //Finish the signup screen and return to the Login activity
                finish();
            }
        });
    }

    /**
     * Check whether the entered user data is valid and start a new
     * signup asynctask to send the user data to the server that creates a new
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

    /**
     * Check whether the user data is valid and return the according boolean value
     * @param name The new user name
     * @param email The new user email
     * @param password The new user password
     * @return Boolean value showing whether the data is valid user data or not
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

        editor.apply();

        setResult(RESULT_OK, null);
        finish();
    }

    /**
     * If the account creation failed
     * show an error toast.
     * @param error The error message
     */
    @Override
    public void onSignUpError(String error) {
        Toast.makeText(getBaseContext(), Constants.SIGNUP_ERROR_MESSAGE, Toast.LENGTH_LONG).show();
    }
}
