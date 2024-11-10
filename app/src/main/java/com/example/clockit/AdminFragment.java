package com.example.clockit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.Calendar;

public class AdminFragment extends Fragment {
    private TextView welcomeMessage, dateTime;
    private TableLayout classScheduleTable;
    private DatabaseReference userDatabase, classDatabase;
    private FirebaseAuth firebaseAuth;
    private Handler handler;
    private DrawerLayout drawerLayout;

    public AdminFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Enable options menu in the fragment
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.admin_fragment_menu, menu); // Only use the fragment-specific menu
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        classDatabase = FirebaseDatabase.getInstance().getReference("classes");

        // Initialize UI elements
        welcomeMessage = view.findViewById(R.id.welcomeMessage);
        dateTime = view.findViewById(R.id.dateTime);
        classScheduleTable = view.findViewById(R.id.classScheduleTable);

        // Fetch and display username
        fetchAndDisplayUsername();

        // Display classes created by the current admin
        displayClassesForAdmin();

        // Update date and time every minute
        handler = new Handler();
        updateDateTime();

        // Set up Toolbar and DrawerLayout
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        drawerLayout = view.findViewById(R.id.drawer_menu);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }

        // Set up drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                activity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set up NavigationView
        NavigationView navigationView = view.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                loadFragment(new AdminFragment());
            } else if (itemId == R.id.nav_participants) {
                loadFragment(new ParticipantsFragment());
            } else if (itemId == R.id.nav_announcements) {
                loadFragment(new AnnouncmentsFragment());
            } else if (itemId == R.id.nav_card_assign) {
                loadFragment(new CardAssignFragment());
            } else if (itemId == R.id.nav_add_classes) {
                Intent intent = new Intent(getActivity(), AddClassActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.nav_help) {
                loadFragment(new HelpFragment());
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Add this code to set up the center "Add Class" button
        Button addClassButton = view.findViewById(R.id.addClassButton);
        addClassButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddClassActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void fetchAndDisplayUsername() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String username = dataSnapshot.child("username").getValue(String.class);
                        welcomeMessage.setText(username != null ? "Welcome Professor " + username + "!" : "Welcome Professor!");
                    } else {
                        Toast.makeText(getContext(), "User data not found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "No user logged in.", Toast.LENGTH_SHORT).show();
        }
    }
    private void deleteClass(String classId) {
        classDatabase.child(classId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Class deleted successfully", Toast.LENGTH_SHORT).show();
                    displayClassesForAdmin(); // Refresh the table after deletion
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to delete class: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void archiveClass(String classId) {
        // Set an "archived" flag in your class data. Modify this according to your database structure.
        classDatabase.child(classId).child("archived").setValue(true)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Class archived successfully", Toast.LENGTH_SHORT).show();
                    displayClassesForAdmin(); // Optionally refresh the table after archiving
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to archive class: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void showPopupMenu(View view, String classId) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.class_item_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_delete) {
                deleteClass(classId);
                return true;
            } else if (item.getItemId() == R.id.action_archive) {
                archiveClass(classId);
                return true;
            }
            return false;
        });


        popupMenu.show();
    }


    private void displayClassesForAdmin() {
        String currentAdminId = firebaseAuth.getCurrentUser().getUid();

        classDatabase.orderByChild("adminId").equalTo(currentAdminId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear all rows except the header row
                int childCount = classScheduleTable.getChildCount();
                if (childCount > 1) {
                    classScheduleTable.removeViews(1, childCount - 1);
                }

                if (!dataSnapshot.hasChildren()) {
                    // If no classes are found, display a message
                    Toast.makeText(getContext(), "No classes found for this admin.", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot classSnapshot : dataSnapshot.getChildren()) {
                    String startTime = classSnapshot.child("startTime").getValue(String.class);
                    String courseCode = classSnapshot.child("classCode").getValue(String.class);
                    String roomNumber = classSnapshot.child("roomNumber").getValue(String.class);
                    String classId = classSnapshot.getKey();

                    // Create a new TableRow for each class entry
                    TableRow row = new TableRow(getContext());
                    row.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));

                    // Create TextViews for each column and add them to the row
                    TextView timeTextView = new TextView(getContext());
                    timeTextView.setText(startTime);
                    timeTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                    timeTextView.setGravity(Gravity.CENTER);
                    timeTextView.setPadding(8, 8, 8, 8);
                    row.addView(timeTextView);

                    TextView courseTextView = new TextView(getContext());
                    courseTextView.setText(courseCode);
                    courseTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                    courseTextView.setGravity(Gravity.CENTER);
                    courseTextView.setPadding(8, 8, 8, 8);
                    row.addView(courseTextView);

                    TextView roomTextView = new TextView(getContext());
                    roomTextView.setText(roomNumber);
                    roomTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                    roomTextView.setGravity(Gravity.CENTER);
                    roomTextView.setPadding(8, 8, 8, 8);
                    row.addView(roomTextView);

                    // Placeholder for Attendance
                    TextView attendanceTextView = new TextView(getContext());
                    attendanceTextView.setText(""); // Placeholder for Attendance column
                    attendanceTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                    attendanceTextView.setGravity(Gravity.CENTER);
                    attendanceTextView.setPadding(8, 8, 8, 8);
                    row.addView(attendanceTextView);

                    // Add an overflow icon (3 dots)
                    ImageView overflowMenu = new ImageView(getContext());
                    overflowMenu.setImageResource(android.R.drawable.ic_menu_more);
                    overflowMenu.setOnClickListener(v -> showPopupMenu(v, classId));
                    row.addView(overflowMenu);

                    // Add the row to the TableLayout
                    classScheduleTable.addView(row);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load classes: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void updateDateTime() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentDateTimeString = DateFormat.format("EEEE, MMM d, yyyy - h:mm a", Calendar.getInstance().getTime()).toString();
                dateTime.setText(currentDateTimeString);
                handler.postDelayed(this, 60000); // Update every minute
            }
        }, 0);
    }

    private void loadFragment(Fragment fragment) {
        if (getView() != null) {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            Toast.makeText(getContext(), "Fragment container not found.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            clearLoginState();
            redirectToLoginFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearLoginState() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("ClockItPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("rememberMe");
        editor.apply();
    }

    private void redirectToLoginFragment() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new LoginFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
