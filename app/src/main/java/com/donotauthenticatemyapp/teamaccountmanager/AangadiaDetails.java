package com.donotauthenticatemyapp.teamaccountmanager;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AangadiaDetails extends AppCompatActivity implements View.OnClickListener{

    TextView name_tv, uid_tv, password_tv, question_tv, answer_tv, phone_tv;
    String name_tx, uid_tx, password_tx, question_tx, answer_tx, phone_tx;

    ImageButton back_btn;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private static final String AANGADIA_UID_PREF = "aangadia_uid_pref";
    private static final String AANGADIA_UID = "aangadia_uid";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aangadia_details);

        name_tv = findViewById(R.id.ad_userNameTexView);
        uid_tv = findViewById(R.id.ad_uidTextView);
        password_tv = findViewById(R.id.ad_passwordTextView);
        question_tv = findViewById(R.id.ad_questionTextView);
        answer_tv = findViewById(R.id.ad_answerTextView);

        back_btn = findViewById(R.id.ad_backButton);

        sharedPreferences = getSharedPreferences(AANGADIA_UID_PREF, MODE_PRIVATE);

        back_btn.setOnClickListener(this);
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
                uid_tv.setText(uid_tx);
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

//    onClick
    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id){
//            back button
            case R.id.ad_backButton:
                onBackPressed();
                break;
        }

    }
//    onClick

    //end
}
