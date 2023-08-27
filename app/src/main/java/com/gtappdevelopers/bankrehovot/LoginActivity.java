package com.gtappdevelopers.bankrehovot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {
    public EditText usernameEditText;
    public EditText passwordEditText;
    boolean loginScreen;
    public TextView signupTextView;
    public TextView loginTextView;
    public Button loginButton;

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 3;
    public static final int GALLERY_PERMISSION_REQUEST_CODE = 4;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;

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

//        // Request camera permission
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.CAMERA},
//                    REQUEST_CAMERA_PERMISSION);
//        }
//
//
//        // Request location permission
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        REQUEST_LOCATION_PERMISSION);
//            }
//        }

        if (MainActivity.acceptedPerms == false) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_REQUEST_CODE);
                }
            }
        }
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
        finish();
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
            User newUser = new User(password, username, emptyList, 10000.00, currentDate, null, null, new ArrayList<GameStock>());
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

    @Override //when user wants to go back
    public void onBackPressed() {
        // Handle back button press event here

    }


    @Override
    protected void onResume() {
        super.onResume();

        // Check if location permissions are granted
        if (hasLocationPermission()) {
            // Location permissions are already granted, you can proceed with your location-related code here.
            showToast("Location and Camera permissions granted!");
        } else if (shouldShowPermissionRationale()) {
            // User has denied permissions before but not selected "Don't ask again."
            showToast("Location and Camera permissions are required for this app to work properly.");
            requestLocationPermission();
        } else {
            // User has denied permissions and selected "Don't ask again."
            showToast("Location and Camera permissions are required for this app to work properly. Please enable them in the app settings.");
            openAppSettings();
        }
    }

    // Check if location permissions are granted
    private boolean hasLocationPermission() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    // Request location permissions
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // User has denied permissions before, show an explanation
            showToast("Location and Camera permissions are required for this app to work properly.");
        }

        // Request the permissions
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }

    // Handle permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permissions granted
                showToast("Location and Camera permissions granted!");
            } else if (!shouldShowPermissionRationale()) {
                // User has denied permissions and selected "Don't ask again."
                showToast("Location and Camera permissions are required for this app to work properly. Please enable them in the app settings.");
                openAppSettings();
            } else {
                // Location permissions denied, ask again
                showToast("Location and Camera permissions are required for this app to work properly.");
                requestLocationPermission();
            }
        } else if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted
                showToast("Camera permissions granted!");
            } else if (!shouldShowPermissionRationale()) {
                // User has denied camera permissions and selected "Don't ask again."
                showToast("Camera permissions are required for this app to work properly. Please enable them in the app settings.");
                openAppSettings();
            } else {
                // Camera permissions denied, ask again
                showToast("Camera permissions are required for this app to work properly.");
                requestCameraPermission();
            }
        } else if (requestCode == GALLERY_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Gallery permission granted
                showToast("Gallery permissions granted!");
            } else if (!shouldShowPermissionRationale()) {
                // User has denied gallery permissions and selected "Don't ask again."
                showToast("Gallery permissions are required for this app to work properly. Please enable them in the app settings.");
                openAppSettings();
            } else {
                // Gallery permissions denied, ask again
                showToast("Gallery permissions are required for this app to work properly.");
                requestGalleryPermission();
            }
        }

    }

    // Request camera permissions
    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            // User has denied camera permissions before, show an explanation
            showToast("Camera permissions are required for this app to work properly.");
        }

        // Request the permissions
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    // Request gallery permissions
    private void requestGalleryPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // User has denied gallery permissions before, show an explanation
            showToast("Gallery permissions are required for this app to work properly.");
        }

        // Request the permissions
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_REQUEST_CODE);
    }

    // Check if the user has denied permissions but not selected "Don't ask again"
    private boolean shouldShowPermissionRationale() {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) &&
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) &&
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    // Open the app settings
    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}