package com.donotauthenticatemyapp.teamaccountmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AangadiaHomePage extends AppCompatActivity implements View.OnClickListener{

    ImageButton logout_ib, addUser_ib, allUsers_btn;


    private static final String LANDING_ACTIVITY = "landingActivity";
    private static final String FIRST_SCREEN = "firstScreen";

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aangadia_home_page);
        FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
       // Toast.makeText(AangadiaHomePage.this, ""+mAuth.getEmail(), Toast.LENGTH_SHORT).show();
        logout_ib = findViewById(R.id.ahp_logoutButton);
        addUser_ib = findViewById(R.id.ahp_addUserButton);
        allUsers_btn = findViewById(R.id.ahp_allUsersButton);

        addUser_ib.setOnClickListener(this);
        allUsers_btn.setOnClickListener(this);
        logout_ib.setOnClickListener(this);

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

        }


    }//onclick
}
