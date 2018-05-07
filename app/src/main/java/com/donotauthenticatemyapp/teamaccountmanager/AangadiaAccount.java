package com.donotauthenticatemyapp.teamaccountmanager;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class AangadiaAccount extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter ;
    List<RecyclerViewListAangadiaData> list = new ArrayList<>();

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ProgressDialog progressDialog;
    private SharedPreferences userIdentifierSharedPreferences;
    protected static final String USER_IDENTIFIER_PREF = "userIdentifierPref";
    private static final String AANGADIA_KEY = "aangadia_key";

    TextView balance_tv;

    public AangadiaAccount() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_aangadia_account, container, false);

        recyclerView = view.findViewById(R.id.faa_recyclerView);
        recyclerView.setHasFixedSize(true);

        recyclerView.isDuplicateParentStateEnabled();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(mLayoutManager);



        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Data...");
        userIdentifierSharedPreferences = getActivity().getSharedPreferences(USER_IDENTIFIER_PREF, MODE_PRIVATE);

        return view;
    }

    //    onStart
    public void onStart(){
        super.onStart();

        LoadData();
    }
    //    onStart

    //    load data
    private void LoadData() {
        progressDialog.show();
        String key = userIdentifierSharedPreferences.getString(AANGADIA_KEY,"");
        databaseReference.child("aangadiaCashInAccount").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(list!=null) {
                    list.clear();  // v v v v important (eliminate duplication of data)
                }

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    RecyclerViewListAangadiaData userData = postSnapshot.getValue(RecyclerViewListAangadiaData.class);
                    list.add(userData);
                }

                adapter = new ListOfAangadiaAccountRecyclerViewAdapter(getContext(), list);

                recyclerView.setAdapter(adapter);

                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hiding the progress dialog.
                progressDialog.dismiss();
            }
        });
    }
//    load data

//end
}
