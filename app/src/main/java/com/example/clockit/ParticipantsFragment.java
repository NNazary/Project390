package com.example.clockit;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ParticipantsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParticipantsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView listView;
    private SearchView searchView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> dataList = new ArrayList<>();;
    public ParticipantsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ParticipantsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ParticipantsFragment newInstance(String param1, String param2) {
        ParticipantsFragment fragment = new ParticipantsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);

        return fragment;
    }
    private void ReadFromSheet( ){

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("please wait..");
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycby_C0NokOkmB19nJ-26uNo8IJJ4leP9zfR3B7WctHqdNP3YBk-PCJeRf3Yd7vQwULVx/exec", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for(int i=0;i<response.length();i++){

                    try{
                        JSONObject jsonObject = response.getJSONObject(i);




                        dataList.add(jsonObject.optString("Name", "N/A"));

                        // Add TextViews to the new row


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
        View view = inflater.inflate(R.layout.fragment_participants, container, false);

        listView = view.findViewById(R.id.listProfiles);
        searchView = view.findViewById(R.id.searchItem);

        // Initialize the dataList as an ArrayList
        dataList = new ArrayList<>();

        // Initialize the adapter and attach it to the ListView
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        // Show progress dialog
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait..");
        progressDialog.show();

        // Volley request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                "https://script.google.com/macros/s/AKfycby_C0NokOkmB19nJ-26uNo8IJJ4leP9zfR3B7WctHqdNP3YBk-PCJeRf3Yd7vQwULVx/exec",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                dataList.add(jsonObject.optString("Name", "N/A"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        // Notify adapter about the updated data
                        adapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);

        listView.setOnItemClickListener((parent, view1, position, id) -> {

            

        });

        // Set up SearchView listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        return view;
    }



}