package com.example.utar.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends Activity {

    private static final int REQUEST_ENABLE_BT = 3;

    DatabaseAdapter loginDatabaseAdapter;

    EditText usernameInput;
    EditText passwordInput;

    String username = "";
    String password = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // create a instance of SQLite Database
        loginDatabaseAdapter = new DatabaseAdapter(this);
        loginDatabaseAdapter = loginDatabaseAdapter.open();

        loginDatabaseAdapter.insertEntry("keiyeng", "ky123");
        loginDatabaseAdapter.insertEntry("karkee", "kk123");
        loginDatabaseAdapter.insertEntry("yunni", "yn123");

        usernameInput = (EditText) findViewById(R.id.usernameInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);

        Button loginBtn = (Button) findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                login();

            }
        });
    }

    public void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        } else
            onLoginSuccess();
    }

    public boolean validate() {
        boolean valid = false;

        username = usernameInput.getText().toString();
        password = passwordInput.getText().toString();

        // fetch the Password form database for respective user name
        String storedPassword = loginDatabaseAdapter.getSinlgeEntry(username);

        if (username.equals("")) {
            Toast.makeText(getApplicationContext(), "Username is empty", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (password.equals("")) {
            Toast.makeText(getApplicationContext(), "Password is empty", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (password.equals(storedPassword)) {
           return true;
        } else Toast.makeText(getApplicationContext(), "Wrong username or password", Toast.LENGTH_SHORT).show();

        return valid;
    }


    public void onLoginSuccess() {

        Toast.makeText(getBaseContext(), "Login success", Toast.LENGTH_LONG).show();

        // Switching to Main Activity
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);

        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close The Database
        loginDatabaseAdapter.close();
    }
}
