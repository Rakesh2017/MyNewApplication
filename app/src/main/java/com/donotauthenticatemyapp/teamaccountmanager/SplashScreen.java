package com.donotauthenticatemyapp.teamaccountmanager;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import util.android.textviews.FontTextView;

public class SplashScreen extends AppCompatActivity {

    FontTextView textView;
    ImageView imageView;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(SplashScreen.this,R.color.black));

        textView = findViewById(R.id.splashText);
        imageView = findViewById(R.id.splashImage);

        YoYo.with(Techniques.Landing)
                .duration(1200)
                .repeat(0)
                .playOn(imageView);

        YoYo.with(Techniques.Landing)
                .duration(1500)
                .repeat(0)
                .playOn(textView);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SplashScreen.this.finish();
                startActivity(new Intent(SplashScreen.this, Login.class));
            }
        },2500);
    }
}
