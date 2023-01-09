package com.gtappdevelopers.bankrehovot;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.checkerframework.checker.units.qual.A;

import java.text.ParseException;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        TextView textView;
        MainActivity.stockModels = null;

        usernameEditText = (EditText) findViewById(R.id.username_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        loginButton = (Button) findViewById(R.id.login_button);
        registerButton = (Button) findViewById(R.id.register_button);

    }

    public void switchIntent() {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }

    public void onClickRegister(View view) {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        boolean check = authenticateRegisterUser(username, password);
        if (check) {
            ArrayList<Trade> emptyList = new ArrayList<>();
            User newUser = new User(password, username, emptyList, 10000.00);
            MainActivity.users.add(newUser);
            try {
                InfoAll info1 = new InfoAll(this);
                info1.users = MainActivity.users;
                info1.uploadUsersFirebase();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            MainActivity.password = password;
            MainActivity.username = password;
            MainActivity.trades = emptyList;
            switchIntent();
        }
    }


    public void onClickLogin(View view) {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        boolean check = authenticateLoginUser(username, password);
        if (check) {
            ArrayList<Trade> emptyList = new ArrayList<>();
            for (User user1 : MainActivity.users) {
                if (user1.username.equals(username)) {
                    MainActivity.trades = user1.trades;

                }
            }

            MainActivity.password = password;
            MainActivity.username = password;
            MainActivity.trades = emptyList;
            switchIntent();
        }
    }


    public boolean authenticateRegisterUser(String username, String password) {

        if (username.length() < 6 || password.length() < 6) {
            Toast.makeText(this, "Username and password should be at least 6 letters", Toast.LENGTH_SHORT).show();
            return false;
        }


        //TODO: Add code to authenticate the user with the variable users from mainactivity
        for (User user1 : MainActivity.users) {
            if (user1.username.equals(username)) {
                Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
                return false;
            }

        }

        return true;
    }

    public boolean authenticateLoginUser(String username, String password) {

        if (username.length() < 6 || password.length() < 6) {
            Toast.makeText(this, "Username and password should be at least 6 letters", Toast.LENGTH_SHORT).show();
            return false;
        }


        //TODO: Add code to authenticate the user with the variable users from mainactivity
        for (User user1 : MainActivity.users) {
            if (user1.password.equals(password) && user1.username.equals(username)) {
                Toast.makeText(this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                return true;
            }
            if ((!user1.password.equals(password)) && user1.username.equals(username)) {
                Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        Toast.makeText(this, "User doesnt exist, please register", Toast.LENGTH_SHORT).show();
        return false;
    }
}