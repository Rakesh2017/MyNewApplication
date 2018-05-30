package com.donotauthenticatemyapp.teamaccountmanager;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class Passbook extends AppCompatActivity {

    TextView balance_tv, uidAndUserName_tv, setLimit_tv, listLength_tv;
    EditText limit_et;

    int limit = 20;

    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;
    List<RecyclerViewListAangadiaData> list = new ArrayList<>();
    LinearLayoutManager recyclerViewLayoutManager;
    ProgressDialog progressDialog;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passbook);

        balance_tv = findViewById(R.id.apx_totalMoneyTextView);
        uidAndUserName_tv = findViewById(R.id.apx_userUidTextView);


        setLimit_tv = findViewById(R.id.apx_setLimitTextView);
        listLength_tv = findViewById(R.id.apx_listLengthTextView);
        limit_et = findViewById(R.id.apx_limitEditText);


        recyclerView = findViewById(R.id.apx_recyclerView);
        recyclerView.setHasFixedSize(true);

        recyclerView.isDuplicateParentStateEnabled();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(Passbook.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(mLayoutManager);

        ImageButton backButton_btn = findViewById(R.id.apx_backButton);

        backButton_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setLimit_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String get_limit = limit_et.getText().toString().trim();

                if (!TextUtils.isEmpty(get_limit)){
                    limit = Integer.parseInt(get_limit);
                    final String identity = userIdentifierSharedPreferences.getString(USER_IDENTITY, "");
                    new CheckNetworkConnection(Passbook.this, new CheckNetworkConnection.OnConnectionCallback() {
                        @Override
                        public void onConnectionSuccess() {
                            if (TextUtils.equals(identity, "aangadia") || TextUtils.equals(identity, "admin")){
                                ListLengthAdminAndAangadia();
                                LoadTransactionsForAangadiaAndAdmin();
                            }
                            else if (TextUtils.equals(identity, "user")){
                                ListLengthUser();
                                LoadTransactionForUser();
                            }
                        }
                        @Override
                        public void onConnectionFail(String msg) {
                            NoInternetConnectionAlert noInternetConnectionAlert = new NoInternetConnectionAlert(Passbook.this);
                            noInternetConnectionAlert.DisplayNoInternetConnection();
                            progressDialog.dismiss();
                        }
                    }).execute();

                }
            }
        });

        progressDialog = new ProgressDialog(Passbook.this);
        progressDialog.setMessage("Loading Data...");


        sharedPreferences = getSharedPreferences(UPDATE_PREF, MODE_PRIVATE);
        userIdentifierSharedPreferences = getSharedPreferences(USER_IDENTIFIER_PREF, MODE_PRIVATE);

    }

    //    onStart
    public void onStart(){
        super.onStart();

        new CheckNetworkConnection(Passbook.this, new CheckNetworkConnection.OnConnectionCallback() {
            @Override
            public void onConnectionSuccess() {
                String identity = userIdentifierSharedPreferences.getString(USER_IDENTITY, "");
                if (TextUtils.equals(identity, "aangadia") || TextUtils.equals(identity, "admin")){
                    setUIDAndUserNameForAangaisaAndAdmin();
                    setBalanceForAangadiaAndAdmin();
                    ListLengthAdminAndAangadia();
                    LoadTransactionsForAangadiaAndAdmin();
                }
                else if (TextUtils.equals(identity, "user")){
                    setUIDAndUserNameForUser();
                    setBalanceForUser();
                    ListLengthUser();
                    LoadTransactionForUser();
                   // userName_tv.setVisibility(View.GONE);
                }
            }
            @Override
            public void onConnectionFail(String msg) {
                progressDialog.dismiss();
                try {
                    new MaterialDialog.Builder(Passbook.this)
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

    public void ListLengthAdminAndAangadia(){
        final String key = sharedPreferences.getString(KEY, "");
        databaseReference.child("transactions").child(key)
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

    public void ListLengthUser(){
        final String key = userIdentifierSharedPreferences.getString(USER_KEY, "");
        databaseReference.child("transactions").child(key)
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


    private void setUIDAndUserNameForUser() {
        String user_uid = userIdentifierSharedPreferences.getString(USER_UID, "");
        uidAndUserName_tv.setText(user_uid);
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
                            balance_tv.setText("Rs "+formatted_balance);
                        }
                        else {
                            balance_tv.setText("Rs 0.00");
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
        databaseReference.child(TRANSACTIONS).child(key).limitToLast(limit).addListenerForSingleValueEvent(new ValueEventListener() {
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

                adapter = new PassbookListRecyclerViewAdapter(Passbook.this, list);

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
                            balance_tv.setText("Rs "+formatted_balance);
                        }
                        else {
                            balance_tv.setText("Rs 0.00");
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
        uidAndUserName_tv.setText(uid+", "+user_name);
        //userName_tv.setText(user_name);
    }
//    setting username and uid

    //    load transaction for LoadTransactionsForAangadiaAndAdmin
    public void LoadTransactionsForAangadiaAndAdmin(){

        progressDialog.show();
        final String key = sharedPreferences.getString(KEY, "");
        databaseReference.child(TRANSACTIONS).child(key).limitToLast(limit).addListenerForSingleValueEvent(new ValueEventListener() {
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
                adapter = new PassbookListRecyclerViewAdapter(Passbook.this, list);

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

    //    show empty page
    public void showEmptyPage(){
        new MaterialDialog.Builder(Passbook.this)
                .title("Empty!")
                .titleColor(Color.BLACK)
                .content("No Data Available")
                .icon(getResources().getDrawable(R.drawable.ic_warning))
                .contentColor(getResources().getColor(R.color.black))
                .backgroundColor(getResources().getColor(R.color.white))
                .positiveText(R.string.ok)
                .show();
    }

    public void onDestroy(){
        super.onDestroy();
        if (progressDialog.isShowing()) progressDialog.dismiss();
    }


    //end
}
