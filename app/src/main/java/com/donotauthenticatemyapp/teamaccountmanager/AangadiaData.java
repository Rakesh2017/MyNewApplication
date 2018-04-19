package com.donotauthenticatemyapp.teamaccountmanager;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class AangadiaData extends Fragment {

    TextView name_tv, uid_tv, password_tv, question_tv, answer_tv, phone_tv;
    String name_tx, uid_tx, password_tx, question_tx, answer_tx, phone_tx;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private static final String AANGADIA_UID_PREF = "aangadia_uid_pref";
    private static final String AANGADIA_UID = "aangadia_uid";
    SharedPreferences sharedPreferences;

    public AangadiaData() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_aangadia_data, container, false);

        name_tv = view.findViewById(R.id.ad_userNameTexView);
        uid_tv = view.findViewById(R.id.ad_uidTextView);
        password_tv = view.findViewById(R.id.ad_passwordTextView);
        question_tv = view.findViewById(R.id.ad_questionTextView);
        answer_tv = view.findViewById(R.id.ad_answerTextView);

        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(AANGADIA_UID_PREF, MODE_PRIVATE);


        return view;

    }

    public void onStart(){
        super.onStart();

        loadData();
    }

//    load data
    private void loadData() {
        String uid = sharedPreferences.getString(AANGADIA_UID, "");
        databaseReference.child("AangadiaProfile").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name_tx = dataSnapshot.child("userName").getValue(String.class);
                uid_tx = dataSnapshot.child("uid").getValue(String.class);
                password_tx = dataSnapshot.child("password").getValue(String.class);
                question_tx = dataSnapshot.child("security_question").getValue(String.class);
                answer_tx = dataSnapshot.child("security_answer").getValue(String.class);
                phone_tx = dataSnapshot.child("phone").getValue(String.class);

                name_tv.setText(name_tx);
                uid_tv.setText(uid_tx.substring(0,7));
                password_tv.setText(password_tx);
                question_tv.setText(question_tx);
                answer_tv .setText(answer_tx);
                //.setText();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
