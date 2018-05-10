package com.donotauthenticatemyapp.teamaccountmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

public class AangadiaDetails extends AppCompatActivity implements View.OnClickListener{

    TextView name_tv, uid_tv, password_tv, question_tv, answer_tv, phone_tv
            , totalTransactionAmount_tv, transactions_tv, userNameHeader_tv;
    String name_tx, uid_tx, password_tx, question_tx, answer_tx, phone_tx;

    Button expandTextView_btn;

    ImageButton back_btn, editPassword_btn, editName_btn, editPhone_btn, home_btn;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    RelativeLayout hiddenProfile_rl;

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

        userNameHeader_tv = findViewById(R.id.ad_userNameHeaderTextView);
        name_tv = findViewById(R.id.ad_userNameTextView);
        uid_tv = findViewById(R.id.ad_uidTextView);
        password_tv = findViewById(R.id.ad_passwordTextView);
        question_tv = findViewById(R.id.ad_questionTextView);
        answer_tv = findViewById(R.id.ad_answerTextView);
        phone_tv = findViewById(R.id.ad_phoneTextView);
        totalTransactionAmount_tv = findViewById(R.id.ad_totalTransactionMoneyTextView);
        transactions_tv = findViewById(R.id.ad_transaction);
        expandTextView_btn = findViewById(R.id.ad_expandTextView);

        editPassword_btn = findViewById(R.id.ad_editPasswordButton);
        editName_btn = findViewById(R.id.ad_editNameButton);
        editPhone_btn = findViewById(R.id.ad_editPhoneButton);

        hiddenProfile_rl = findViewById(R.id.ad_hiddenProfileRelativeLayout);

        back_btn = findViewById(R.id.ad_backButton);
        home_btn = findViewById(R.id.ad_homeButton);

        sharedPreferences = getSharedPreferences(AANGADIA_UID_PREF, MODE_PRIVATE);
        passwordSharedPreferences = getSharedPreferences(UPDATE_PREF, MODE_PRIVATE);

        back_btn.setOnClickListener(this);
        home_btn.setOnClickListener(this);
        editPassword_btn.setOnClickListener(this);
        editName_btn.setOnClickListener(this);
        editPhone_btn.setOnClickListener(this);
        transactions_tv.setOnClickListener(this);
        expandTextView_btn.setOnClickListener(this);
    }

    public void onStart(){
        super.onStart();

        loadData();
        setTotalTransactionAmount();
    }

//    setting total amount transactions
    private void setTotalTransactionAmount() {
        final String key = sharedPreferences.getString(AANGADIA_UID, "");
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
                            totalTransactionAmount_tv.setText(formatted_balance);
                        }
                         else totalTransactionAmount_tv.setText("Rs 0.0");
                    } //if
                    else { //else
                        totalTransactionAmount_tv.setTextColor(getResources().getColor(R.color.red));
                        totalTransactionAmount_tv.setText("Rs 0.0 (No Transaction)");
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
                userNameHeader_tv.setText(uid_tx+", "+name_tx);
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

//                expand profile
            case R.id.ad_expandTextView:
                if (hiddenProfile_rl.getVisibility() == View.GONE){
                    hiddenProfile_rl.setVisibility(View.VISIBLE);
                    expandTextView_btn.setBackground(getResources().getDrawable(R.drawable.ic_collapse));
                    YoYo.with(Techniques.FadeIn)
                            .duration(300)
                            .repeat(0)
                            .playOn(hiddenProfile_rl);
                }
                else if (hiddenProfile_rl.getVisibility() == View.VISIBLE){
                    expandTextView_btn.setBackground(getResources().getDrawable(R.drawable.ic_expand));
                    YoYo.with(Techniques.FadeOut)
                            .duration(300)
                            .repeat(0)
                            .playOn(hiddenProfile_rl);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hiddenProfile_rl.setVisibility(View.GONE);
                        }
                    },300);
                }
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
//                aangadia accounts
            case R.id.ad_transaction:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_aangadia_details, new AangadiaAccount()).addToBackStack("aangadiaAccount").commit();
                break;

        }

    }
//    onClick


    //end
}
