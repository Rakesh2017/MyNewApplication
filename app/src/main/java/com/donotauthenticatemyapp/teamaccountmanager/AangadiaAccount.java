package com.donotauthenticatemyapp.teamaccountmanager;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private SharedPreferences userIdentifierSharedPreferences, sharedPreferences;
    protected static final String USER_IDENTIFIER_PREF = "userIdentifierPref";
    private static final String AANGADIA_KEY = "aangadia_key";
    private static final String USER_IDENTITY = "userIdentity";

    private static final String AANGADIA_UID_PREF = "aangadia_uid_pref";
    private static final String AANGADIA_UID = "aangadia_uid";

    TextView balance_tv, setLimit_tv, listLength_tv;
    EditText limit_et;

    int limit = 10;

    public AangadiaAccount() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_aangadia_account, container, false);

        recyclerView = view.findViewById(R.id.faa_recyclerView);
        balance_tv = view.findViewById(R.id.faa_transactionsTextView);

        setLimit_tv = view.findViewById(R.id.faa_setLimitTextView);
        listLength_tv = view.findViewById(R.id.faa_listLengthTextView);
        limit_et = view.findViewById(R.id.faa_limitEditText);

        recyclerView.setHasFixedSize(true);

        recyclerView.isDuplicateParentStateEnabled();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(mLayoutManager);

        setLimit_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String get_limit = limit_et.getText().toString().trim();

                if (!TextUtils.isEmpty(get_limit)){
                    limit = Integer.parseInt(get_limit);

                    final String identity = userIdentifierSharedPreferences.getString(USER_IDENTITY, "");
                    new CheckNetworkConnection(getActivity(), new CheckNetworkConnection.OnConnectionCallback() {
                        @Override
                        public void onConnectionSuccess() {
                            if (TextUtils.equals(identity, "aangadia")) {
                                LoadDataForAangadia();
                            } else if (TextUtils.equals(identity, "admin")){
                                LoadDataForAdmin();
                            }
                        }
                        @Override
                        public void onConnectionFail(String msg) {
                            NoInternetConnectionAlert noInternetConnectionAlert = new NoInternetConnectionAlert(getActivity());
                            noInternetConnectionAlert.DisplayNoInternetConnection();
                            progressDialog.dismiss();
                        }
                    }).execute();


                }


            }
        });

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Data...");
        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(AANGADIA_UID_PREF, MODE_PRIVATE);
        userIdentifierSharedPreferences = getActivity().getSharedPreferences(USER_IDENTIFIER_PREF, MODE_PRIVATE);

        return view;
    }

    //    onStart
    public void onStart() {
        super.onStart();

        final String identity = userIdentifierSharedPreferences.getString(USER_IDENTITY, "");

        progressDialog.show();
        new CheckNetworkConnection(getActivity(), new CheckNetworkConnection.OnConnectionCallback() {
            @Override
            public void onConnectionSuccess() {
                if (TextUtils.equals(identity, "aangadia")) {
                    setBalanceAangadia();
                    LoadDataForAangadia();
                    ListLengthAangadia();
                } else if (TextUtils.equals(identity, "admin")){
                    setBalanceAdmin();
                    LoadDataForAdmin();
                    ListLengthAdmin();
                }
            }

            @Override
            public void onConnectionFail(String msg) {
                progressDialog.dismiss();
                try {
                    new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                            .title("No Internet Access!")
                            .titleColor(Color.BLACK)
                            .content("No internet connectivity detected. Please make sure you have working internet connection and try again.")
                            .icon(getResources().getDrawable(R.drawable.ic_no_internet_connection))
                            .contentColor(getResources().getColor(R.color.black))
                            .backgroundColor(getResources().getColor(R.color.white))
                            .positiveColor(getResources().getColor(R.color.green))
                            .negativeText("Cancel")
                            .negativeColor(getResources().getColor(R.color.red))
                            .positiveText("Try Again!")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    onStart();
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .cancelable(false)
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).execute();

    }
    //    onStart




    public void ListLengthAangadia(){
        final String key = userIdentifierSharedPreferences.getString(AANGADIA_KEY,"");
        databaseReference.child("aangadiaCashInAccount")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try { //try
                            long total = dataSnapshot.child(key).getChildrenCount();
                            listLength_tv.setText("Total: "+String.valueOf(total));
                        } //try
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    public void ListLengthAdmin(){
        final String key = sharedPreferences.getString(AANGADIA_UID,"");
        databaseReference.child("aangadiaCashInAccount")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try { //try
                            long total = dataSnapshot.child(key).getChildrenCount();
                            listLength_tv.setText("Total: "+String.valueOf(total));
                        } //try
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }


//    if used by aangadia
    private void setBalanceAangadia() {
        final String key = userIdentifierSharedPreferences.getString(AANGADIA_KEY,"");
        databaseReference.child("aangadiaCashInAccount")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try { //try
                            if (dataSnapshot.hasChild(key)){ //if
                                long sum = 0;
                                for (DataSnapshot snapshot : dataSnapshot.child(key).getChildren()) {
                                    sum = sum + Long.parseLong(snapshot.child("money_added").getValue(String.class));
                                }
                                if (!TextUtils.isEmpty(String.valueOf(sum))){
                                    NumberFormat formatter = new DecimalFormat("#,###");
                                    String formatted_balance = formatter.format(sum);
                                    balance_tv.setText(formatted_balance);
                                }
                                else balance_tv.setText("0.0");
                            } //if
                            else { //else
                                balance_tv.setTextColor(getResources().getColor(R.color.red));
                                balance_tv.setText("0.0 (No Transaction)");
                            } //else
                        } //try
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    //    if used by admin
    private void setBalanceAdmin() {
        final String key = sharedPreferences.getString(AANGADIA_UID, "");
        databaseReference.child("aangadiaCashInAccount")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try { //try
                            if (dataSnapshot.hasChild(key)){ //if
                                long sum = 0;
                                for (DataSnapshot snapshot : dataSnapshot.child(key).getChildren()) {
                                    sum = sum + Long.parseLong(snapshot.child("money_added").getValue(String.class));
                                }
                                if (!TextUtils.isEmpty(String.valueOf(sum))){
                                    NumberFormat formatter = new DecimalFormat("#,###");
                                    String formatted_balance = formatter.format(sum);
                                    balance_tv.setText(formatted_balance);
                                }
                                else balance_tv.setText("0.0");
                            } //if
                            else { //else
                                balance_tv.setTextColor(getResources().getColor(R.color.red));
                                balance_tv.setText("0.0 (No Transaction)");
                            } //else
                        } //try
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }




    //    load data
    private void LoadDataForAangadia() {
        progressDialog.show();
        final String key = userIdentifierSharedPreferences.getString(AANGADIA_KEY,"");
        databaseReference.child("aangadiaCashInAccount").child(key).limitToLast(limit).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(list!=null) {
                    list.clear();  // v v v v important (eliminate duplication of data)
                }

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    RecyclerViewListAangadiaData userData = postSnapshot.getValue(RecyclerViewListAangadiaData.class);
                    list.add(userData);
                }

                if (list.isEmpty()){
                    showEmptyPage();
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


    //    load data
    private void LoadDataForAdmin() {
        progressDialog.show();
        final String key = sharedPreferences.getString(AANGADIA_UID, "");
        databaseReference.child("aangadiaCashInAccount").child(key).limitToLast(limit).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(list!=null) {
                    list.clear();  // v v v v important (eliminate duplication of data)
                }

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    RecyclerViewListAangadiaData userData = postSnapshot.getValue(RecyclerViewListAangadiaData.class);
                    list.add(userData);
                }

                if (list.isEmpty()){
                    showEmptyPage();
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

    //    show empty page
    public void showEmptyPage(){
        try {
            new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                    .title("Empty!")
                    .titleColor(Color.BLACK)
                    .content("Aangadia have not made any transaction yet.")
                    .icon(getResources().getDrawable(R.drawable.ic_warning))
                    .contentColor(getResources().getColor(R.color.black))
                    .backgroundColor(getResources().getColor(R.color.white))
                    .positiveText(R.string.ok)
                    .show();
        }
        catch (Exception e){
//            exception
        }

    }
    //end

//end
}
