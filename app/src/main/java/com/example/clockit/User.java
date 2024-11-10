package com.example.clockit;

public class User {
    public String name;        // Full name of the user
    public String email;       // Email of the user
    public String accountType; // Account type (Admin or User)
    public String studentId;   // Student ID
    public String cardUid;     // Card UID assigned

    // Default constructor required for Firebase Realtime Database
    public User() {}

    // Constructor to initialize all fields
    public User(String name, String email, String accountType, String studentId, String cardUid) {
        this.name = name;
        this.email = email;
        this.accountType = accountType;
        this.studentId = studentId;
        this.cardUid = cardUid;
    }
}
