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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import util.android.textviews.FontTextView;

public class AangadiaHomePage extends AppCompatActivity implements View.OnClickListener{

    ImageButton logout_ib, addUser_ib, allUsers_btn, account_btn;
    TextView allUsersCount_tv, uid_tv;
    FontTextView aangadiaName_ftv;

    SharedPreferences userIdentifierSharedPreferences;

    private static final String USER_IDENTIFIER_PREF = "userIdentifierPref";
    private static final String AANGADIA_UID = "aangadia_uid";
    private static final String AANGADIA_NAME_FTV = "aangadia_name_ftv";

    private static final String LANDING_ACTIVITY = "landingActivity";
    private static final String FIRST_SCREEN = "firstScreen";
    private static final String AANGADIA_KEY = "aangadia_key";

    String aangadiaUID_tx;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aangadia_home_page);
        FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();

        logout_ib = findViewById(R.id.ahp_logoutButton);
        addUser_ib = findViewById(R.id.ahp_addUserButton);
        allUsers_btn = findViewById(R.id.ahp_allUsersButton);
        account_btn = findViewById(R.id.ahp_cashButton);
        allUsersCount_tv = findViewById(R.id.ahp_totalUsersTextView);
        aangadiaName_ftv = findViewById(R.id.ahp_aangadiaNameTextView);

        uid_tv = findViewById(R.id.ahp_userUIDTextView);

        addUser_ib.setOnClickListener(this);
        allUsers_btn.setOnClickListener(this);
        logout_ib.setOnClickListener(this);
        account_btn.setOnClickListener(this);

    }

    public void onStart(){
        super.onStart();

        userIdentifierSharedPreferences = getSharedPreferences(USER_IDENTIFIER_PREF, MODE_PRIVATE);
        aangadiaUID_tx = userIdentifierSharedPreferences.getString(AANGADIA_UID, "");
        uid_tv.setText("UID: "+aangadiaUID_tx);
        AllUsersCount();
        setUserName();
    }

    //    setting aangadia name at bottom
    private void setUserName() {
        String name = userIdentifierSharedPreferences.getString(AANGADIA_NAME_FTV, "");
        if (!TextUtils.isEmpty(name))  aangadiaName_ftv.setText("User Name: "+name);

        String key = userIdentifierSharedPreferences.getString(AANGADIA_KEY, "");
        databaseReference.child("AangadiaProfile").child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("userName").getValue(String.class);
                        aangadiaName_ftv.setText("User Name: "+name);
                        SharedPreferences.Editor editor = userIdentifierSharedPreferences.edit();
                        editor.putString(AANGADIA_NAME_FTV, name);
                        editor.apply();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

//    getting users count
    private void AllUsersCount() {
        DatabaseReference childReference = databaseReference.child("userProfile");
        Query query = childReference.orderByChild("aangadia_uid").equalTo(aangadiaUID_tx);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String user_count = String.valueOf((int) snapshot.getChildrenCount());
                allUsersCount_tv.setText(user_count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hiding the progress dialog.
            }
        });
    }

    //    onclick
    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {

            //            logout
            case R.id.ahp_logoutButton:
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

                                AangadiaHomePage.this.finish();
                                startActivity(new Intent(AangadiaHomePage.this, Login.class));

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
                break;

//                add user
            case R.id.ahp_addUserButton:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_aangadia_home_page, new AddUser()).addToBackStack("addUser").commit();
                break;
                //                all user
            case R.id.ahp_allUsersButton:
                startActivity(new Intent(AangadiaHomePage.this, ListOfUsersForAangadia.class));
                break;

            case R.id.ahp_cashButton:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_aangadia_home_page, new AangadiaAccount()).addToBackStack("aangadiaAccount").commit();
                break;

        }


    }//onclick
}
