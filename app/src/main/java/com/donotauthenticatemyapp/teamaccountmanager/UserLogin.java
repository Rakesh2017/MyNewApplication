package com.donotauthenticatemyapp.teamaccountmanager;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserLogin extends Fragment {

    View view;
    RelativeLayout parentRelativeLayout;

    public UserLogin() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_login, container, false);

//        relative layout ids
        parentRelativeLayout = view.findViewById(R.id.lu_parentRelativeLayout);



        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
//        function call
        LoadAnimations();
    }


    //  load  Animations
    private void LoadAnimations() {
        YoYo.with(Techniques.SlideInRight)
                .duration(500)
                .playOn(parentRelativeLayout);
    }
//end
}

