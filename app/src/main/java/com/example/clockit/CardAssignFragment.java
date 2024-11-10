package com.example.clockit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.google.firebase.auth.FirebaseAuth; // Added FirebaseAuth import

public class CardAssignFragment extends Fragment {
    private DrawerLayout drawerLayout;
    private Handler handler;
    private EditText cardInput;
    private Button assignButton;
    private ProgressBar loadingView;
    private TextView successMessage;

    public CardAssignFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_assign, container, false);

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
            } else if (itemId == R.id.nav_students) { // Changed to nav_students
                loadFragment(new StudentsFragment()); // Use StudentsFragment instead
            } else if (itemId == R.id.nav_announcements) {
                loadFragment(new AnnouncmentsFragment());
            } else if (itemId == R.id.nav_card_assign) {
                loadFragment(new CardAssignFragment());
            } else if (itemId == R.id.nav_add_classes) {
                loadFragment(new AddClassesFragment());
            } else if (itemId == R.id.nav_help) {
                loadFragment(new HelpFragment());
            } else if (itemId == R.id.nav_logout) { // Handle logout
                FirebaseAuth.getInstance().signOut();
                loadFragment(new LoginFragment());
                Toast.makeText(getContext(), "Logged out successfully.", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Initialize UI elements for card assignment
        cardInput = view.findViewById(R.id.cardInput);
        assignButton = view.findViewById(R.id.assignButton);
        loadingView = view.findViewById(R.id.loadingView);
        successMessage = view.findViewById(R.id.successMessage);

        // Set up click listener for the Assign button
        assignButton.setOnClickListener(v -> assignCard());

        return view;
    }

    private void assignCard() {
        String cardInfo = cardInput.getText().toString().trim();

        if (cardInfo.isEmpty()) {
            Toast.makeText(getContext(), "Please enter card information", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hide the keyboard
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        // Hide input and button, show loading
        cardInput.setVisibility(View.GONE);
        assignButton.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);

        // Simulate card assignment delay (replace with actual logic)
        handler = new Handler();
        handler.postDelayed(() -> {
            // Hide loading and show success message
            loadingView.setVisibility(View.GONE);
            successMessage.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Card assigned successfully!", Toast.LENGTH_SHORT).show();

            // After showing the success message, delay for 5 seconds, then navigate to MainActivity
            handler.postDelayed(() -> {
                // Navigate to MainActivity
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                // Optionally, finish the current activity to remove it from the back stack
                getActivity().finish();
            }, 5000); // 5-second delay

        }, 3000); // 3 seconds delay for simulated assignment
    }

    private void loadFragment(Fragment fragment) {
        // Check if fragment_container exists in the current layout
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
}
