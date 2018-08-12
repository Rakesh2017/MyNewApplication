package com.donotauthenticatemyapp.teamaccountmanager;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import pl.droidsonroids.gif.GifImageView;
import util.android.textviews.FontTextView;

public class AangadiaHomePage extends AppCompatActivity implements View.OnClickListener{

    ImageButton logout_ib, addUser_ib, allUsers_btn, account_btn, refresh_ib;
    TextView allUsersCount_tv, uid_tv, totalAangadiaTransactions_tv;
    FontTextView aangadiaName_ftv;

    SharedPreferences userIdentifierSharedPreferences;

    private static final String USER_IDENTIFIER_PREF = "userIdentifierPref";
    private static final String AANGADIA_UID = "aangadia_uid";
    private static final String AANGADIA_NAME_FTV = "aangadia_name_ftv";

    private static final String LANDING_ACTIVITY = "landingActivity";
    private static final String FIRST_SCREEN = "firstScreen";
    private static final String AANGADIA_KEY = "aangadia_key";
    private static final String AANGADIA_PASSWORD_KEY = "aangadia_password_key";

    String aangadiaUID_tx;
    GifImageView loadingGIf;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aangadia_home_page);
        FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();

        logout_ib = findViewById(R.id.ahp_logoutButton);
        addUser_ib = findViewById(R.id.ahp_addUserButton);
        refresh_ib = findViewById(R.id.ahp_refreshImageButton);
        allUsers_btn = findViewById(R.id.ahp_allUsersButton);
        account_btn = findViewById(R.id.ahp_cashButton);
        allUsersCount_tv = findViewById(R.id.ahp_totalUsersTextView);
        aangadiaName_ftv = findViewById(R.id.ahp_aangadiaNameTextView);
        totalAangadiaTransactions_tv = findViewById(R.id.ahp_totalTransactionsTextView);

        uid_tv = findViewById(R.id.ahp_userUIDTextView);

        loadingGIf = findViewById(R.id.ahp_loadingGif);

        addUser_ib.setOnClickListener(this);
        refresh_ib.setOnClickListener(this);
        allUsers_btn.setOnClickListener(this);
        logout_ib.setOnClickListener(this);
        account_btn.setOnClickListener(this);

    }

    @SuppressLint("SetTextI18n")
    public void onStart() {
        super.onStart();

        userIdentifierSharedPreferences = getSharedPreferences(USER_IDENTIFIER_PREF, MODE_PRIVATE);
        aangadiaUID_tx = userIdentifierSharedPreferences.getString(AANGADIA_UID, "");
        uid_tv.setText("UID: " + aangadiaUID_tx);

        loadingGIf.setVisibility(View.VISIBLE);
        new CheckNetworkConnection(AangadiaHomePage.this, new CheckNetworkConnection.OnConnectionCallback() {
            @Override
            public void onConnectionSuccess() {
                //        check password change
                new PasswordCheck(AangadiaHomePage.this).checkIfPasswordChanged();
                setTotalTransactionAmount();
                AllUsersCount();
                setUserName();
              //  loadingGIf.setVisibility(View.GONE);
            }

            @Override
            public void onConnectionFail(String msg) {
                loadingGIf.setVisibility(View.GONE);
                try {
                    new MaterialDialog.Builder(AangadiaHomePage.this)
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
                    loadingGIf.setVisibility(View.GONE);
                }

            }
        }).execute();
    }
//    onStart

    //    setting total amount transactions
    private void setTotalTransactionAmount() {
        final String key = userIdentifierSharedPreferences.getString(AANGADIA_KEY, "");
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
                                    totalAangadiaTransactions_tv.setText("Rs "+formatted_balance);
                                }
                                else totalAangadiaTransactions_tv.setText("Rs 0.0");
                            } //if
                            else { //else
                                totalAangadiaTransactions_tv.setTextColor(getResources().getColor(R.color.red));
                                totalAangadiaTransactions_tv.setText("Rs 0.0 (No Transaction yet)");
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
    //    setting total amount transactions


    //    setting aangadia name at bottom
    private void setUserName() {
        String name = userIdentifierSharedPreferences.getString(AANGADIA_NAME_FTV, "");
        if (!TextUtils.isEmpty(name))  aangadiaName_ftv.setText("User Name: "+name);

        String key = userIdentifierSharedPreferences.getString(AANGADIA_KEY, "");
        databaseReference.child("AangadiaProfile").child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("userName").getValue(String.class);
                        aangadiaName_ftv.setText("User Name: "+name);
                        SharedPreferences.Editor editor = userIdentifierSharedPreferences.edit();
                        editor.putString(AANGADIA_NAME_FTV, name);
                        editor.apply();
                        loadingGIf.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        try {
                            loadingGIf.setVisibility(View.GONE);
                        }
                        catch (Exception e){
                            //do nothing
                        }
                    }
                });
    }

//    getting users count
    private void AllUsersCount() {
        DatabaseReference childReference = databaseReference.child("userProfile");
        Query query = childReference.orderByChild("aangadia_uid").equalTo(aangadiaUID_tx);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String user_count = String.valueOf((int) snapshot.getChildrenCount());
                allUsersCount_tv.setText(user_count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hiding the progress dialog.
            }
        });
    }



    //    onclick
    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {

            //            logout
            case R.id.ahp_logoutButton:
                new MaterialDialog.Builder(this)
                        .title("Logout")
                        .content("Are You Sure to Logout?")
                        .positiveText("Yes")
                        .positiveColor(getResources().getColor(R.color.googleRed))
                        .negativeText("No")
                        .icon(getResources().getDrawable(R.drawable.ic_logout))
                        .backgroundColor(getResources().getColor(R.color.black90))
                        .negativeColor(getResources().getColor(R.color.googleGreen))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                                SharedPreferences sharedpreferences = getSharedPreferences(LANDING_ACTIVITY, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(FIRST_SCREEN, "");
                                editor.clear();
                                editor.apply();

                                mAuth = FirebaseAuth.getInstance();
                                mAuth.signOut();

                                AangadiaHomePage.this.finish();
                                finishAffinity();
                                startActivity(new Intent(AangadiaHomePage.this, Login.class));

                                // TODO
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                // TODO
                            }
                        }) .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // TODO
                    }
                })
                        .show();
                break;

//                add user
            case R.id.ahp_addUserButton:
                try{
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_aangadia_home_page, new AddUser()).addToBackStack("addUser").commit();

                }
                catch (IllegalStateException e){
//                    exception
                }
                break;
                //                all user
            case R.id.ahp_allUsersButton:
                try{
                    startActivity(new Intent(AangadiaHomePage.this, ListOfUsersForAangadia.class));

                }
                catch (IllegalStateException | ActivityNotFoundException e){
//                    exception
                }
                break;

            case R.id.ahp_cashButton:
                try{
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_aangadia_home_page, new AangadiaAccount()).addToBackStack("aangadiaAccount").commit();

                }
                catch (IllegalStateException e){
//                    exception
                }
                break;

//                refresh button
            case R.id.ahp_refreshImageButton:
                try{
                  onStart();
                }
                catch (IllegalStateException e){
//                    exception
                }
                break;

        }


    }//onclick

    //ends
}
