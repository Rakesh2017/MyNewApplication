package com.donotauthenticatemyapp.teamaccountmanager;


import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mehdi.sakout.fancybuttons.FancyButton;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdminLogin extends Fragment implements View.OnClickListener {

    View view;
    RelativeLayout parentRelativeLayout;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    EditText userName_et, password_et;
    String userName_tx, password_tx;
    FancyButton submit_btn;
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    ProgressDialog progressDialog;

    public AdminLogin() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_admin_login, container, false);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getContext());

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Logging in...");

//        edit text
        userName_et = view.findViewById(R.id.la_emailEditText);
        password_et = view.findViewById(R.id.la_passwordEditText);

        submit_btn = view.findViewById(R.id.la_btn);


//        relative layout ids
        parentRelativeLayout = view.findViewById(R.id.la_parentRelativeLayout);

        submit_btn.setOnClickListener(this);

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

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (id == R.id.la_btn){ //if 1
            userName_tx = userName_et.getText().toString().trim();
            password_tx = password_et.getText().toString().trim();
            if (editTextValidations()){ //if 2

                progressDialog.show();
//                Signing in
                mAuth.signInWithEmailAndPassword(userName_tx, password_tx)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    progressDialog.dismiss();

                                    new MaterialDialog.Builder(getActivity())
                                        .title(R.string.masterKey)
                                        .content("You have successfully Logged in. Now You need to enter master key to use owner account")
                                        .contentColorRes(R.color.appColor)
                                        .titleColor(getResources().getColor(R.color.white))
                                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                                        .positiveText("Confirm")
                                        .positiveColorRes(R.color.googleGreen)
                                        .negativeText("Cancel")
                                        .negativeColorRes(R.color.googleRed)
                                        .backgroundColor(getResources().getColor(R.color.black90))
                                        .icon(getResources().getDrawable(R.drawable.ic_password))
                                        .inputRange(8, 8, getResources().getColor(R.color.lightCoral))
                                        .input("8 digit master key", "", new MaterialDialog.InputCallback() {
                                            @Override
                                            public void onInput(MaterialDialog dialog, final CharSequence input) {
                                                progressDialog.show();
//                                                matching master key
                                                databaseReference
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                String masterKey = dataSnapshot.child("security").child("masterKey").getValue(String.class);
                                                                if (TextUtils.equals(masterKey, input)){
//check whether is really admin email
                                                                    String isAdmin = dataSnapshot.child("adminProfile").child("email").getValue(String.class);
                                                                    if (TextUtils.equals(isAdmin, userName_tx)){
                                                                        startActivity(new Intent(getActivity(), AdminHomePage.class));
                                                                        progressDialog.dismiss();
//                                                                    shared preference
                                                                        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("LogDetail", MODE_PRIVATE);
                                                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                                                        editor.putString("firstScreen", "AdminHomePage");
                                                                        editor.apply();
                                                                        getActivity().finish();
                                                                    }
                                                                    else {
                                                                        new MaterialDialog.Builder(getActivity())
                                                                                .title("Suspicious Activity!")
                                                                                .titleColor(Color.WHITE)
                                                                                .content("This is not a Admin UserName.")
                                                                                .icon(getResources().getDrawable(R.drawable.ic_warning))
                                                                                .contentColor(getResources().getColor(R.color.lightCoral))
                                                                                .backgroundColor(getResources().getColor(R.color.black90))
                                                                                .positiveText(R.string.ok)
                                                                                .show();
                                                                        progressDialog.dismiss();

                                                                    } // admin email check ends


                                                                }
                                                                else {
                                                                    new MaterialDialog.Builder(getActivity())
                                                                            .title("Incorrect Master Key")
                                                                            .titleColor(Color.WHITE)
                                                                            .content("Either your entered master key is incorrect or it can be old one.")
                                                                            .icon(getResources().getDrawable(R.drawable.ic_warning))
                                                                            .contentColor(getResources().getColor(R.color.lightCoral))
                                                                            .backgroundColor(getResources().getColor(R.color.black90))
                                                                            .positiveText(R.string.ok)
                                                                            .show();
                                                                    progressDialog.dismiss();

                                                                }//master key check ends
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {
                                                                progressDialog.dismiss();

                                                            }
                                                        });

                                            }
                                        }).show();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    try {
                                        new MaterialDialog.Builder(getActivity())
                                                .title("Authentication Failed!")
                                                .titleColor(Color.BLACK)
                                                .content("Either UserName or Password is incorrect")
                                                .icon(getResources().getDrawable(R.drawable.ic_warning))
                                                .contentColor(getResources().getColor(R.color.lightCoral))
                                                .backgroundColor(getResources().getColor(R.color.white))
                                                .positiveText(R.string.ok)
                                                .show();
                                        progressDialog.dismiss();

                                    }
                                    catch (NullPointerException | ActivityNotFoundException e){
                                        e.printStackTrace();
                                        progressDialog.dismiss();

                                    }
                                }//try

                            }
                        });//sign in
            }// if 2
        }//if 1

    }// onclick ends

    public boolean editTextValidations(){
        try {
            if(!validateEmail(userName_tx)){
                new MaterialDialog.Builder(getActivity())
                        .title("Invalid Username")
                        .titleColor(Color.WHITE)
                        .content("Please re-enter your username with correct format.")
                        .icon(getResources().getDrawable(R.drawable.ic_warning))
                        .contentColor(getResources().getColor(R.color.lightCoral))
                        .backgroundColor(getResources().getColor(R.color.black90))
                        .positiveText(R.string.ok)
                        .show();

                return false;
            }
            else if (TextUtils.isEmpty(password_tx)){
                new MaterialDialog.Builder(getActivity())
                        .title("Invalid Password")
                        .titleColor(Color.WHITE)
                        .content("Password can't be empty. Please enter your password.")
                        .icon(getResources().getDrawable(R.drawable.ic_warning))
                        .contentColor(getResources().getColor(R.color.lightCoral))
                        .backgroundColor(getResources().getColor(R.color.black90))
                        .positiveText(R.string.ok)
                        .show();
                return false;
            }
            return true;
        }
        catch (NullPointerException | ActivityNotFoundException e){
            e.printStackTrace();
        }
        return true;
    }// editTextValidations end

    //    validate email
    public boolean validateEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

//    ends
}
