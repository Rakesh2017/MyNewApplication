package com.donotauthenticatemyapp.teamaccountmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminHomePage extends AppCompatActivity implements View.OnClickListener{

    ImageButton addAangadia_btn, logout_ib;

    private static final String LANDING_ACTIVITY = "landingActivity";
    private static final String FIRST_SCREEN = "firstScreen";

    TextView totalAangadia_tv;

    int totalAangadia;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home_page);

        addAangadia_btn = findViewById(R.id.adh_addAangadiaButton);

        logout_ib = findViewById(R.id.adh_logoutButton);

        totalAangadia_tv = findViewById(R.id.adh_totalAangadiasTextView);

        addAangadia_btn.setOnClickListener(this);
        logout_ib.setOnClickListener(this);

    }

    public void onStart(){
        super.onStart();
        TotalAangadias();
    }

    //    total aangadia
    private void TotalAangadias() {
        databaseReference.child("AangadiaProfile").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        totalAangadia = (int) dataSnapshot.getChildrenCount();
                        totalAangadia_tv.setText(null);
                        totalAangadia_tv.setText("Total Aangadias: "+ String.valueOf(totalAangadia));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Toast.makeText(AdminHomePage.this, ""+databaseError.getDetails(), Toast.LENGTH_LONG).show();
                    }
                });
    }
//    total aangadia



    //    onclick
    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){
//            add aangadia
            case R.id.adh_addAangadiaButton:
                getSupportFragmentManager().beginTransaction().replace(R.id.adh_fragment_container, new AddAangadia()).addToBackStack("addAangadia").commit();
                break;
//            logout
            case R.id.adh_logoutButton:
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

                                AdminHomePage.this.finish();
                                startActivity(new Intent(AdminHomePage.this, Login.class));

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

        }//switch ends

    }//onclick

//    onBackPressed
    public void onBackPressed(){
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        int s =fragmentManager.getBackStackEntryCount() - 1;
        if (s >= 0){
            super.onBackPressed();

        }
        else {
            super.onBackPressed();
            moveTaskToBack(true);
            finish();
        }
    }//onBackPressed ends

//    end
}
