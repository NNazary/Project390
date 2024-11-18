package com.example.clockit;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddClassActivity extends AppCompatActivity {
    private EditText etClassName, etClassCode, etClassDescription, etTeacherName, etClassStartTime, etClassEndTime, etClassRoom;
    private Button btnSubmitClass;
    private DatabaseReference classDatabase;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        // Initialize Firebase authentication and database reference
        firebaseAuth = FirebaseAuth.getInstance();
        classDatabase = FirebaseDatabase.getInstance().getReference("classes");

        // Initialize UI elements
        etClassName = findViewById(R.id.etClassName);
        etClassCode = findViewById(R.id.etClassCode);
        etClassDescription = findViewById(R.id.etClassDescription);
        etTeacherName = findViewById(R.id.etTeacherName);
        etClassStartTime = findViewById(R.id.etClassStartTime);
        etClassEndTime = findViewById(R.id.etClassEndTime);
        etClassRoom = findViewById(R.id.etClassRoom);
        btnSubmitClass = findViewById(R.id.btnSubmitClass);

        // Set the button listener
        btnSubmitClass.setOnClickListener(this::addClassToFirebase);
    }

    private void addClassToFirebase(View view) {
        // Get input values
        String className = etClassName.getText().toString().trim();
        String classCode = etClassCode.getText().toString().trim();
        String classDescription = etClassDescription.getText().toString().trim();
        String teacherName = etTeacherName.getText().toString().trim();
        String classStartTime = etClassStartTime.getText().toString().trim();
        String classEndTime = etClassEndTime.getText().toString().trim();
        String classRoom = etClassRoom.getText().toString().trim();
        String adminId = firebaseAuth.getCurrentUser().getUid(); // Get current admin's ID

        // Validate input
        if (TextUtils.isEmpty(className) || TextUtils.isEmpty(classCode) ||
                TextUtils.isEmpty(classDescription) || TextUtils.isEmpty(teacherName) ||
                TextUtils.isEmpty(classStartTime) || TextUtils.isEmpty(classEndTime) || TextUtils.isEmpty(classRoom)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique class ID
        String classId = classDatabase.push().getKey();

        // Create a new ClassData object with the collected data
        ClassData newClass = new ClassData(classId, className, classCode, classDescription, teacherName, classStartTime, classEndTime, classRoom, adminId);

        // Store the class data in Firebase
        if (classId != null) {
            classDatabase.child(classId).setValue(newClass)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Class added successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to add class", Toast.LENGTH_SHORT).show());
        }
    }



}
