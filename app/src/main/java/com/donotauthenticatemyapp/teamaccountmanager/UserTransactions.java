package com.donotauthenticatemyapp.teamaccountmanager;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserTransactions extends Fragment {

    TextView balance_tv, uid_tv, userName_tv;
    String balance_tx;
    SharedPreferences sharedPreferences, userIdentifierSharedPreferences;

    private static final String UPDATE_PREF = "change_password_pref";
    private static final String USER_NAME = "userName";
    private static final String UID = "uid"; // user uid
    private static final String KEY = "key"; //user key
    private static final String USER_BALANCE = "userBalance";
    private static final String TRANSACTIONS = "transactions";
    private static final String TOTAL_BALANCE = "total_balance";

    private static final String USER_IDENTIFIER_PREF = "userIdentifierPref";
    private static final String USER_IDENTITY = "userIdentity";
    private static final String USER_KEY = "user_key";
    private static final String USER_UID = "user_uid";



    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;
    List<RecyclerViewListAangadiaData> list = new ArrayList<>();
    LinearLayoutManager recyclerViewLayoutManager;
    ProgressDialog progressDialog;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


    public UserTransactions() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        balance_tv = view.findViewById(R.id.ft_totalMoneyTextView);
        uid_tv = view.findViewById(R.id.ft_userUidTextView);
        userName_tv = view.findViewById(R.id.ft_userNameTextView);

        recyclerView = view.findViewById(R.id.ft_recyclerView);
        recyclerView.setHasFixedSize(true);

        recyclerView.isDuplicateParentStateEnabled();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(mLayoutManager);



        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Data...");


        sharedPreferences = getContext().getSharedPreferences(UPDATE_PREF, MODE_PRIVATE);
        userIdentifierSharedPreferences = getActivity().getSharedPreferences(USER_IDENTIFIER_PREF, MODE_PRIVATE);


        return view;
    }

//    onStart
    public void onStart(){
        super.onStart();

        String identity = userIdentifierSharedPreferences.getString(USER_IDENTITY, "");
        if (TextUtils.equals(identity, "aangadia") || TextUtils.equals(identity, "admin")){
            setUIDAndUserNameForAangaisaAndAdmin();
            setBalanceForAangadiaAndAdmin();
            LoadTransactionsForAangadiaAndAdmin();
        }
        else if (TextUtils.equals(identity, "user")){
            setUIDAndUserNameForUser();
            setBalanceForUser();
            LoadTransactionForUser();
            userName_tv.setVisibility(View.GONE);
        }

    }
    //    onStart


    private void setUIDAndUserNameForUser() {
        String user_uid = userIdentifierSharedPreferences.getString(USER_UID, "");
        uid_tv.setText(user_uid);
    }


//    set balance for user
    private void setBalanceForUser() {
        final String key = userIdentifierSharedPreferences.getString(USER_KEY, "");
        databaseReference.child(USER_BALANCE).child(key)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String total_balance = dataSnapshot.child(TOTAL_BALANCE).getValue(String.class);
                        if (!TextUtils.isEmpty(total_balance)){
                            NumberFormat formatter = new DecimalFormat("#,###");
                            String formatted_balance = formatter.format(Long.parseLong(total_balance));
                            balance_tv.setText(formatted_balance);
                        }
                        else {
                            balance_tv.setText("0.00");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    //    transaction for user
    private void LoadTransactionForUser() {

        progressDialog.show();
        final String key = userIdentifierSharedPreferences.getString(USER_KEY, "");
        databaseReference.child(TRANSACTIONS).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(list!=null) {
                    list.clear();  // v v v v important (eliminate duplication of data)
                }

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    RecyclerViewListAangadiaData userData = postSnapshot.getValue(RecyclerViewListAangadiaData.class);
                    list.add(userData);
                }

                adapter = new ListOfUserTransactionsRecyclerViewAdapter(getContext(), list);

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

    //    set balance
    private void setBalanceForAangadiaAndAdmin() {
        final String key = sharedPreferences.getString(KEY, "");
        databaseReference.child(USER_BALANCE).child(key)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String total_balance = dataSnapshot.child(TOTAL_BALANCE).getValue(String.class);
                        if (!TextUtils.isEmpty(total_balance)){
                            NumberFormat formatter = new DecimalFormat("#,###");
                            String formatted_balance = formatter.format(Long.parseLong(total_balance));
                            balance_tv.setText(formatted_balance);
                        }
                        else {
                            balance_tv.setText("0.00");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
//    set balance

    //    setting username and uid
    private void setUIDAndUserNameForAangaisaAndAdmin() {
        String uid = sharedPreferences.getString(UID, "");
        String user_name = sharedPreferences.getString(USER_NAME, "");
        uid_tv.setText(uid);
        userName_tv.setText(user_name);
    }
//    setting username and uid

    //    load transaction for LoadTransactionsForAangadiaAndAdmin
    public void LoadTransactionsForAangadiaAndAdmin(){

        progressDialog.show();
        final String key = sharedPreferences.getString(KEY, "");
        databaseReference.child(TRANSACTIONS).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(list!=null) {
                    list.clear();  // v v v v important (eliminate duplication of data)
                }

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    RecyclerViewListAangadiaData userData = postSnapshot.getValue(RecyclerViewListAangadiaData.class);
                    list.add(userData);
                }

                adapter = new ListOfUserTransactionsRecyclerViewAdapter(getContext(), list);

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


//    end
}









