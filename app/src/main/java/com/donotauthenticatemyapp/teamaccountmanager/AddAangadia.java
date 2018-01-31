package com.donotauthenticatemyapp.teamaccountmanager;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mehdi.sakout.fancybuttons.FancyButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddAangadia extends Fragment implements View.OnClickListener{

    private View view;

    private RelativeLayout relativeLayout;
    private EditText userName_et, password_et, email_et, phone_et;
    private String userName_tx, password_tx, email_tx, phone_tx;
    private FancyButton button;

    private FirebaseAuth mAuth1;
    private FirebaseAuth mAuth2;

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private FirebaseApp myApp;

    DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    ProgressDialog progressDialog;

    public AddAangadia() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_aangadia, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        CreatingFirebaseAuthInstance();


        userName_et = view.findViewById(R.id.fad_userNameEditText);
        password_et = view.findViewById(R.id.fad_passwordEditText);
        email_et = view.findViewById(R.id.fad_emailEditText);
        phone_et = view.findViewById(R.id.fad_phoneEditText);

        relativeLayout = view.findViewById(R.id.fad_mainRelativeLayout);

        button = view.findViewById(R.id.fad_submitButton);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Creating Aangadia Account...");

        button.setOnClickListener(this);

        return view;
    }

    public void onStart(){
        super.onStart();
//        functions
        LoadAnimations();
    }

//    load animations
    private void LoadAnimations() {
        YoYo.with(Techniques.SlideInRight)
                .duration(500)
                .playOn(relativeLayout);

    }//load animations

    //    onclick
    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (id == R.id.fad_submitButton){  //main if
            email_tx = email_et.getText().toString().trim();
            password_tx = password_et.getText().toString().trim();
            userName_tx = userName_et.getText().toString().trim();
            phone_tx = phone_et.getText().toString().trim();

            if (EditTextValidations()){
                progressDialog.show();
                mAuth2.createUserWithEmailAndPassword(email_tx, password_tx)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    String message = task.getException().getLocalizedMessage();
                                    new MaterialDialog.Builder(getActivity())
                                            .title("Failed!")
                                            .titleColor(Color.WHITE)
                                            .content(message)
                                            .icon(getResources().getDrawable(R.drawable.ic_warning))
                                            .contentColor(getResources().getColor(R.color.lightCoral))
                                            .backgroundColor(getResources().getColor(R.color.black90))
                                            .positiveText(R.string.ok)
                                            .show();
                                    progressDialog.dismiss();
                                }
                                else
                                {
                                    String aangadiaUid = mAuth2.getCurrentUser().getUid();
                                    database.child("AangadiaProfile").child(aangadiaUid).child("userName").setValue(userName_tx);
                                    database.child("AangadiaProfile").child(aangadiaUid).child("email").setValue(email_tx);
                                    database.child("AangadiaProfile").child(aangadiaUid).child("password").setValue(password_tx);
                                    database.child("AangadiaProfile").child(aangadiaUid).child("phone").setValue(phone_tx);
                                    new MaterialDialog.Builder(getActivity())
                                            .title("Account Successfully Created")
                                            .titleColor(Color.WHITE)
                                            .content("Email: "+ email_tx
                                            +"\nPassword: "+password_tx
                                            +"\nuserName: "+userName_tx
                                            +"\nPhone: "+phone_tx)
                                            .icon(getResources().getDrawable(R.drawable.ic_success))
                                            .contentColor(getResources().getColor(R.color.lightCoral))
                                            .backgroundColor(getResources().getColor(R.color.black90))
                                            .positiveText(R.string.ok)
                                            .show();

                                    mAuth2.signOut();
                                    progressDialog.dismiss();
                                }

                            }
                        });

            } //main if ends





        }//if ends
    }//onclick ends

    public boolean EditTextValidations(){
            if(!validateEmail(email_tx)){
                new MaterialDialog.Builder(getActivity())
                        .title("Invalid E-mail")
                        .titleColor(Color.WHITE)
                        .content("Please enter correct e-mail.")
                        .icon(getResources().getDrawable(R.drawable.ic_warning))
                        .contentColor(getResources().getColor(R.color.lightCoral))
                        .backgroundColor(getResources().getColor(R.color.black90))
                        .positiveText(R.string.ok)
                        .show();
            return false;
        }
        else if (password_tx.length() < 8){
                new MaterialDialog.Builder(getActivity())
                        .title("Invalid Password")
                        .titleColor(Color.WHITE)
                        .content("Length of Password should be at least of length 8.")
                        .icon(getResources().getDrawable(R.drawable.ic_warning))
                        .contentColor(getResources().getColor(R.color.lightCoral))
                        .backgroundColor(getResources().getColor(R.color.black90))
                        .positiveText(R.string.ok)
                        .show();
                return false;
            }

            else if (userName_tx.isEmpty()){
                new MaterialDialog.Builder(getActivity())
                        .title("Invalid UserName")
                        .titleColor(Color.WHITE)
                        .content("UserName cannot be empty.")
                        .icon(getResources().getDrawable(R.drawable.ic_warning))
                        .contentColor(getResources().getColor(R.color.lightCoral))
                        .backgroundColor(getResources().getColor(R.color.black90))
                        .positiveText(R.string.ok)
                        .show();
                return false;
            }
            else if (!phone_tx.isEmpty()){
                if (phone_tx.length() != 10){
                    new MaterialDialog.Builder(getActivity())
                            .title("Invalid Phone Number")
                            .titleColor(Color.WHITE)
                            .content("Please re-check mobile number.")
                            .icon(getResources().getDrawable(R.drawable.ic_warning))
                            .contentColor(getResources().getColor(R.color.lightCoral))
                            .backgroundColor(getResources().getColor(R.color.black90))
                            .positiveText(R.string.ok)
                            .show();
                    return false;
                }
            }
            return true;
    }

    //    validate email
    public boolean validateEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void CreatingFirebaseAuthInstance(){
        mAuth1 = FirebaseAuth.getInstance();
        Toast.makeText(getActivity(), ""+mAuth1.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl("https://aangadiaaccountmanager.firebaseio.com/")
                .setApiKey("AIzaSyBa1bKRr6jall0WSAu0ZCkBbWkOnEXIiFQ")
                .setApplicationId("1:1065691751675:android:3699bf1db52b4943")
                .build();
        String uuid = UUID.randomUUID().toString();
        myApp = FirebaseApp.initializeApp(getActivity(),firebaseOptions,
                uuid);
        mAuth2 = FirebaseAuth.getInstance(myApp);
    }

    //end
}
