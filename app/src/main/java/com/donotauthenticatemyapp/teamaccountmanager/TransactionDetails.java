package com.donotauthenticatemyapp.teamaccountmanager;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;


/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionDetails extends Fragment {

    TextView dateTime_tv, balanceCredited_tv, mode_tv, transactionBy_tv, previousBalance_tv;
    String dateTime_tx, balanceCredited_tx, mode_tx, transactionBy_tx, previousBalance_tx, aangadiaKey_tx;

    SharedPreferences transactionSharedPreferences;
    private final String transactionPref = "transactionPref";
    private final String AANGADIA_KEY = "aangadia_key";
    private final String CURRENT_BALANCE = "current_balance";
    private final String PREVIOUS_BALANCE = "previous_balance";
    private final String DATE_TIME = "date_time";
    private final String MODE = "mode";
    private final String MONEY_ADDED = "money_added";
    private final String MONEY_ADDED_BY = "money_added_by";

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    RelativeLayout relativeLayout;

    public TransactionDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaction_details, container, false);

        dateTime_tv = view.findViewById(R.id.td_dateTimeTextView);
        balanceCredited_tv = view.findViewById(R.id.td_balanceAddedTextView);
        mode_tv = view.findViewById(R.id.td_modeTextView);
        transactionBy_tv = view.findViewById(R.id.td_moneyAddedByTextView);
        previousBalance_tv = view.findViewById(R.id.td_previousBalanceTextView);
        relativeLayout = view.findViewById(R.id.td_relativeLayout);



        return view;
    }

//    onStart
    public void onStart(){
        super.onStart();

        transactionSharedPreferences = getActivity().getSharedPreferences(transactionPref, Context.MODE_PRIVATE);
        LoadAnimation();
        setData();

    }


    private void LoadAnimation() {
        YoYo.with(Techniques.SlideInUp)
                .duration(300)
                .repeat(0)
                .playOn(relativeLayout);
    }

    //    setting values
    private void setData() {
        dateTime_tx = transactionSharedPreferences.getString(DATE_TIME, "");
        balanceCredited_tx = transactionSharedPreferences.getString(MONEY_ADDED, "");
        mode_tx = transactionSharedPreferences.getString(MODE, "");
        transactionBy_tx = transactionSharedPreferences.getString(MONEY_ADDED_BY, "");
        previousBalance_tx = transactionSharedPreferences.getString(PREVIOUS_BALANCE, "");
        aangadiaKey_tx = transactionSharedPreferences.getString(AANGADIA_KEY, "");

        dateTime_tv.setText(dateTime_tx);
        if (!TextUtils.isEmpty(balanceCredited_tx)){
            NumberFormat formatter = new DecimalFormat("#,###");
            String formatted_balance = formatter.format(Long.parseLong(balanceCredited_tx));
            balanceCredited_tv.setText("Rs "+formatted_balance);
        }
        else {
            balanceCredited_tv.setText("Rs 0.00");
        }

        if (TextUtils.equals(mode_tx,"moneyAdd")){
            String BALANCE_ADD = "Balance Add";
            mode_tv.setText(BALANCE_ADD);
            balanceCredited_tv.setTextColor(getActivity().getResources().getColor(R.color.green));
        }

        if (!TextUtils.isEmpty(previousBalance_tx)){
            NumberFormat formatter = new DecimalFormat("#,###");
            String formatted_balance = formatter.format(Long.parseLong(previousBalance_tx));
            previousBalance_tv.setText("Rs "+formatted_balance);
        }
        else {
            previousBalance_tv.setText("Rs 0.00");
        }

        if (TextUtils.equals(transactionBy_tx, "aangadia") && !TextUtils.isEmpty(transactionBy_tx)) setAangadiaDetails();
        else if ((TextUtils.equals(transactionBy_tx, "admin") && !TextUtils.isEmpty(transactionBy_tx))) transactionBy_tv.setText("Admin");


    }

//    set aangadia details
    private void setAangadiaDetails() {
        databaseReference.child("AangadiaProfile").child(aangadiaKey_tx)
                .addListenerForSingleValueEvent(new ValueEventListener() {
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
