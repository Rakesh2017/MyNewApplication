package com.donotauthenticatemyapp.teamaccountmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AangadiaDetails extends AppCompatActivity implements View.OnClickListener{

    TextView name_tv, uid_tv, password_tv, question_tv, answer_tv, phone_tv;
    String name_tx, uid_tx, password_tx, question_tx, answer_tx, phone_tx;

    ImageButton back_btn, editPassword_btn, editName_btn, editPhone_btn, home_btn;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private static final String AANGADIA_UID_PREF = "aangadia_uid_pref";
    private static final String AANGADIA_UID = "aangadia_uid";
    private static final String UPDATE_PREF = "change_password_pref";
    private static final String OLD_PASSWORD = "old_password";
    private static final String USER_NAME = "userName";
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String UID = "uid";
    private static final String KEY = "key";
    private static final String PATH = "path";

    SharedPreferences sharedPreferences, passwordSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aangadia_details);

        name_tv = findViewById(R.id.ad_userNameTextView);
        uid_tv = findViewById(R.id.ad_uidTextView);
        password_tv = findViewById(R.id.ad_passwordTextView);
        question_tv = findViewById(R.id.ad_questionTextView);
        answer_tv = findViewById(R.id.ad_answerTextView);
        phone_tv = findViewById(R.id.ad_phoneTextView);

        editPassword_btn = findViewById(R.id.ad_editPasswordButton);
        editName_btn = findViewById(R.id.ad_editNameButton);
        editPhone_btn = findViewById(R.id.ad_editPhoneButton);

        back_btn = findViewById(R.id.ad_backButton);
        home_btn = findViewById(R.id.ad_homeButton);

        sharedPreferences = getSharedPreferences(AANGADIA_UID_PREF, MODE_PRIVATE);
        passwordSharedPreferences = getSharedPreferences(UPDATE_PREF, MODE_PRIVATE);

        back_btn.setOnClickListener(this);
        home_btn.setOnClickListener(this);
        editPassword_btn.setOnClickListener(this);
        editName_btn.setOnClickListener(this);
        editPhone_btn.setOnClickListener(this);
    }

    public void onStart(){
        super.onStart();

        loadData();
    }

    //    load data
    private void loadData() {
        final String key = sharedPreferences.getString(AANGADIA_UID, "");
        databaseReference.child("AangadiaProfile").child(key).addValueEventListener(new ValueEventListener() {
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
                phone_tv.setText(phone_tx);

//              saving for password change
                SharedPreferences.Editor editor = passwordSharedPreferences.edit();
                editor.putString(OLD_PASSWORD, password_tx);
                editor.putString(UID, uid_tx);
                editor.putString(USER_NAME, name_tx);
                editor.putString(KEY, key);
                editor.putString(PATH, "AangadiaProfile");
                editor.putString(PHONE_NUMBER, phone_tx);
                editor.apply();

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

//                change password
            case R.id.ad_editPasswordButton:
                if (TextUtils.isEmpty(password_tx) || TextUtils.isEmpty(uid_tx) ){
                    Toast.makeText(this, "something went wrong!", Toast.LENGTH_SHORT).show();
                }
                else getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_aangadia_details, new ChangePassword()).addToBackStack("changePassword").commit();
                break;
//                changing name
            case R.id.ad_editNameButton:
                 getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_aangadia_details, new ChangeName()).addToBackStack("changeName").commit();
                 break;

//                 changing phone
            case R.id.ad_editPhoneButton:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_aangadia_details, new ChangePhone()).addToBackStack("changePhone").commit();
                break;

//                home button
            case R.id.ad_homeButton:
                startActivity(new Intent(this, AdminHomePage.class));
                this.finish();
                break;

        }

    }
//    onClick

    //end
}
