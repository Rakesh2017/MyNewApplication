package com.donotauthenticatemyapp.teamaccountmanager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import mehdi.sakout.fancybuttons.FancyButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddAangadia extends Fragment implements View.OnClickListener{

    private View view;

    private EditText userName_et, password_et, email_et;
    private String userName_tx, password_tx, email_tx;
    private FancyButton button;

    public AddAangadia() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_aangadia, container, false);

        userName_et = view.findViewById(R.id.fad_userNameEditText);
        password_et = view.findViewById(R.id.fad_passwordEditText);
        email_et = view.findViewById(R.id.fad_emailEditText);

        button = view.findViewById(R.id.fad_submitButton);

        button.setOnClickListener(this);

        return view;
    }

//    onclick
    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (id == R.id.fad_submitButton){

        }//if ends
    }//onclick ends

    //end
}
