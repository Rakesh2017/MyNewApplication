package com.donotauthenticatemyapp.teamaccountmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

import mehdi.sakout.fancybuttons.FancyButton;

public class UserHomePage extends AppCompatActivity implements View.OnClickListener{

    ImageButton logout_ib;
    FancyButton sendMoney_btn;
    TextView uid_tv, totalBalance_tv, transaction_tv, amountInWords_tv;

    EditText amountToBeSent_et, userUIDToSendMoney_et;

    SharedPreferences userIdentifierSharedPreferences;
    private static final String USER_IDENTIFIER_PREF = "userIdentifierPref";
    private static final String USER_UID = "user_uid";
    private static final String USER_KEY = "user_key";

    private static final String LANDING_ACTIVITY = "landingActivity";
    private static final String FIRST_SCREEN = "firstScreen";
    private static final String USER_BALANCE = "userBalance";
    private static final String TOTAL_BALANCE = "total_balance";

    String userUID_tx, amountToBeSent_tx, userUIDToSendMoney_tx;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    FirebaseAuth mAuth;
    Boolean checkUser;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);
        mAuth = FirebaseAuth.getInstance();

        logout_ib = findViewById(R.id.auh_logoutButton);
        sendMoney_btn = findViewById(R.id.auh_sendMoneyButton);
        uid_tv = findViewById(R.id.auh_userUIDTextView);

        totalBalance_tv = findViewById(R.id.auh_totalBalanceTextView);
        transaction_tv = findViewById(R.id.auh_transaction);
        amountToBeSent_et = findViewById(R.id.auh_amountEditText);
        userUIDToSendMoney_et = findViewById(R.id.auh_userUIDEditText);
        amountInWords_tv = findViewById(R.id.auh_numberToEnglishTextView);

        progressDialog = new ProgressDialog(UserHomePage.this);
        progressDialog.setMessage("Please Wait|Making Transaction...");

//        text watcher on amount
        amountToBeSent_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    String return_val_in_english =   EnglishNumberToWords.convert(Long.parseLong(s.toString()));
                    amountInWords_tv.setText(return_val_in_english+".");
                }
                catch (NumberFormatException e){
                    e.printStackTrace();
                }
                amountToBeSent_et.setBackgroundColor(getResources().getColor(R.color.cyanLight));

                if (start == 0) {
                    amountInWords_tv.setText("");
                    amountToBeSent_et.setBackgroundColor(getResources().getColor(R.color.wheat));
                }
                if (count == 1) amountToBeSent_et.setBackgroundColor(getResources().getColor(R.color.cyanLight));
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //        text watcher on uid
        userUIDToSendMoney_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 6){
                    userUIDToSendMoney_et.setBackgroundColor(getResources().getColor(R.color.cyanLight));
                }
                else userUIDToSendMoney_et.setBackgroundColor(getResources().getColor(R.color.wheat));
                if (count == 0) userUIDToSendMoney_et.setBackgroundColor(getResources().getColor(R.color.wheat));
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (start == 6){
                    userUIDToSendMoney_et.setBackgroundColor(getResources().getColor(R.color.cyanLight));
                }
                else userUIDToSendMoney_et.setBackgroundColor(getResources().getColor(R.color.wheat));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        logout_ib.setOnClickListener(this);
        transaction_tv.setOnClickListener(this);
        sendMoney_btn.setOnClickListener(this);
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
            case R.id.auh_logoutButton: //logout
                Logout();
                break;
            case R.id.auh_transaction: // load transaction fragment
                LoadTransactions();
                break;
            case R.id.auh_sendMoneyButton: // send money to other uid
                SendMoney();
                break;
        }

    }//onClick

//    send money
    private void SendMoney() {
        userUIDToSendMoney_tx = userUIDToSendMoney_et.getText().toString().trim();
        amountToBeSent_tx = amountToBeSent_et.getText().toString().trim();
        if (Validations()) {
            progressDialog.show();
           CheckIfUserExist();
        }

    }

//    checking user
    private void CheckIfUserExist() {
        checkUser = false;
        databaseReference.child("userProfile")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){ // for
                            final String uid = snapshot.child("uid").getValue(String.class);
                            if (TextUtils.equals(uid, userUIDToSendMoney_tx)) {  // if user exist
                                Toast.makeText(UserHomePage.this, "User Exist", Toast.LENGTH_SHORT).show();
                                checkUser = true;
                                return;
                            }
                        }//for

                         //if user does not exist
                        if (!checkUser){
                            new MaterialDialog.Builder(UserHomePage.this)
                                    .title("No Such User Exist!")
                                    .titleColor(Color.WHITE)
                                    .content("User with " +
                                            "UID: "+userUIDToSendMoney_tx
                                            +" does not exist. Please re-check the UID.")
                                    .icon(getResources().getDrawable(R.drawable.ic_warning))
                                    .contentColor(getResources().getColor(R.color.lightCoral))
                                    .backgroundColor(getResources().getColor(R.color.black90))
                                    .positiveText(R.string.ok)
                                    .show();
                            progressDialog.dismiss();
                        }
                        progressDialog.dismiss();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        new MaterialDialog.Builder(UserHomePage.this)
                                .title(databaseError.toString())
                                .titleColor(Color.WHITE)
                                .icon(getResources().getDrawable(R.drawable.ic_warning))
                                .contentColor(getResources().getColor(R.color.lightCoral))
                                .backgroundColor(getResources().getColor(R.color.black90))
                                .positiveText(R.string.ok)
                                .show();
                        progressDialog.dismiss();
                    }
                });//database refer
    }
//    checking user

//    send money

//    validations
    public Boolean Validations(){
        if (userUIDToSendMoney_et.length() != 7){
            new MaterialDialog.Builder(UserHomePage.this)
                    .title("Not a Valid User UID")
                    .titleColor(Color.WHITE)
                    .content("UID is 7 in length")
                    .icon(getResources().getDrawable(R.drawable.ic_warning))
                    .contentColor(getResources().getColor(R.color.lightCoral))
                    .backgroundColor(getResources().getColor(R.color.black90))
                    .positiveText(R.string.ok)
                    .show();
            return false;
        }
        else if (TextUtils.isEmpty(amountToBeSent_tx)){
            new MaterialDialog.Builder(UserHomePage.this)
                    .title("Failed")
                    .titleColor(Color.WHITE)
                    .content("Please Enter Amount")
                    .icon(getResources().getDrawable(R.drawable.ic_warning))
                    .contentColor(getResources().getColor(R.color.lightCoral))
                    .backgroundColor(getResources().getColor(R.color.black90))
                    .positiveText(R.string.ok)
                    .show();
            return false;
        }
        return true;
    }
//  validations

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




