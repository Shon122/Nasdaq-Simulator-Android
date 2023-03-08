package com.gtappdevelopers.bankrehovot;

import java.util.ArrayList;

public class User {
    public String password;
    public Double startingBalance;
    public String username;
    public ArrayList<Trade> trades;
    public String savedImage;
    public String creationDate;
    public Double balance;
    public String status;
    public ArrayList<Game> games;

    public User(
            String password1,
            String username1,
            ArrayList<Trade> trades1,
            Double startingBalance1,
            String creationDate1,
            String savedImage1,
            String status1
    ) {
        password = password1;
        status = status1;
        username = username1;
        creationDate = creationDate1;
        savedImage = savedImage1;
        trades = trades1;
        startingBalance = startingBalance1;
        balance = startingBalance1;
        games = new ArrayList<>();
    }

    public User() {
        // Empty constructor needed for deserialization
    }

}