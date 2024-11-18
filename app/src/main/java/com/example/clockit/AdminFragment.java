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
        //see_logs = view.findViewById(R.id.see_logs_btn);



        // Fetch and display username
        fetchAndDisplayUsername();

        // Display classes created by the current admin
        displayClassesForAdmin(view);

        // Update date and time every minute
        handler = new Handler();
        updateDateTime();

        // Set up Toolbar and DrawerLayout
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        drawerLayout = view.findViewById(R.id.drawer_layout);
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
            } else if (itemId == R.id.nav_students) { // Load StudentsFragment
                loadFragment(new StudentsFragment());
            } else if (itemId == R.id.nav_announcements) {
                loadFragment(new AnnouncementsFragment());
            } else if (itemId == R.id.nav_card_assign) {
                loadFragment(new CardAssignFragment());
            } else if(itemId == R.id.attend_log) {
                loadFragment(new Attend_log_History());

            }else if (itemId == R.id.nav_add_classes) {
                Intent intent = new Intent(getActivity(), AddClassActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.nav_help) {
                loadFragment(new HelpFragment());
            } else if (itemId == R.id.nav_logout) { // Handle logout
                firebaseAuth.signOut();
                loadFragment(new LoginFragment());
                Toast.makeText(getContext(), "Logged out successfully.", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Add Class button in center of layout
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



    private void displayClassesForAdmin(View view) {
        String currentAdminId = firebaseAuth.getCurrentUser().getUid();
        classDatabase.orderByChild("adminId").equalTo(currentAdminId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int childCount = classScheduleTable.getChildCount();
                if (childCount > 1) {
                    classScheduleTable.removeViews(1, childCount - 1);
                }

                if (!dataSnapshot.hasChildren()) {
                    Toast.makeText(getContext(), "No classes found for this admin.", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot classSnapshot : dataSnapshot.getChildren()) {
                    String startTime = classSnapshot.child("startTime").getValue(String.class);
                    String courseCode = classSnapshot.child("classCode").getValue(String.class);
                    String endTime = classSnapshot.child("endTime").getValue(String.class);
                    String roomNumber = classSnapshot.child("roomNumber").getValue(String.class);
                    String classId = classSnapshot.getKey();

                    TableRow row = new TableRow(getContext());
                    row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                    TextView timeTextView = new TextView(getContext());
                    timeTextView.setText(startTime);
                    timeTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                    timeTextView.setGravity(Gravity.CENTER);
                    timeTextView.setPadding(8, 8, 8, 8);
                    row.addView(timeTextView);

                    TextView endtimeTextView = new TextView(getContext());
                    endtimeTextView.setText(endTime);
                    endtimeTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                    endtimeTextView.setGravity(Gravity.CENTER);
                    endtimeTextView.setPadding(8, 8, 8, 8);
                    row.addView(endtimeTextView);

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

                    // Create the attendance button dynamically for each row
                    Button seeLogsButton = new Button(getContext());
                    seeLogsButton.setText("See Logs");
                    seeLogsButton.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                    seeLogsButton.setGravity(Gravity.CENTER);
                    seeLogsButton.setPadding(8, 8, 8, 8);
                    Intent intent = new Intent(requireContext(), Attendance_log.class);
                    intent.putExtra("classId", classId);  // Pass classId if needed in the log activity
                    intent.putExtra("startTime", startTime);
                    intent.putExtra("endTime", endTime);

                    // Set the onClickListener for the button
                    seeLogsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), Attendance_log.class);
                            intent.putExtra("classId", classId);  // Pass classId if needed in the log activity
                            intent.putExtra("startTime", startTime);
                            intent.putExtra("endTime", endTime);

                            startActivity(intent);
                        }
                    });

                    // Add the button to the row
                    row.addView(seeLogsButton);

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
                handler.postDelayed(this, 60000);
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
