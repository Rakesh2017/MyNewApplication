package com.donotauthenticatemyapp.teamaccountmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AangadiaHomePage extends AppCompatActivity implements View.OnClickListener{

    ImageButton logout_ib;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aangadia_home_page);
        FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
        Toast.makeText(AangadiaHomePage.this, ""+mAuth.getEmail(), Toast.LENGTH_SHORT).show();
        logout_ib = findViewById(R.id.ahg_logoutButton);


        logout_ib.setOnClickListener(this);

    }

//    onclick
    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {

            //            logout
            case R.id.ahg_logoutButton:
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
                                SharedPreferences sharedpreferences = getSharedPreferences("LogDetail", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("firstScreen", "LoginPage");
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

        }


    }//onclick
}
