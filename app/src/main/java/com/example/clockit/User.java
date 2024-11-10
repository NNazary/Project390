package com.example.clockit;

public class User {
    public String username;
    public String email;
    public String accountType;

    // Default constructor required for Firebase Realtime Database
    public User() {}

    public User(String username, String email, String accountType) {
        this.username = username;
        this.email = email;
        this.accountType = accountType;
    }
}

