package com.donotauthenticatemyapp.teamaccountmanager;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class AdminBalance extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter ;
    List<RecyclerViewListAangadiaData> list = new ArrayList<>();

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ProgressDialog progressDialog;

    TextView balance_tv, setLimit_tv, listLength_tv;
    EditText limit_et;

    int limit = 10;

    public AdminBalance() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_balance, container, false);

        recyclerView = view.findViewById(R.id.fab_recyclerView);
        balance_tv = view.findViewById(R.id.fab_totalMoneyTextView);

        setLimit_tv = view.findViewById(R.id.fab_setLimitTextView);
        listLength_tv = view.findViewById(R.id.fab_listLengthTextView);
        limit_et = view.findViewById(R.id.fab_limitEditText);

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
                final String get_limit = limit_et.getText().toString().trim();

                if (!TextUtils.isEmpty(get_limit)){
                    new CheckNetworkConnection(getActivity(), new CheckNetworkConnection.OnConnectionCallback() {
                        @Override
                        public void onConnectionSuccess() {
                            limit = Integer.parseInt(get_limit);
                            LoadData();
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

        return view;
    }

//    onStart
    public void onStart(){
        super.onStart();

        progressDialog.show();
        new CheckNetworkConnection(Objects.requireNonNull(getActivity()), new CheckNetworkConnection.OnConnectionCallback() {
            @Override
            public void onConnectionSuccess() {
                setBalance();
                ListLength();
                LoadData();
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

    public void ListLength(){
        databaseReference.child("adminAccount")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try { //try
                            long total = dataSnapshot.getChildrenCount();
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

    private void setBalance() {
        databaseReference.child("adminBalance")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String balance = dataSnapshot.child("total_balance").getValue(String.class);
                        if (!TextUtils.isEmpty(balance)){
                            NumberFormat formatter = new DecimalFormat("#,###");
                            String formatted_balance = formatter.format(Long.parseLong(balance));
                            balance_tv.setText(formatted_balance);
                        }
                        else balance_tv.setText("0.0");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


//    load data
    private void LoadData() {
        progressDialog.show();
        databaseReference.child("adminAccount").limitToLast(limit).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(list!=null) {
                    list.clear();  // v v v v important (eliminate duplication of data)
                }

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    RecyclerViewListAangadiaData userData = postSnapshot.getValue(RecyclerViewListAangadiaData.class);
                    list.add(userData);
                    }

                if (list.isEmpty())
                    showEmptyPage();

                adapter = new ListOfAdminBalanceTransactionsRecyclerViewAdapter(getContext(), list);

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
        new MaterialDialog.Builder(getContext())
                .title("Empty!")
                .titleColor(Color.BLACK)
                .content("No Data Available.")
                .icon(getResources().getDrawable(R.drawable.ic_warning))
                .contentColor(getResources().getColor(R.color.black))
                .backgroundColor(getResources().getColor(R.color.white))
                .positiveText(R.string.ok)
                .show();
    }


}
