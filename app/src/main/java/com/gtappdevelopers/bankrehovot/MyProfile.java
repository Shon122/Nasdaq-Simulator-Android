package com.gtappdevelopers.bankrehovot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MyProfile extends AppCompatActivity {
    public TextView balanceTextView;
    public TextView nameTextView;
    public TextView gamesPlayed;
    public TextView gamesWon;
    public TextView gamesLost;
    public TextView winratePercent;
    public Button logoutButton;
    public int mainUserIndex;
    public User currentUser;
    public FusedLocationProviderClient fusedLocationClient;
    public LocationCallback locationCallback;
    //this after location
    public ImageView imageView;
    public static final int GALLERY_REQUEST = 1;
    public static final int CAMERA_REQUEST = 2;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile);


        //find index in Mainactivity

        currentUser = MainActivity.viewingUser;
        for (int i = 0; i < MainActivity.users.size(); i++) {
            if (currentUser.username.equals(MainActivity.users.get(i).username)) {
                mainUserIndex = i;
                break;
            }
        }


        Double played = 0.0;
        Double lost = 0.0;
        Double won = 0.0;
        int winrate = 0;
        for (GameStock game : currentUser.games) {
            played++;
            if (game.win)
                won++;
            else
                lost++;
        }
        if (won > 0) {
            Double num123 = (won / played);
            num123 *= 100;
            winrate = num123.intValue();

        }
        //now views init
        balanceTextView = findViewById(R.id.balance_text);
        nameTextView = findViewById(R.id.profile_name);
        logoutButton = findViewById(R.id.button_logout1);
        gamesPlayed = findViewById(R.id.games_played);
        gamesWon = findViewById(R.id.games_won);
        gamesLost = findViewById(R.id.games_lost);
        winratePercent = findViewById(R.id.winrate_text);
        //now put values
        gamesPlayed.setText("Games Played: " + played.intValue());
        gamesWon.setText("Games Won: " + won.intValue());
        gamesLost.setText("Games Lost: " + lost.intValue());
        winratePercent.setText(winrate + "% Winrate");

        nameTextView.setText(currentUser.username);
        String dateString = currentUser.creationDate.substring(0, 10);
        dateString = dateString.replaceAll(":", "/");
        //   creationDateTextView.setText("Creation Date: " + dateString);
        balanceTextView.setText(roundToTwoDecimals(currentUser.balance) + "$");

        //get country
        if(currentUser.username.equals(MainActivity.currentUser.username)) {
            String country = getIntent().getStringExtra(LocationReceiver.COUNTRY_EXTRA);
            nameTextView.setText(currentUser.username + ", " + country);
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0
            );
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    Location location = locationResult.getLastLocation();
                    updateCountryName(location.getLatitude(), location.getLongitude());
                }
            };
            requestSingleLocationUpdate();
        }


        //only here after location

        imageView = (ImageView) findViewById(R.id.profile_image);
        if (currentUser.username.equals(MainActivity.currentUser.username)) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage();
                }
            });
        }

        // Load the saved image from firebase
        String savedImage = currentUser.savedImage;
        if (savedImage != null && !savedImage.equals("null") && savedImage.length() > 10) {
            byte[] decodedString = Base64.decode(savedImage, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Bitmap circularBitmap = getCircularBitmap(decodedBitmap);
            imageView.setBackgroundColor(Color.WHITE);
            imageView.setImageBitmap(circularBitmap);
        }

        //on click logout and check if the user is the current phone's user
        if (!currentUser.username.equals(MainActivity.currentUser.username)) {
            logoutButton.setVisibility(View.INVISIBLE);
        }
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyProfile.this, LoginActivity.class);


                startActivity(intent);
                finish();
            }
        });

    }

    // Call this method when you want to request a single location update
    public void requestSingleLocationUpdate() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(createLocationRequest(), new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null && locationResult.getLastLocation() != null) {
                        double latitude = locationResult.getLastLocation().getLatitude();
                        double longitude = locationResult.getLastLocation().getLongitude();
                        updateCountryName(latitude, longitude);
                    }
                }
            }, null);
        } else {
        }
    }

    public LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000); // Update interval in milliseconds
        locationRequest.setFastestInterval(5000); // Fastest update interval
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    public void updateCountryName(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<android.location.Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                String countryName = addresses.get(0).getCountryName();
                if (currentUser != null) {
                    nameTextView.setText(currentUser.username + ", " + countryName);
                }
            } else {
                nameTextView.setText("Loading...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void selectImage() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(
                pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                }
        );
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );

        startActivityForResult(galleryIntent, GALLERY_REQUEST);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_REQUEST);
        } else {
            Toast.makeText(MyProfile.this, "Your device doesn't have a camera!", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY_REQUEST) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Uri selectedImage = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(),
                            selectedImage
                    );

                    Bitmap circularBitmap = getCircularBitmap(bitmap);
                    imageView.setBackgroundColor(Color.WHITE);
                    imageView.setImageBitmap(circularBitmap);
                    // Save the image to SharedPreferences
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    circularBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    PreferenceManager.getDefaultSharedPreferences(this).edit().putString("saved_image", encodedImage).apply();
                    MainActivity.viewingUser.savedImage = encodedImage;
                    currentUser.savedImage = encodedImage;
                    MainActivity.users.set(mainUserIndex, currentUser);
                    MainActivity.uploadUsersToFirestore();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MyProfile.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }


        } else if (requestCode == CAMERA_REQUEST) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            Bitmap circularBitmap = getCircularBitmap(bitmap);
            imageView.setBackgroundColor(Color.WHITE);
            imageView.setImageBitmap(circularBitmap);
            // Save the image to SharedPreferences
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            circularBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("saved_image", encodedImage).apply();
            MainActivity.viewingUser.savedImage = encodedImage;
            currentUser.savedImage = encodedImage;
            MainActivity.users.set(mainUserIndex, currentUser);
            MainActivity.uploadUsersToFirestore();
        }
    }

    private Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(),
                    bitmap.getHeight(),
                    Bitmap.Config.ARGB_8888
            );
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getWidth(),
                    Bitmap.Config.ARGB_8888
            );
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
//
//    //location
//    @SuppressLint("SetTextI18n")
//    @Override
//    public void onRequestPermissionsResult(
//            int requestCode,
//            @NonNull String[] permissions,
//            @NonNull int[] grantResults
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//
//        if (requestCode == 0) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                nameTextView.setText(currentUser.username + ", Israel"); // just in case
//
//                // permission was granted, proceed with your logic
//                LocationManager locationManager =
//                        (LocationManager) getSystemService(LOCATION_SERVICE);
//                if (ActivityCompat.checkSelfPermission(
//                        this,
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                        this,
//                        Manifest.permission.ACCESS_COARSE_LOCATION
//                ) != PackageManager.PERMISSION_GRANTED) {
//
//
//                }
//                Location location =
//                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                if (location != null) {
//                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//                    try {
//                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),
//                                location.getLongitude(),
//                                1
//                        );
//                        if (addresses.size() > 0) {
//                            String country = addresses.get(0).getCountryName();
//                            nameTextView.setText(currentUser.username + ", " + country); // just in case
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            } else {
//                // permission was denied, handle accordingly
//                nameTextView.setText(currentUser.username + ", Israel"); // just in case
//            }
//        }
//    }


    public Double roundToTwoDecimals(Double value) {
        return (double) Math.round(value * 100) / 100;
    }


    @Override //when user wants to go back
    public void onBackPressed() {
        // Handle back button press event here
        goBackProfile(null);
    }

    public void goBackProfile(View view) {
        if (MainActivity.backToUsers == 0) {
            Intent intent = new Intent(this, HomePage.class);


            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, UserList.class);


            startActivity(intent);
            finish();


        }
    }
}