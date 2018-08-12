package com.donotauthenticatemyapp.teamaccountmanager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import mehdi.sakout.fancybuttons.FancyButton;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class AdminHomePage extends AppCompatActivity implements View.OnClickListener{

    ImageButton addAangadia_btn, addUser_btn,logout_ib, allAangadias_btn, allUsers_btn, adminBalance_btn, refresh_ib;

    private static final String LANDING_ACTIVITY = "landingActivity";
    private static final String FIRST_SCREEN = "firstScreen";

    TextView totalAangadia_tv, totalUsers_tv, commission_tv, currentCommission_tv, adminBalance_tv, totalAangadiaTransactions_tv;
    String commission_tx;

    int totalAangadia, totalUsers;
    FancyButton submitCommission_btn;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    GifImageView loadingGIf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home_page);

        addAangadia_btn = findViewById(R.id.adh_addAangadiaButton);
        addUser_btn = findViewById(R.id.adh_addUserButton);
        allAangadias_btn = findViewById(R.id.adh_allAangadiaButton);
        allUsers_btn = findViewById(R.id.adh_allUsersButton);
        submitCommission_btn = findViewById(R.id.adh_submitCommissionBtn);
        adminBalance_btn = findViewById(R.id.adh_adminAccountButton);

        loadingGIf = findViewById(R.id.aap_loadingGif);

        logout_ib = findViewById(R.id.adh_logoutButton);
        refresh_ib = findViewById(R.id.adh_refreshImageButton);

        totalAangadia_tv = findViewById(R.id.adh_totalAangadiasTextView);
        totalUsers_tv = findViewById(R.id.adh_totalUsersTextView);
        commission_tv = findViewById(R.id.adh_commissionEditText);
        currentCommission_tv = findViewById(R.id.adh_currentCommissionTextView);
        adminBalance_tv = findViewById(R.id.adh_adminAccountTextView);
        totalAangadiaTransactions_tv = findViewById(R.id.adh_totalTransactionsTextView);

        progressDialog = new ProgressDialog(AdminHomePage.this);
        progressDialog.setMessage("Please wait...");

        addAangadia_btn.setOnClickListener(this);
        addUser_btn.setOnClickListener(this);
        allAangadias_btn.setOnClickListener(this);
        allUsers_btn.setOnClickListener(this);
        logout_ib.setOnClickListener(this);
        refresh_ib.setOnClickListener(this);
        submitCommission_btn.setOnClickListener(this);
        adminBalance_btn.setOnClickListener(this);

    }

    public void onStart(){
        super.onStart();

        loadingGIf.setVisibility(View.VISIBLE);
        new CheckNetworkConnection(AdminHomePage.this, new CheckNetworkConnection.OnConnectionCallback() {
            @Override
            public void onConnectionSuccess() {
                setAdminBalance();
                TotalAangadias();
                TotalUsers();
                SetCommission();
                setTotalAangadiaTransactions();
              //  loadingGIf.setVisibility(View.GONE);
            }
            @Override
            public void onConnectionFail(String msg) {
                loadingGIf.setVisibility(View.GONE);
                try {
                    new MaterialDialog.Builder(AdminHomePage.this)
                            .title("No Internet Access!")
                            .titleColor(Color.BLACK)
                            .content("No internet connectivity detected. Please make sure you have working internet connection and try again.")
                            .icon(getResources().getDrawable(R.drawable.ic_no_internet_connection))
                            .contentColor(getResources().getColor(R.color.black))
                            .backgroundColor(getResources().getColor(R.color.white))
                            .positiveColor(getResources().getColor(R.color.green))
                            .negativeText("Cancel")
                            .negativeColor(getResources().getColor(R.color.red))
                            .positiveText("Try Again!")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    onStart();
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .cancelable(false)
                            .show();
                }
                catch (Exception e){
                    loadingGIf.setVisibility(View.GONE);
                    e.printStackTrace();
                }

            }
        }).execute();

    }

    private void setTotalAangadiaTransactions() {
        databaseReference.child("aangadiaCashInAccount")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long sum = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                sum = sum + Long.parseLong(snapshot1.child("money_added").getValue(String.class));
                            }
                        }
                            if (!TextUtils.isEmpty(String.valueOf(sum))){
                                NumberFormat formatter = new DecimalFormat("#,###");
                                String formatted_balance = formatter.format(sum);
                                totalAangadiaTransactions_tv.setText("Rs "+formatted_balance+"/-");
                            }
                            else totalAangadiaTransactions_tv.setText("Rs 0.0");
                            loadingGIf.setVisibility(View.GONE);   // finally stop rotating gif when all values are loaded
                        }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        try {
                            loadingGIf.setVisibility(View.GONE);
                        }
                        catch (Exception e){
                            // do nothing
                        }

                    }
                });
    }

    //setting admin balance
    private void setAdminBalance() {
        databaseReference.child("adminBalance")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String balance = dataSnapshot.child("total_balance").getValue(String.class);
                        adminBalance_tv.setText("Rs "+balance+"/-");
                        if (TextUtils.isEmpty(balance)){
                            adminBalance_tv.setText("Rs "+0.0+"/-");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    //    setting commission
    private void SetCommission() {
        databaseReference.child("adminCommission")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String commission = dataSnapshot.child("commission").getValue(String.class);
                        currentCommission_tv.setText(commission+"%");
                        if (TextUtils.isEmpty(commission)){
                            currentCommission_tv.setText(" NA");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
//    setting commission

    //    total users
    private void TotalUsers() {
        databaseReference.child("userProfile").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        totalUsers = (int) dataSnapshot.getChildrenCount();
                        totalUsers_tv.setText(String.valueOf(totalUsers));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                       // Toast.makeText(AdminHomePage.this, ""+databaseError.getDetails(), Toast.LENGTH_LONG).show();
                    }
                });
    }
//    total users

    //    total aangadia
    private void TotalAangadias() {
        databaseReference.child("AangadiaProfile").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        totalAangadia = (int) dataSnapshot.getChildrenCount();
                        totalAangadia_tv.setText(String.valueOf(totalAangadia));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                      //  Toast.makeText(AdminHomePage.this, ""+databaseError.getDetails(), Toast.LENGTH_LONG).show();
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
// add user
            case R.id.adh_addUserButton:
                getSupportFragmentManager().beginTransaction().replace(R.id.adh_fragment_container, new AddUser()).addToBackStack("addUser").commit();
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
                                finishAffinity();
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

//                list of all aangadias
            case R.id.adh_allAangadiaButton:
                startActivity(new Intent(AdminHomePage.this, ListOfAangadias.class));
                break;

            //                list of all users
            case R.id.adh_allUsersButton:
                startActivity(new Intent(AdminHomePage.this, ListOfUsersForAdmin.class));
                break;
//                commission
            case R.id.adh_submitCommissionBtn:
                SubmitCommission();
                break;
//                admin balance
            case R.id.adh_adminAccountButton:
                getSupportFragmentManager().beginTransaction().replace(R.id.adh_fragment_container, new AdminBalance()).addToBackStack("adminBalance").commit();
                break;

//                refresh button
            case R.id.adh_refreshImageButton:
                try{
                    onStart();
                }
                catch (IllegalStateException e){
//                    exception
                }
                break;

        }//switch ends

    }//onclick

//    submitting commission
    private void SubmitCommission() {
        commission_tx = commission_tv.getText().toString().trim();
        if (CommissionValidation()){ //if
            progressDialog.show();
            new CheckNetworkConnection(AdminHomePage.this, new CheckNetworkConnection.OnConnectionCallback() {
                @Override
                public void onConnectionSuccess() {
                    FinallySetCommission();
                }
                @Override
                public void onConnectionFail(String msg) {
                    NoInternetConnectionAlert noInternetConnectionAlert = new NoInternetConnectionAlert(AdminHomePage.this);
                    noInternetConnectionAlert.DisplayNoInternetConnection();
                    progressDialog.dismiss();
                }
            }).execute();
        }//if
    }
    //    submitting commission

    public void FinallySetCommission(){
        databaseReference.child("adminCommission").child("commission").setValue(commission_tx)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(Void aVoid) {
                        new MaterialDialog.Builder(AdminHomePage.this)
                                .title("Success")
                                .titleColor(Color.BLACK)
                                .content("Commission set to "+commission_tx +"%")
                                .icon(getResources().getDrawable(R.drawable.ic_success))
                                .contentColor(getResources().getColor(R.color.black))
                                .backgroundColor(getResources().getColor(R.color.white))
                                .positiveText(R.string.ok)
                                .show();
                        currentCommission_tv.setText(commission_tx+"%");
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new MaterialDialog.Builder(AdminHomePage.this)
                        .title("Failed")
                        .titleColor(Color.BLACK)
                        .content(e.getLocalizedMessage())
                        .icon(getResources().getDrawable(R.drawable.ic_success))
                        .contentColor(getResources().getColor(R.color.green))
                        .backgroundColor(getResources().getColor(R.color.white))
                        .positiveText(R.string.ok)
                        .show();
                progressDialog.dismiss();
            }
        });
    }

    public Boolean CommissionValidation(){
        if (TextUtils.isEmpty(commission_tx)){
            new MaterialDialog.Builder(AdminHomePage.this)
                    .title("Empty!")
                    .titleColor(Color.WHITE)
                    .content("Please enter commission")
                    .icon(getResources().getDrawable(R.drawable.ic_warning))
                    .contentColor(getResources().getColor(R.color.lightCoral))
                    .backgroundColor(getResources().getColor(R.color.black90))
                    .positiveText(R.string.ok)
                    .show();
            return false;
        }
        try {
              if (Integer.parseInt(commission_tx) > 100 ||Integer.parseInt(commission_tx) < 0 ){
                new MaterialDialog.Builder(AdminHomePage.this)
                        .title("InValid Commission!")
                        .titleColor(Color.WHITE)
                        .content("Commission can neither be less than 0 or more than 100")
                        .icon(getResources().getDrawable(R.drawable.ic_warning))
                        .contentColor(getResources().getColor(R.color.lightCoral))
                        .backgroundColor(getResources().getColor(R.color.black90))
                        .positiveText(R.string.ok)
                        .show();
                return false;
            }
        }
        catch (NumberFormatException e){
            e.printStackTrace();
        }

        return true;
    }

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

    public void onDestroy(){
        super.onDestroy();
        if (progressDialog.isShowing()) progressDialog.dismiss();
    }

//    end
}
