package com.gtappdevelopers.bankrehovot;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {
    public EditText usernameEditText;
    public EditText passwordEditText;
    boolean loginScreen;
    public TextView signupTextView;
    public TextView loginTextView;
    public Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        loginScreen = true;
        signupTextView = findViewById(R.id.signupText);
        loginTextView = findViewById(R.id.loginText);
        loginButton = findViewById(R.id.loginButton);

        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);


    }

    public void switchIntent() {
        //before switich i put all the current user stats in mainactivity vars

        for (int i = 0; i < MainActivity.users.size(); i++) {
            if (MainActivity.users.get(i).username.equals(MainActivity.username)) {
                MainActivity.currentUserIndex = i;
                MainActivity.currentUser = MainActivity.users.get(i);
            }
        }
        MainActivity.trades = MainActivity.users.get(MainActivity.currentUserIndex).trades;
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }

    public void clickContinueButton(View view) {
        if (loginScreen)
            onClickLogin(view);
        else
            onClickRegister(view);

    }

    @SuppressLint("SetTextI18n")
    public void clickedSignup(View view) {
        passwordEditText.setText("");
        passwordEditText.setHint(" Password");
        usernameEditText.setText("");
        usernameEditText.setHint(" Username");

        if (loginScreen) { //if it is showing login stuff
            loginScreen = false;
            signupTextView.setText("Already Have an account? Login Now");
            loginTextView.setText("Register");
            loginButton.setText("Create Account");
        } else //if it is showing register stuff
        {
            loginScreen = true;
            signupTextView.setText("Not yet registered? Sign Up Now");
            loginTextView.setText("Login");
            loginButton.setText("Login");

        }
    }

    public void onClickRegister(View view) {

        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        boolean check = authenticateRegisterUser(username, password);
        if (check) {
            ArrayList<Trade> emptyList = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            Date date = new Date();
            String currentDate = dateFormat.format(date);
            User newUser = new User(password, username, emptyList, 10000.00, currentDate, "null", null);
            MainActivity.users.add(newUser);
            MainActivity.uploadUsersToFirestore();
            MainActivity.password = password;
            MainActivity.username = username;
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
                    MainActivity.password = user1.password;
                    MainActivity.username = user1.username;
                    switchIntent();
                }
            }

            MainActivity.password = password;
            MainActivity.username = username;
            MainActivity.trades = emptyList;
            switchIntent();
        }


    }


    public boolean containsOnlyLettersAndNumbers(String str) {

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            if (c < 'A' || (c > 'Z' && c < 'a') || c > 'z') {
                if (c < '0' || c > '9') {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean authenticateRegisterUser(String username, String password) {

        if (username.length() < 6 || password.length() < 6 || username.length() > 10 || password.length() > 10) {
            Toast.makeText(
                    LoginActivity.this,
                    "Username and password should be between 6-10 letters",
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }
        if (!containsOnlyLettersAndNumbers(username) || !containsOnlyLettersAndNumbers(password)) {
            Toast.makeText(
                    LoginActivity.this,
                    "Username and password can only contain letters and numbers",
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }


        //TODO: Add code to authenticate the user with the variable users from mainactivity
        for (User user1 : MainActivity.users) {
            if (user1.username.equals(username)) {
                Toast.makeText(LoginActivity.this, "User already exists", Toast.LENGTH_SHORT)
                        .show();
                return false;
            }

        }

        return true;
    }

    public boolean authenticateLoginUser(String username, String password) {

        if (username.length() < 6 || password.length() < 6 || username.length() > 10 || password.length() > 10) {
            Toast.makeText(LoginActivity.this, "Username and password should be between 6-10 letters", Toast.LENGTH_SHORT).show();
            return false;
        }


        //TODO: Add code to authenticate the user with the variable users from mainactivity
        for (User user1 : MainActivity.users) {
            if (user1.password.equals(password) && user1.username.equals(username)) {
                Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT)
                        .show();
                return true;
            }
            if ((!user1.password.equals(password)) && user1.username.equals(username)) {
                Toast.makeText(LoginActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        Toast.makeText(LoginActivity.this, "User doesnt exist, please register", Toast.LENGTH_SHORT)
                .show();
        return false;
    }
}