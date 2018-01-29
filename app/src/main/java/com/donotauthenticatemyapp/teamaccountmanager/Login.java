package com.donotauthenticatemyapp.teamaccountmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.mikhaellopez.circularimageview.CircularImageView;

import util.android.textviews.FontTextView;

public class Login extends AppCompatActivity implements View.OnClickListener{

    CircularImageView admin_ib, worker_ib, user_ib;
    FontTextView admin_tv, worker_tv, user_tv;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//      circular images button
        admin_ib = findViewById(R.id.login_adminImage);
        worker_ib = findViewById(R.id.adh_addAangadiaImage);
        user_ib = findViewById(R.id.adh_allAngadiaImage);

//        text view is
        admin_tv = findViewById(R.id.login_adminTextView);
        worker_tv = findViewById(R.id.login_angadiaTextView);
        user_tv = findViewById(R.id.login_userTextView);
        admin_tv.setVisibility(View.INVISIBLE);
        worker_tv.setVisibility(View.INVISIBLE);
        user_tv.setVisibility(View.INVISIBLE);

//        frame layout
        frameLayout = findViewById(R.id.login_fragment_container);


//        on click calls
        admin_ib.setOnClickListener(this);
        worker_ib.setOnClickListener(this);
        user_ib.setOnClickListener(this);

    }




    //    onStart
    public void onStart(){
        super.onStart();
        //        function call
        ButtonAnimations();
        checkLandingPage();

    }

//    onBAckPressed
    public void onBackPressed(){
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        int s = fragmentManager.getBackStackEntryCount() - 1;
        if (s >= 0){
            super.onBackPressed();
        }
        else {
            super.onBackPressed();
            moveTaskToBack(true);
            this.finish();
        }
    }//onBackPressed ends

//    Loading Animations
    public void ButtonAnimations(){
        YoYo.with(Techniques.ZoomIn)
                .duration(1500)
                .playOn(admin_ib);
        YoYo.with(Techniques.ZoomIn)
                .duration(1500)
                .playOn(worker_ib);
        YoYo.with(Techniques.ZoomIn)
                .duration(1500)
                .playOn(user_ib);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                admin_tv.setVisibility(View.VISIBLE);
                worker_tv.setVisibility(View.VISIBLE);
                user_tv.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeIn)
                        .duration(300)
                        .playOn(admin_tv);
                YoYo.with(Techniques.FadeIn)
                        .duration(300)
                        .playOn(worker_tv);
                YoYo.with(Techniques.FadeIn)
                        .duration(300)
                        .playOn(user_tv);

            }
        },1500);

    }

//  onclick
    @Override
    public void onClick(View view) {

        int id = view.getId();
//        admin
        if (id == R.id.login_adminImage){
            getSupportFragmentManager().beginTransaction().replace(R.id.login_fragment_container, new AdminLogin()).addToBackStack("loginAdmin").commit();
        }
//        worker
        else if (id == R.id.adh_addAangadiaImage){
            getSupportFragmentManager().beginTransaction().replace(R.id.login_fragment_container, new WorkerLogin()).addToBackStack("loginWorker").commit();
        }
//        user
        else if (id == R.id.adh_allAngadiaImage){
            getSupportFragmentManager().beginTransaction().replace(R.id.login_fragment_container, new UserLogin()).addToBackStack("loginUser").commit();
        }
    }

    // landing page
    public void checkLandingPage() {

        SharedPreferences sharedpreferences = getSharedPreferences("LogDetail", MODE_PRIVATE);
        String decider = sharedpreferences.getString("firstScreen", "");
        if (TextUtils.equals(decider, "HomePage")) {
            startActivity(new Intent(Login.this, AdminHomePage.class));
            Login.this.finish();
        }
    }

//    end
}
