package com.example.clockit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            SharedPreferences prefs = getSharedPreferences("ClockItPrefs", MODE_PRIVATE);
            String accountType = prefs.getString("accountType", null);

            if (accountType != null) {
                startMainActivity(accountType);
            } else {
                fetchAccountType(currentUser.getUid());
            }
        } else {
            startMainActivity(null);
        }
    }

    private void fetchAccountType(String userId) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String accountType = dataSnapshot.child("accountType").getValue(String.class);

                    SharedPreferences.Editor editor = getSharedPreferences("ClockItPrefs", MODE_PRIVATE).edit();
                    editor.putString("accountType", accountType);
                    editor.apply();

                    startMainActivity(accountType);
                } else {
                    // Account type not found, indicating possible account deletion
                    clearAccountType();
                    startMainActivity(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                startMainActivity(null);
            }
        });
    }

    private void startMainActivity(String accountType) {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.putExtra("accountType", accountType);
        startActivity(intent);
        finish();
    }

    private void clearAccountType() {
        SharedPreferences.Editor editor = getSharedPreferences("ClockItPrefs", MODE_PRIVATE).edit();
        editor.remove("accountType");
        editor.apply();
    }
}
