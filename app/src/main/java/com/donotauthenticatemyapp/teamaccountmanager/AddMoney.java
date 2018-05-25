package com.donotauthenticatemyapp.teamaccountmanager;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Trace;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

import mehdi.sakout.fancybuttons.FancyButton;

import static android.content.Context.MODE_PRIVATE;
import static com.donotauthenticatemyapp.teamaccountmanager.AddUser.TIME_SERVER;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddMoney extends Fragment implements View.OnClickListener {

    private static final String UPDATE_PREF = "change_password_pref";
    private static final String KEY = "key";
    private static final String UID = "uid";
    private static final String USER_NAME = "userName";
    private static final String USER_BALANCE = "userBalance";
    private static final String TRANSACTIONS = "transactions";
    private static final String TOTAL_BALANCE = "total_balance";
    private static final String MONEY_ADDED_BY = "money_added_by";
    private static final String PREVIOUS_BALANCE = "previous_balance";
    private static final String MONEY_ADDED = "money_added";
    private static final String MODE = "mode";
    private static final String CURRENT_BALANCE = "current_balance";
    private static final String DATE_TIME = "dateTime";
    private static final String AANGADIA_KEY = "aangadia_key";
    private SharedPreferences sharedPreferences, userIdentifierSharedPreferences;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mAuth1;

    FancyButton submit_btn;

    String uid_tx, userName_tx, today_dateTime;
    String money_tx;
    TextView uid_tv, userName_tv, amountInWords_tv, totalBalance_tv;

    protected EditText money_et;
    ProgressDialog progressDialog;

    protected static final String USER_IDENTIFIER_PREF = "userIdentifierPref";
    protected static final String USER_IDENTITY = "userIdentity";

    CoordinatorLayout coordinatorLayout;


    public AddMoney() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_money, container, false);

        uid_tv = view.findViewById(R.id.am_userUidTextView);
        userName_tv = view.findViewById(R.id.am_userNameTextView);
        amountInWords_tv = view.findViewById(R.id.am_amountInWordsTextView);
        totalBalance_tv = view.findViewById(R.id.am_totalBalanceTextView);

        userIdentifierSharedPreferences = getActivity().getSharedPreferences(USER_IDENTIFIER_PREF, MODE_PRIVATE);
        sharedPreferences = getActivity().getSharedPreferences(UPDATE_PREF, MODE_PRIVATE);
        uid_tx = sharedPreferences.getString(UID, "");
        userName_tx = sharedPreferences.getString(USER_NAME, "");

        submit_btn = view.findViewById(R.id.am_submitButton);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Adding Balance...");

        coordinatorLayout = view.findViewById(R.id.fragment_container_add_money);

        money_et = view.findViewById(R.id.am_addMoneyEditText);
        money_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    String return_val_in_english =   EnglishNumberToWords.convert(Long.parseLong(s.toString()));
                    amountInWords_tv.setText(return_val_in_english+".");
                }
                catch (NumberFormatException e){
                    e.printStackTrace();
                }
                if (start == 0) amountInWords_tv.setText("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // TODO Auto-generated method stub
            }
        });

        submit_btn.setOnClickListener(this);

        return view;
    }


//    on Start
    public void onStart(){
        super.onStart();
        LoadAnimation();
        SetUIDAndUserName();
        setTotalBalance();
    }

//    load animation
    private void LoadAnimation() {
        YoYo.with(Techniques.SlideInRight)
                .duration(300)
                .repeat(0)
                .playOn(coordinatorLayout);
    }

//    setting total balance
    private void setTotalBalance() {
        final String key = sharedPreferences.getString(KEY, "");
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

    //    setting username and uid
    private void SetUIDAndUserName() {
        uid_tv.setText(uid_tx);
        userName_tv.setText(userName_tx);
    }

//    onClickListener
    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.am_submitButton){
            money_tx = money_et.getText().toString();
            if (MoneyValidation()) AddMoneyFunction();
        } //id if ends

    }//onclick ends

    public boolean MoneyValidation(){
        try {
            if (TextUtils.isEmpty(money_tx)){
                new MaterialDialog.Builder(getActivity())
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
            else if (Integer.parseInt(money_tx) < 1){
                new MaterialDialog.Builder(getActivity())
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
        }
        catch (Exception e){
            return false;
//            exception
        }
        return true;
    }

//    add money function
    public void AddMoneyFunction(){

        new MaterialDialog.Builder(getActivity())
                .title("Are you sure to Add Money!")
                .content("Rs "+money_tx+"/- will be added to Account with UID:"+uid_tx
                        +", User Name: "+userName_tx)
                .contentColorRes(R.color.white)
                .titleColor(getResources().getColor(R.color.whiteSmoke))
                .positiveText("Confirm")
                .positiveColorRes(R.color.googleGreen)
                .negativeText("Cancel")
                .negativeColorRes(R.color.googleRed)
                .backgroundColor(getResources().getColor(R.color.black90))
                .icon(getResources().getDrawable(R.drawable.ic_transfer_money))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        progressDialog.show();

                        final Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                int count = 0;
                                Date dateTime = null;
                                NTPUDPClient timeClient = new NTPUDPClient();
                                timeClient.setDefaultTimeout(1000);

                                for (int retries = 7; retries >= 0; retries--) { // for
                                    try {
                                        count++;
                                        InetAddress inetAddress = InetAddress.getByName("in.pool.ntp.org");

                                        TimeInfo timeInfo = timeClient.getTime(inetAddress);
                                        long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();   //server time
                                        dateTime = new Date(returnTime);

                                        String myDate = String.valueOf(dateTime);
                                        String date, time, year, month;
                                        date = myDate.substring(8, 10);
                                        month = myDate.substring(4, 7);
                                        time = myDate.substring(11, 16);
                                        year = myDate.substring(myDate.length() - 4, myDate.length());
                                        today_dateTime = time + ", " + date + " " + month + " " + year;
//                                    when get time then hit function
                                        if (!TextUtils.isEmpty(today_dateTime)){
                                            String identity = userIdentifierSharedPreferences.getString(USER_IDENTITY, "");
                                            if (TextUtils.equals(identity, "admin")) AddMoneyByAdmin();
                                            else if (TextUtils.equals(identity, "aangadia")) AddMoneyByAangadia();
                                            break;
                                        }


                                    } catch (IOException e) {

                                        progressDialog.dismiss();
                                    }
                                }//for
                            }
                        });
                        thread.start();

                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }
//    add money function

//    money added by aangadia
    private void AddMoneyByAangadia() {
        mAuth1 = FirebaseAuth.getInstance();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final String aangadia_key = mAuth1.getCurrentUser().getUid();
        final String key = sharedPreferences.getString(KEY, "");
        money_tx = money_et.getText().toString();
        //getting amount from database
        databaseReference.child(USER_BALANCE).child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() { //add value event listener will run in loop(avoid using it)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String balance_to_be_added_at_database = null;
                        final String previous_total_balance = dataSnapshot.child(TOTAL_BALANCE).getValue(String.class);
                        if (!TextUtils.isEmpty(previous_total_balance)) {
                            final int temp_balance = Integer.parseInt(previous_total_balance) + Integer.parseInt(money_tx);
                            balance_to_be_added_at_database = String.valueOf(temp_balance);
                        } else balance_to_be_added_at_database = money_tx;

                        //now setting new total money to database
                        databaseReference.child(USER_BALANCE).child(key)
                                .child(TOTAL_BALANCE).setValue(balance_to_be_added_at_database).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });//setting new total ends

                        String timestamp_unique_key = databaseReference.push().getKey();

                        DatabaseReference databaseReferenceMoneyAddedByAangadia = databaseReference.child(TRANSACTIONS).child(key)
                                .child(timestamp_unique_key);
                        databaseReferenceMoneyAddedByAangadia.child(MONEY_ADDED_BY).setValue("aangadia");
                        databaseReferenceMoneyAddedByAangadia.child(MONEY_ADDED).setValue(money_tx);
                        databaseReferenceMoneyAddedByAangadia.child(CURRENT_BALANCE).setValue(balance_to_be_added_at_database);
                        databaseReferenceMoneyAddedByAangadia.child(PREVIOUS_BALANCE).setValue(previous_total_balance);
                        databaseReferenceMoneyAddedByAangadia.child(MODE).setValue("moneyAdd");
                        databaseReferenceMoneyAddedByAangadia.child(DATE_TIME).setValue(today_dateTime);
                        databaseReferenceMoneyAddedByAangadia.child(AANGADIA_KEY).setValue(aangadia_key);
                        databaseReferenceMoneyAddedByAangadia.child("transaction_id").setValue(timestamp_unique_key);

//                        setting data in aangadia cash in account
                        String aangadia_key = userIdentifierSharedPreferences.getString(AANGADIA_KEY, "");
                        DatabaseReference databaseReferenceMoneyAddedInCashInAccount = databaseReference.child("aangadiaCashInAccount").child(aangadia_key)
                                .child(timestamp_unique_key);
                        databaseReferenceMoneyAddedInCashInAccount.child(MONEY_ADDED).setValue(money_tx);
                        databaseReferenceMoneyAddedInCashInAccount.child(DATE_TIME).setValue(today_dateTime);
                        databaseReferenceMoneyAddedInCashInAccount.child("user_key").setValue(key);
                        databaseReferenceMoneyAddedInCashInAccount.child("transaction_id").setValue(timestamp_unique_key);


                        progressDialog.dismiss();
                        new MaterialDialog.Builder(getActivity())
                                .title("Transaction Successful")
                                .titleColor(Color.BLACK)
                                .content("Rs "+money_tx +"/- is credited to Account with " +
                                        "UID: "+uid_tx)
                                .icon(getResources().getDrawable(R.drawable.ic_success))
                                .contentColor(getResources().getColor(R.color.black))
                                .backgroundColor(getResources().getColor(R.color.white))
                                .positiveText(R.string.ok)
                                .show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressDialog.dismiss();
                        new MaterialDialog.Builder(getActivity())
                                .title("Failed")
                                .titleColor(Color.BLACK)
                                .content(databaseError.toString())
                                .icon(getResources().getDrawable(R.drawable.ic_warning))
                                .contentColor(getResources().getColor(R.color.lightCoral))
                                .backgroundColor(getResources().getColor(R.color.white))
                                .positiveText(R.string.ok)
                                .show();
                    }
                });
    }
//    money by aangadia


//    money added by admin

    private void AddMoneyByAdmin() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final String key = sharedPreferences.getString(KEY, "");
        money_tx = money_et.getText().toString();
        //getting amount from database
        databaseReference.child(USER_BALANCE).child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() { //add value event listener will run in loop(avoid using it)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String balance_to_be_added_at_database = null;
                        final String previous_total_balance = dataSnapshot.child(TOTAL_BALANCE).getValue(String.class);
                        if (!TextUtils.isEmpty(previous_total_balance)) {
                            final int temp_balance = Integer.parseInt(previous_total_balance) + Integer.parseInt(money_tx);
                            balance_to_be_added_at_database = String.valueOf(temp_balance);
                        } else balance_to_be_added_at_database = money_tx;

                        //now setting new total money to database
                        databaseReference.child(USER_BALANCE).child(key)
                                .child(TOTAL_BALANCE).setValue(balance_to_be_added_at_database).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });//setting new total ends

                        String timestamp_unique_key = databaseReference.push().getKey();

                        DatabaseReference databaseReferenceMoneyAddedByADMIN = databaseReference.child(TRANSACTIONS).child(key)
                                .child(timestamp_unique_key);
                        databaseReferenceMoneyAddedByADMIN.child(MONEY_ADDED_BY).setValue("admin");
                        databaseReferenceMoneyAddedByADMIN.child(MONEY_ADDED).setValue(money_tx);
                        databaseReferenceMoneyAddedByADMIN.child(CURRENT_BALANCE).setValue(balance_to_be_added_at_database);
                        databaseReferenceMoneyAddedByADMIN.child(PREVIOUS_BALANCE).setValue(previous_total_balance);
                        databaseReferenceMoneyAddedByADMIN.child(MODE).setValue("moneyAdd");
                        databaseReferenceMoneyAddedByADMIN.child(DATE_TIME).setValue(today_dateTime);
                        databaseReferenceMoneyAddedByADMIN.child("transaction_id").setValue(timestamp_unique_key);

                        progressDialog.dismiss();
                        new MaterialDialog.Builder(getActivity())
                                .title("Transaction Successful")
                                .titleColor(Color.BLACK)
                                .content("Rs "+money_tx +" is credited to Account with " +
                                        "UID: "+uid_tx)
                                .icon(getResources().getDrawable(R.drawable.ic_success))
                                .contentColor(getResources().getColor(R.color.black))
                                .backgroundColor(getResources().getColor(R.color.white))
                                .positiveText(R.string.ok)
                                .show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressDialog.dismiss();
                        try {
                            new MaterialDialog.Builder(getActivity())
                                    .title("Failed")
                                    .titleColor(Color.BLACK)
                                    .content(databaseError.toString())
                                    .icon(getResources().getDrawable(R.drawable.ic_warning))
                                    .contentColor(getResources().getColor(R.color.lightCoral))
                                    .backgroundColor(getResources().getColor(R.color.white))
                                    .positiveText(R.string.ok)
                                    .show();
                        }
                        catch (Exception e){
//            exception
                        }


                    }
                });
    }
    //    money added by admin

    //ends
}
