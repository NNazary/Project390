package com.example.clockit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.fragment.app.Fragment;

public class AddClassesFragment extends Fragment {

    private Spinner deptSpinner;

    public AddClassesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_classes, container, false);

        // Initialize UI elements
        deptSpinner = view.findViewById(R.id.deptSpinner);

        // Setup the spinner (optional setup if you need it for UI consistency)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.departments_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deptSpinner.setAdapter(adapter);

        // Save button click listener (no actual data saving)
        view.findViewById(R.id.saveButton).setOnClickListener(v -> saveClassData());

        return view;
    }

    // Empty saveClassData method
    private void saveClassData() {
        // Method intentionally left empty
    }
}
