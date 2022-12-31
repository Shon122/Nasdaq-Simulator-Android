package com.gtappdevelopers.bankrehovot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

public class RegisterActivity extends AppCompatActivity {

    private static final int MIN_USERNAME_LENGTH = 6;
    private static final int MIN_PASSWORD_LENGTH = 6;

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mRegisterButton;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registeractivity);

        mUsernameEditText = findViewById(R.id.edit_text_username);
        mPasswordEditText = findViewById(R.id.edit_text_password);
        mRegisterButton = findViewById(R.id.button_register);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsernameEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Username and password cannot be empty",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (username.length() < MIN_USERNAME_LENGTH) {
                    Toast.makeText(RegisterActivity.this, "Username must be at least 6 characters long",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < MIN_PASSWORD_LENGTH) {
                    Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters long",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString("username", username);
                editor.putString("password", password);
                editor.apply();

                Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}