package com.example.clockit;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Attend_log_History extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TableLayout attendanceTable;
    private EditText manualEntryName, manualEntryID;
    private Button manualEntryButton, logoutButton;  // Added logoutButton
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static HelpFragment newInstance(String param1, String param2) {
        HelpFragment fragment = new HelpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }




    }
    private void ReadFromSheet(){

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("please wait..");
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbyx9TymfwlKVXQA1jD28Bfcfl74PC3z8reY8s0ClaC6EPVLc-rf9s-hZTvTZY61LBh5WQ/exec", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for(int i=0;i<response.length();i++){

                    try{
                        JSONObject jsonObject = response.getJSONObject(i);

                        TableRow newRow = new TableRow(getContext());
                        newRow.setPadding(4,0,0,0);
                        // Create new TextViews for each field (Name, ID, Check-in Time)
                        TextView nameView = new TextView(getContext());
                        nameView.setText(jsonObject.optString("Name", "N/A"));
                        nameView.setPadding(8, 8, 8, 8);

                        TextView idView = new TextView(getContext());
                        idView.setText(jsonObject.optString("UID", "N/A"));
                        idView.setPadding(8, 8, 8, 8);

                        TextView timeView = new TextView(getContext());
                        timeView.setText(jsonObject.optString("TimeIn", "N/A"));
                        timeView.setPadding(8, 8, 8, 8);

                        TextView timeoutView = new TextView(getContext());
                        timeoutView.setText(jsonObject.optString("TimeOut", "N/A"));
                        timeoutView.setPadding(8, 8, 8, 8);


                        // Add TextViews to the new row
                        newRow.addView(nameView);
                        newRow.addView(idView);
                        newRow.addView(timeView);
                        newRow.addView(timeoutView);
                        attendanceTable.addView(newRow);

                        progressDialog.dismiss();
                    }catch(JSONException e){
                        e.printStackTrace();
                        progressDialog.dismiss();

                    }

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getContext(),"NANIIIIIIIII?",Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonArrayRequest);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
View view =  inflater.inflate(R.layout.history_log_attend, container, false);
        attendanceTable = view.findViewById(R.id.attendanceTable);
        ReadFromSheet();
        // Inflate the layout for this fragment
        return view;


    }

}
