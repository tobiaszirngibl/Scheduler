package com.ase_1617.organized.activities;

/**
 * Created by bob on 03.01.17.
 *
 * source: http://sourcey.com/beautiful-android-login-and-signup-screens-with-material-design/
 *
 */

import android.app.ProgressDialog;
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
    public static final String PREFS_NAME = "LoginPrefs";
    private static final String INVALID_LOGIN_INPUT = "Either email or password was invalid.";

    private final String signupURL = Constants.serverUrlBase + ":8000/api/actor/";

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

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignUpError(INVALID_LOGIN_INPUT);
            return;
        }

        _signupButton.setEnabled(false);

        String name = _nameText.getText().toString().trim();
        String email = _emailText.getText().toString().trim();
        String password = _passwordText.getText().toString().trim();

        SignupAsyncTask signupAsyncTask = new SignupAsyncTask(this);
        signupAsyncTask.signupAsyncInterface = this;
        signupAsyncTask.execute(signupURL, email, password, name, this);

    }

    private void saveUserData(String name, String email, String password) {

        //TODO:Save user data after correct login

        userData = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = userData.edit();
        editor.putString("userName", name);
        editor.putString("userMail", email);
        editor.putString("userPass", password);

        // Commit the edits
        editor.commit();

        Log.v(TAG, "Login saved "+name+" --- usermail: "+email+" --- userpass: "+password);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

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

    @Override
    public void onSignUpSuccess(String name, String email, String password) {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    @Override
    public void onSignUpError(String error) {
        Toast.makeText(getBaseContext(), error, Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }
}
