package com.donotauthenticatemyapp.teamaccountmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class UserDetails extends AppCompatActivity implements View.OnClickListener {

    TextView name_tv, uid_tv, password_tv, question_tv, answer_tv, phone_tv, state_tv, city_tv, addMoney_tv
            , totalBalance_tv;
    String name_tx, uid_tx, password_tx, question_tx, answer_tx, phone_tx, state_tx, city_tx;

    ImageButton back_btn, editPassword_btn, editName_btn, editPhone_btn, home_btn;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private static final String USER_UID_PREF = "user_uid_pref";
    private static final String USER_UID = "user_uid";
    private static final String USER_IDENTIFIER_PREF = "userIdentifierPref";
    private static final String USER_IDENTITY = "userIdentity";

    private static final String UPDATE_PREF = "change_password_pref";
    private static final String OLD_PASSWORD = "old_password";
    private static final String USER_NAME = "userName";
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String UID = "uid";
    private static final String KEY = "key";
    private static final String PATH = "path";
    private static final String USER_BALANCE = "userBalance";
    private static final String TOTAL_BALANCE = "total_balance";


    SharedPreferences sharedPreferences, passwordSharedPreferences, userIdentifierSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        name_tv = findViewById(R.id.ud_userNameTextView);
        uid_tv = findViewById(R.id.ud_uidTextView);
        password_tv = findViewById(R.id.ud_passwordTextView);
        question_tv = findViewById(R.id.ud_questionTextView);
        answer_tv = findViewById(R.id.ud_answerTextView);
        phone_tv = findViewById(R.id.ud_phoneTextView);
        state_tv = findViewById(R.id.ud_stateTextView);
        city_tv = findViewById(R.id.ud_cityTextView);
        addMoney_tv = findViewById(R.id.ud_addMoneyTextView);
        totalBalance_tv = findViewById(R.id.ud_totalMoneyTextView);

        editPassword_btn = findViewById(R.id.ud_editPasswordButton);
        editName_btn = findViewById(R.id.ud_editNameButton);
        editPhone_btn = findViewById(R.id.ud_editPhoneButton);

        back_btn = findViewById(R.id.ud_backButton);
        home_btn = findViewById(R.id.ud_homeButton);

        sharedPreferences = getSharedPreferences(USER_UID_PREF, MODE_PRIVATE);
        passwordSharedPreferences = getSharedPreferences(UPDATE_PREF, MODE_PRIVATE);
        userIdentifierSharedPreferences = getSharedPreferences(USER_IDENTIFIER_PREF, MODE_PRIVATE);


        back_btn.setOnClickListener(this);
        home_btn.setOnClickListener(this);
        editPassword_btn.setOnClickListener(this);
        editName_btn.setOnClickListener(this);
        editPhone_btn.setOnClickListener(this);
        addMoney_tv.setOnClickListener(this);
    }

//    onStart
    public void onStart(){
        super.onStart();
        loadData();
        setTotalBalance();
    }


    //    load data
    private void loadData() {
        final String key = sharedPreferences.getString(USER_UID, "");
        databaseReference.child("userProfile").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name_tx = dataSnapshot.child("userName").getValue(String.class);
                uid_tx = dataSnapshot.child("uid").getValue(String.class);
                password_tx = dataSnapshot.child("password").getValue(String.class);
                question_tx = dataSnapshot.child("security_question").getValue(String.class);
                answer_tx = dataSnapshot.child("security_answer").getValue(String.class);
                phone_tx = dataSnapshot.child("phone").getValue(String.class);
                state_tx = dataSnapshot.child("state").getValue(String.class);
                city_tx = dataSnapshot.child("city").getValue(String.class);

                name_tv.setText(name_tx);
                uid_tv.setText(uid_tx);
                password_tv.setText(password_tx);
                question_tv.setText(question_tx);
                answer_tv .setText(answer_tx);
                phone_tv.setText(phone_tx);
                state_tv.setText(state_tx);
                city_tv.setText(city_tx);

                SharedPreferences.Editor editor = passwordSharedPreferences.edit();
                editor.putString(OLD_PASSWORD, password_tx);
                editor.putString(UID, uid_tx);
                editor.putString(USER_NAME, name_tx);
                editor.putString(KEY, key);
                editor.putString(PATH, "userProfile");
                editor.putString(PHONE_NUMBER, phone_tx);
                editor.apply();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    //    setting total balance
    private void setTotalBalance() {
        final String key = sharedPreferences.getString(USER_UID, "");
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
    //    setting total balance


    //    onClick
    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id){
//            back button
            case R.id.ud_backButton:
                onBackPressed();
                break;
//                change password
            case R.id.ud_editPasswordButton:
                if (TextUtils.isEmpty(password_tx) || TextUtils.isEmpty(uid_tx) ){
                    Toast.makeText(this, "something went wrong!", Toast.LENGTH_SHORT).show();
                }
                else getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_user_details, new ChangePassword()).addToBackStack("changePassword").commit();
                break;
//                changing name
            case R.id.ud_editNameButton:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_user_details, new ChangeName()).addToBackStack("changeName").commit();
                break;

//                 changing phone
            case R.id.ud_editPhoneButton:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_user_details, new ChangePhone()).addToBackStack("changePhone").commit();
                break;

//                home button
            case R.id.ud_homeButton:
                String identity = userIdentifierSharedPreferences.getString(USER_IDENTITY, "");
                if (TextUtils.equals(identity, "admin")) startActivity(new Intent(this, AdminHomePage.class));
                else if (TextUtils.equals(identity, "aangadia")) startActivity(new Intent(this, AangadiaHomePage.class));
                this.finish();
                break;

            //                add money
            case R.id.ud_addMoneyTextView:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_user_details, new AddMoney()).addToBackStack("addMoney").commit();
                break;

        }

    }
//    onClick

    //end
}
