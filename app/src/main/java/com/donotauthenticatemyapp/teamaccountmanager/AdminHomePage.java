package com.donotauthenticatemyapp.teamaccountmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class AdminHomePage extends AppCompatActivity implements View.OnClickListener{

    ImageButton addAangadia_btn, allAangadia_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home_page);

        addAangadia_btn = findViewById(R.id.adh_addAangadiaImage);
        allAangadia_btn = findViewById(R.id.adh_allAngadiaImage);


        addAangadia_btn.setOnClickListener(this);
        allAangadia_btn.setOnClickListener(this);

    }

//    onclick
    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){
            case R.id.adh_addAangadiaImage:
                getSupportFragmentManager().beginTransaction().add(R.id.adh_fragment_container, new AddAangadia()).addToBackStack("addAangadia").commit();

        }

    }
}
