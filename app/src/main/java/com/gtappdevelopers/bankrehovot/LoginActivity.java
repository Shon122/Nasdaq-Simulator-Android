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
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        TextView textView;


        usernameEditText = (EditText) findViewById(R.id.username_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);
      


    }

    public void switchIntent() {
        //before switich i put all the current user stats in mainactivity vars

        for (int i = 0; i < MainActivity.users.size(); i++) {
            if (MainActivity.users.get(i).username.equals(MainActivity.username))
                MainActivity.currentUserIndex = i;
        }
        MainActivity.trades = MainActivity.users.get(MainActivity.currentUserIndex).trades;
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
            MainActivity.uploadUsersToFirestore(MainActivity.users);

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


    public boolean authenticateRegisterUser(String username, String password) {

        if (username.length() < 6 || password.length() < 6) {
            Toast.makeText(LoginActivity.this, "Username and password should be at least 6 letters", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!username.matches("^[a-zA-Z0-9]*$") ||!password.matches("^[a-zA-Z0-9]*$") ) {
            Toast.makeText(LoginActivity.this, "Username and password can only contain letters and numbers", Toast.LENGTH_SHORT).show();
            return false;
        }


        //TODO: Add code to authenticate the user with the variable users from mainactivity
        for (User user1 : MainActivity.users) {
            if (user1.username.equals(username)) {
                Toast.makeText(LoginActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
                return false;
            }

        }

        return true;
    }

    public boolean authenticateLoginUser(String username, String password) {

        if (username.length() < 6 || password.length() < 6) {
            Toast.makeText(LoginActivity.this, "Username and password should be at least 6 letters", Toast.LENGTH_SHORT).show();
            return false;
        }


        //TODO: Add code to authenticate the user with the variable users from mainactivity
        for (User user1 : MainActivity.users) {
            if (user1.password.equals(password) && user1.username.equals(username)) {
                Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                return true;
            }
            if ((!user1.password.equals(password)) && user1.username.equals(username)) {
                Toast.makeText(LoginActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        Toast.makeText(LoginActivity.this, "User doesnt exist, please register", Toast.LENGTH_SHORT).show();
        return false;
    }
}