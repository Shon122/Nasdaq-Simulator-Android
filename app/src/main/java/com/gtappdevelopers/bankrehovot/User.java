package com.gtappdevelopers.bankrehovot;

import java.util.ArrayList;

public class User {
    public String password;
    public String username;
    public ArrayList<Trade> trades;
    public Double balance;

    public User(String password1, String username1, ArrayList<Trade> trades1,Double balance1) {
        password = password1;
        username = username1;
        trades = trades1;
        balance=balance1;
    }


}
