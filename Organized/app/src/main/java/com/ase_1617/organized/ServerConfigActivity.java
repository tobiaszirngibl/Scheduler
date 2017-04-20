package com.ase_1617.organized;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ase_1617.organizedlib.utility.Constants;

/**
 * Class to enter the ip address, oauth client id and secret
 * and to therefore configure the server data accordingly.
 * Can be deleted once the server data isnt changing anymore.
 * For testing purposes.
 */

public class ServerConfigActivity extends AppCompatActivity{

    private SharedPreferences userData;

    private EditText _ipText;
    private EditText _clientIdText;
    private EditText _clientSecretText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Button _configButton;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_config);

        _ipText = (EditText) findViewById(R.id.input_ip);
        _clientIdText = (EditText) findViewById(R.id.input_client_id);
        _clientSecretText = (EditText) findViewById(R.id.input_client_secret);
        _configButton = (Button) findViewById(R.id.btn_config);

        checkConfigStatus();

        _configButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setConfigData();
            }
        });

    }

    /**
     * Load the shared prefs config data and fill the according text fields.
     */
    private void checkConfigStatus() {
        userData = getSharedPreferences(Constants.PREFS_NAME, 0);
        String ipAddress = userData.getString(Constants.PREFS_IP_ADDRESS_KEY, "");
        String clientId = userData.getString(Constants.PREFS_CLIENT_ID_KEY, "");
        String clientSecret = userData.getString(Constants.PREFS_CLIENT_SECRET_KEY, "");

        _ipText.setText(ipAddress);
        _clientIdText.setText(clientId);
        _clientSecretText.setText(clientSecret);
    }

    /**
     * Save the server config data in the constants class and in the shared prefs.
     */
    public void setConfigData() {
        String ip = _ipText.getText().toString();
        String id = _clientIdText.getText().toString();
        String secret = _clientSecretText.getText().toString();

        userData = getSharedPreferences(Constants.PREFS_NAME, 0);

        SharedPreferences.Editor editor = userData.edit();
        editor.putString(Constants.PREFS_IP_ADDRESS_KEY, ip);
        editor.putString(Constants.PREFS_CLIENT_ID_KEY, id);
        editor.putString(Constants.PREFS_CLIENT_SECRET_KEY, secret);
        editor.apply();

        Constants.setServerUrlBase(ip);
        Constants.setClientId(id);
        Constants.setClientSecret(secret);

        startApp();
    }

    @Override
    public void onBackPressed() {
        // disable going back
        moveTaskToBack(true);
    }

    /**
     * Start the login activity via intent.
     */
    private void startApp() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

}
