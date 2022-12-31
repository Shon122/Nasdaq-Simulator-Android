package com.gtappdevelopers.bankrehovot;

public class User {
    public String password;
    public String username;
    public Trade[] trades;
    public Double balance;

    public User(String password1, String username1, Trade[] trades1,Double balance1) {
        password = password1;
        username = username1;
        trades = trades1;
        balance=balance1;
    }


}
