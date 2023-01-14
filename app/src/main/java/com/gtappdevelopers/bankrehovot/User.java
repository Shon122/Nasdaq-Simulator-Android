package com.gtappdevelopers.bankrehovot;

import java.util.ArrayList;

public class User {
    public String password;
    public Double startingBalance;
    public String username;
    public ArrayList<Trade> trades;
    public Double balance;

    public User(String password1, String username1, ArrayList<Trade> trades1,Double startingBalance1) {
        password = password1;
        username = username1;
        trades = trades1;
        startingBalance=startingBalance1;
        balance=startingBalance1;
    }
    public User() {
        // Empty constructor needed for deserialization
    }

}
