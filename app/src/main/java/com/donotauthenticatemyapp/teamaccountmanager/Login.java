package com.donotauthenticatemyapp.teamaccountmanager;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.mikhaellopez.circularimageview.CircularImageView;

import util.android.textviews.FontTextView;

public class Login extends AppCompatActivity implements View.OnClickListener{

    CircularImageView admin_ib, worker_ib, user_ib;
    FontTextView admin_tv, worker_tv, user_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//      circular images button
        admin_ib = findViewById(R.id.login_adminImage);
        worker_ib = findViewById(R.id.login_workerImage);
        user_ib = findViewById(R.id.login_userImage);

//        text view is
        admin_tv = findViewById(R.id.login_adminTextView);
        worker_tv = findViewById(R.id.login_angadiaTextView);
        user_tv = findViewById(R.id.login_userTextView);
        admin_tv.setVisibility(View.INVISIBLE);
        worker_tv.setVisibility(View.INVISIBLE);
        user_tv.setVisibility(View.INVISIBLE);


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

    }

//    Loading Animations
    public void ButtonAnimations(){
        YoYo.with(Techniques.ZoomIn)
                .duration(3000)
                .playOn(admin_ib);

        YoYo.with(Techniques.ZoomIn)
                .duration(3000)
                .playOn(worker_ib);

        YoYo.with(Techniques.ZoomIn)
                .duration(3000)
                .playOn(user_ib);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                admin_tv.setVisibility(View.VISIBLE);
                worker_tv.setVisibility(View.VISIBLE);
                user_tv.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeIn)
                        .duration(600)
                        .playOn(admin_tv);
                YoYo.with(Techniques.FadeIn)
                        .duration(600)
                        .playOn(worker_tv);
                YoYo.with(Techniques.FadeIn)
                        .duration(600)
                        .playOn(user_tv);

            }
        },2400);

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
        else if (id == R.id.login_workerImage){
            getSupportFragmentManager().beginTransaction().replace(R.id.login_fragment_container, new WorkerLogin()).addToBackStack("loginWorker").commit();
        }
//        user
        else if (id == R.id.login_userImage){
            getSupportFragmentManager().beginTransaction().replace(R.id.login_fragment_container, new UserLogin()).addToBackStack("loginUser").commit();
        }
    }

//    end
}