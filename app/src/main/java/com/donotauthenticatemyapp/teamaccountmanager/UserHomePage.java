package com.donotauthenticatemyapp.teamaccountmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class UserHomePage extends AppCompatActivity implements View.OnClickListener{

    ImageButton logout_ib;
    TextView uid_tv, totalBalance_tv, transaction_tv;

    SharedPreferences userIdentifierSharedPreferences;
    private static final String USER_IDENTIFIER_PREF = "userIdentifierPref";
    private static final String USER_UID = "user_uid";
    private static final String USER_KEY = "user_key";

    private static final String LANDING_ACTIVITY = "landingActivity";
    private static final String FIRST_SCREEN = "firstScreen";
    private static final String USER_BALANCE = "userBalance";
    private static final String TOTAL_BALANCE = "total_balance";

    String userUID_tx;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);
        mAuth = FirebaseAuth.getInstance();

        logout_ib = findViewById(R.id.auh_logoutButton);
        uid_tv = findViewById(R.id.auh_userUIDTextView);

        totalBalance_tv = findViewById(R.id.auh_totalBalanceTextView);
        transaction_tv = findViewById(R.id.auh_transaction);

        logout_ib.setOnClickListener(this);
        transaction_tv.setOnClickListener(this);
    }

//    onStart
    public void onStart(){
        super.onStart();

        userIdentifierSharedPreferences = getSharedPreferences(USER_IDENTIFIER_PREF, MODE_PRIVATE);
        userUID_tx = userIdentifierSharedPreferences.getString(USER_UID, "");
        uid_tv.setText("UID: "+userUID_tx);
        setTotalBalance();
    }
//    onStart

//    onclick
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.auh_logoutButton:
                Logout();
                break;
            case R.id.auh_transaction:
                LoadTransactions();
                break;
        }

    }//onClick


//    Load Transactions
    private void LoadTransactions() {
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_user_home_page, new UserTransactions()).addToBackStack("transactions").commit();
    }
//    Load Transactions


//    setBalance
//    setting total balance
private void setTotalBalance() {
    final String key = userIdentifierSharedPreferences.getString(USER_KEY, "");
    databaseReference.child(USER_BALANCE).child(key)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String total_balance = dataSnapshot.child(TOTAL_BALANCE).getValue(String.class);
                    if (!TextUtils.isEmpty(total_balance)){
                        NumberFormat formatter = new DecimalFormat("#,###");
                        String formatted_balance = formatter.format(Long.parseLong(total_balance));
                        totalBalance_tv.setText(formatted_balance);
                    }
                    else {
                        totalBalance_tv.setText("0.00");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
}
//set balance


//    logout
    private void Logout() {
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
                        editor.apply();

                        mAuth = FirebaseAuth.getInstance();
                        mAuth.signOut();

                        UserHomePage.this.finish();
                        startActivity(new Intent(UserHomePage.this, Login.class));

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
    }
//    logout

    //ends
}




