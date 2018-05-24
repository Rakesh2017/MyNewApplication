package com.donotauthenticatemyapp.teamaccountmanager;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionDetails extends Fragment {

    TextView dateTime_tv, balanceCredited_tv, mode_tv, transactionBy_tv
            , transactionBy_tv0, previousBalance_tv, balanceAfterTransaction_tv, commission_tv, commission1_tv
            ,serialNumber_tv, transactionID_tv, remarks_tv;

    String dateTime_tx, balanceCredited_tx, mode_tx, transactionBy_tx, previousBalance_tx
            , aangadiaKey_tx, balanceAfterTransaction_tx, commission_tx, serialNumber_tx, transactionID_tx, remarks_tx;

    SharedPreferences transactionSharedPreferences;
    private final String transactionPref = "transactionPref";
    private final String AANGADIA_KEY = "aangadia_key";
    private final String CURRENT_BALANCE = "current_balance";
    private final String PREVIOUS_BALANCE = "previous_balance";
    private final String DATE_TIME = "date_time";
    private final String MODE = "mode";
    private final String MONEY_ADDED = "money_added";
    private final String MONEY_ADDED_BY = "money_added_by";

    private final String BALANCE_AFTER_DEBIT = "balance_after_debit";
    private final String BALANCE_AFTER_CREDIT = "balance_after_credit";
    private final String BALANCE_DEBITED = "balance_debited";
    private final String BALANCE_CREDITED = "balance_credited";
    private final String RECEIVER_KEY = "receiver_key";
    private final String SENDER_KEY = "sender_key";
    private final String COMMISSION = "commission";
    private final String COMMISSION_RATE = "commission_rate";
    private final String SERIAL_NUMBER = "serial_number";
    private final String TRANSACTION_ID = "transaction_id";


    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    RelativeLayout relativeLayout;

    public TransactionDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaction_details, container, false);

        dateTime_tv = view.findViewById(R.id.td_dateTimeTextView);
        balanceCredited_tv = view.findViewById(R.id.td_balanceAddedTextView);
        mode_tv = view.findViewById(R.id.td_modeTextView);
        transactionBy_tv = view.findViewById(R.id.td_moneyAddedByTextView);
        transactionBy_tv0 = view.findViewById(R.id.td_moneyAddedBy);
        previousBalance_tv = view.findViewById(R.id.td_previousBalanceTextView);
        balanceAfterTransaction_tv = view.findViewById(R.id.td_balanceAfterTransactionTextView);
        commission_tv = view.findViewById(R.id.td_commissionTextView);
        commission1_tv = view.findViewById(R.id.td_commission);
        transactionID_tv = view.findViewById(R.id.td_transactionIDTextView);
        serialNumber_tv = view.findViewById(R.id.td_serialNumberTextView);
        remarks_tv = view.findViewById(R.id.td_remarksTextView);

        relativeLayout = view.findViewById(R.id.td_relativeLayout);



        return view;
    }

//    onStart
    public void onStart(){
        super.onStart();

        transactionSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(transactionPref, Context.MODE_PRIVATE);
        LoadAnimation();
        setData();
        new CheckNetworkConnection(getActivity(), new CheckNetworkConnection.OnConnectionCallback() {
            @Override
            public void onConnectionSuccess() {

            }
            @Override
            public void onConnectionFail(String msg) {
                Toast.makeText(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
            }
        }).execute();


    }


    private void LoadAnimation() {
        YoYo.with(Techniques.SlideInUp)
                .duration(300)
                .repeat(0)
                .playOn(relativeLayout);
    }

    //    setting values
    @SuppressLint("SetTextI18n")
    private void setData() {
        dateTime_tx = transactionSharedPreferences.getString(DATE_TIME, "");
        mode_tx = transactionSharedPreferences.getString(MODE, "");
        transactionID_tx = transactionSharedPreferences.getString(TRANSACTION_ID, "");
        serialNumber_tx = transactionSharedPreferences.getString(SERIAL_NUMBER, "");

        dateTime_tv.setText(dateTime_tx);
        transactionID_tv.setText(transactionID_tx);
        serialNumber_tv.setText("Sr No: "+serialNumber_tx);
        setRemarks();

//        if mode is money Add
        if(TextUtils.equals(mode_tx, "moneyAdd")) {
            commission_tv.setVisibility(View.GONE);
            commission1_tv.setVisibility(View.GONE);
            balanceCredited_tx = transactionSharedPreferences.getString(MONEY_ADDED, "");
            transactionBy_tx = transactionSharedPreferences.getString(MONEY_ADDED_BY, "");
            previousBalance_tx = transactionSharedPreferences.getString(PREVIOUS_BALANCE, "");
            aangadiaKey_tx = transactionSharedPreferences.getString(AANGADIA_KEY, "");
            balanceAfterTransaction_tx = transactionSharedPreferences.getString(CURRENT_BALANCE, "");



            if (!TextUtils.isEmpty(balanceCredited_tx)) {
                NumberFormat formatter = new DecimalFormat("#,###");
                String formatted_balance = formatter.format(Long.parseLong(balanceCredited_tx));
                balanceCredited_tv.setText("Rs " + formatted_balance);
            } else {
                balanceCredited_tv.setText("Rs 0.00");
            }

            if (!TextUtils.isEmpty(balanceAfterTransaction_tx)) {
                NumberFormat formatter = new DecimalFormat("#,###");
                String formatted_balance = formatter.format(Long.parseLong(balanceAfterTransaction_tx));
                balanceAfterTransaction_tv.setText("Rs " + formatted_balance);
            } else {
                balanceAfterTransaction_tv.setText("Rs 0.00");
            }

                String BALANCE_ADD = "Balance Add";
                mode_tv.setText(BALANCE_ADD);
                balanceCredited_tv.setTextColor(Objects.requireNonNull(getActivity()).getResources().getColor(R.color.green));

            if (!TextUtils.isEmpty(previousBalance_tx)) {
                NumberFormat formatter = new DecimalFormat("#,###");
                String formatted_balance = formatter.format(Long.parseLong(previousBalance_tx));
                previousBalance_tv.setText("Rs " + formatted_balance);
            } else {
                previousBalance_tv.setText("Rs 0.00");
            }


            if (TextUtils.equals(transactionBy_tx, "aangadia") && !TextUtils.isEmpty(transactionBy_tx))
                setAangadiaDetails();
            else if ((TextUtils.equals(transactionBy_tx, "admin") && !TextUtils.isEmpty(transactionBy_tx)))
                transactionBy_tv.setText("Admin");
        }// if mode is money add


//        if mode is credit
        else if (TextUtils.equals(mode_tx, "credit")){
              transactionBy_tv0.setHint("Credited By: ");

              transactionBy_tx = transactionSharedPreferences.getString(SENDER_KEY, "");
              balanceCredited_tx = transactionSharedPreferences.getString(BALANCE_CREDITED, "");
              previousBalance_tx = transactionSharedPreferences.getString(CURRENT_BALANCE, "");
              balanceAfterTransaction_tx = transactionSharedPreferences.getString(BALANCE_AFTER_CREDIT, "");
              commission_tx = transactionSharedPreferences.getString(COMMISSION, "");
              String commission_rate = transactionSharedPreferences.getString(COMMISSION_RATE, "");

            String credit = "Credit";
            mode_tv.setText(credit);
            balanceCredited_tv.setTextColor(Objects.requireNonNull(getActivity()).getResources().getColor(R.color.green));

            if (!TextUtils.isEmpty(balanceCredited_tx)) {
                NumberFormat formatter = new DecimalFormat("#,###");
                String formatted_balance = formatter.format(Long.parseLong(balanceCredited_tx));
                balanceCredited_tv.setText("Rs " + formatted_balance);
            } else {
                balanceCredited_tv.setText("Rs 0.00");
            }

            if (!TextUtils.isEmpty(balanceAfterTransaction_tx)) {
                NumberFormat formatter = new DecimalFormat("#,###");
                String formatted_balance = formatter.format(Long.parseLong(balanceAfterTransaction_tx));
                balanceAfterTransaction_tv.setText("Rs " + formatted_balance);
            } else {
                balanceAfterTransaction_tv.setText("Rs 0.00");
            }

            if (!TextUtils.isEmpty(previousBalance_tx)) {
                NumberFormat formatter = new DecimalFormat("#,###");
                String formatted_balance = formatter.format(Long.parseLong(previousBalance_tx));
                previousBalance_tv.setText("Rs " + formatted_balance);
            } else {
                previousBalance_tv.setText("Rs 0.00");
            }
            if (!TextUtils.isEmpty(commission_tx)) {
                NumberFormat formatter = new DecimalFormat("#,###");
                String formatted_balance = formatter.format(Long.parseLong(commission_tx));
                commission_tv.setText("Rs " + formatted_balance+" ("+commission_rate+"%)");
            } else {
                commission_tv.setText("Rs 0 (0%)");
            }

              CreditAndDebitTransactionBy();
        }


        //        if mode is debit
        else if (TextUtils.equals(mode_tx, "debit")){
            transactionBy_tv0.setHint("Debited To: ");

            transactionBy_tx = transactionSharedPreferences.getString(RECEIVER_KEY, "");
            balanceCredited_tx = transactionSharedPreferences.getString(BALANCE_DEBITED, "");
            previousBalance_tx = transactionSharedPreferences.getString(CURRENT_BALANCE, "");
            balanceAfterTransaction_tx = transactionSharedPreferences.getString(BALANCE_AFTER_DEBIT, "");
            commission_tx = transactionSharedPreferences.getString(COMMISSION, "");
            String commission_rate = transactionSharedPreferences.getString(COMMISSION_RATE, "");

            String debit = "Debit";
            mode_tv.setText(debit);
            balanceCredited_tv.setTextColor(Objects.requireNonNull(getActivity()).getResources().getColor(R.color.red));

            if (!TextUtils.isEmpty(balanceCredited_tx)) {
                NumberFormat formatter = new DecimalFormat("#,###");
                String formatted_balance = formatter.format(Long.parseLong(balanceCredited_tx));
                balanceCredited_tv.setText("Rs " + formatted_balance);
            } else {
                balanceCredited_tv.setText("Rs 0.00");
            }

            if (!TextUtils.isEmpty(balanceAfterTransaction_tx)) {
                NumberFormat formatter = new DecimalFormat("#,###");
                String formatted_balance = formatter.format(Long.parseLong(balanceAfterTransaction_tx));
                balanceAfterTransaction_tv.setText("Rs " + formatted_balance);
            } else {
                balanceAfterTransaction_tv.setText("Rs 0.00");
            }

            if (!TextUtils.isEmpty(previousBalance_tx)) {
                NumberFormat formatter = new DecimalFormat("#,###");
                String formatted_balance = formatter.format(Long.parseLong(previousBalance_tx));
                previousBalance_tv.setText("Rs " + formatted_balance);
            } else {
                previousBalance_tv.setText("Rs 0.00");
            }
            if (!TextUtils.isEmpty(commission_tx)) {
                NumberFormat formatter = new DecimalFormat("#,###");
                String formatted_balance = formatter.format(Long.parseLong(commission_tx));
                commission_tv.setText("Rs " + formatted_balance+" ("+commission_rate+"%)");
            } else {
                commission_tv.setText("Rs 0 (0%)");
            }

            CreditAndDebitTransactionBy();
        }

    }

//    setting transaction ID
    private void setRemarks() {
        databaseReference.child("transaction_remarks").child("remarks")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            if (dataSnapshot.hasChild(transactionID_tx)){
                                String remarks = dataSnapshot.child(transactionID_tx).getValue(String.class);
                                if (!TextUtils.isEmpty(remarks))
                                    remarks_tv.setText(remarks);
                                else {
                                    remarks_tv.setTextColor(getResources().getColor(R.color.black90));
                                    remarks_tv.setText("NO REMARKS");
                                }

                            }
                            else{
                                remarks_tv.setTextColor(getResources().getColor(R.color.black90));
                                remarks_tv.setText("NO REMARKS");
                            }

                        }
                        catch (DatabaseException e){
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    //    getting sender/receiver uid and name
    private void CreditAndDebitTransactionBy() {
        databaseReference.child("userProfile")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String uid = dataSnapshot.child(transactionBy_tx).child("uid").getValue(String.class);
                        String name = dataSnapshot.child(transactionBy_tx).child("userName").getValue(String.class);
                        transactionBy_tv.setText(uid+", "+name);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    //    getting sender/receiver uid and name

    //    set aangadia details
    private void setAangadiaDetails() {
        databaseReference.child("AangadiaProfile").child(aangadiaKey_tx)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("userName").getValue(String.class);
                        String uid = dataSnapshot.child("uid").getValue(String.class);

                        transactionBy_tv.setText("Aangadia"+ " (" + uid +", "+name+")");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


//ends
}
