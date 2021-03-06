package com.donotauthenticatemyapp.teamaccountmanager;


import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import mehdi.sakout.fancybuttons.FancyButton;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class AangadiaLogin extends Fragment implements View.OnClickListener{

    View view;
    RelativeLayout parentRelativeLayout;

    private FirebaseAuth mAuth;

    FancyButton submit_btn;
    String userName_tx, password_tx;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    EditText userName_et, password_et;
    protected static final String PLAY_EMAIL = "@play.org";
    ProgressDialog progressDialog;

    SharedPreferences sharedPreferences, userIdentifierSharedPreferences;
    private static final String LANDING_ACTIVITY = "landingActivity";
    private static final String FIRST_SCREEN = "firstScreen";
    private static final String AANGADIA_HOME_PAGE = "aangadiaHomePage";

    private static final String USER_IDENTIFIER_PREF = "userIdentifierPref";
    private static final String USER_IDENTITY = "userIdentity";
    private static final String AANGADIA_UID = "aangadia_uid";
    private static final String AANGADIA_KEY = "aangadia_key";
    private static final String PASSWORD_KEY = "password_key";
    private static final String FIRST_TIME = "first_time";


    public AangadiaLogin() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_aangadia_login, container, false);
        mAuth = FirebaseAuth.getInstance();

//        relative layout ids
        parentRelativeLayout = view.findViewById(R.id.lw_parentRelativeLayout);

//        landing page shared pref
        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(LANDING_ACTIVITY, MODE_PRIVATE);
        userIdentifierSharedPreferences = getActivity().getSharedPreferences(USER_IDENTIFIER_PREF, MODE_PRIVATE);

        progressDialog = new ProgressDialog(getContext());

        progressDialog.setMessage("Logging in...");

        userName_et = view.findViewById(R.id.lw_emailEditText);
        password_et = view.findViewById(R.id.lw_passwordEditText);

        submit_btn = view.findViewById(R.id.lw_submitButton);

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

//    onclick
    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.lw_submitButton) { //main if
            userName_tx = userName_et.getText().toString().trim();
            password_tx = password_et.getText().toString().trim();

            if (editTextValidations()){ //if 2
                progressDialog.show();
                new CheckNetworkConnection(getActivity(), new CheckNetworkConnection.OnConnectionCallback() {
                    @Override
                    public void onConnectionSuccess() {
                        SignIn();
                    }
                    @Override
                    public void onConnectionFail(String msg) {
                        NoInternetConnectionAlert noInternetConnectionAlert = new NoInternetConnectionAlert(getActivity());
                        noInternetConnectionAlert.DisplayNoInternetConnection();
                        progressDialog.dismiss();
                    }
                }).execute();

            }//if 2
        }//main if
    }//onclick


    //    sign in
    protected void SignIn(){
        progressDialog.show();
        userName_tx = userName_tx + PLAY_EMAIL;
        mAuth.signInWithEmailAndPassword(userName_tx, password_tx)
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (task.isSuccessful()) { //if 3
                            final FirebaseUser user = mAuth.getCurrentUser();
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child("AangadiaProfile").hasChild(user.getUid())){//if 4
                                        final String password_key = dataSnapshot.child("PasswordKey")
                                                .child(user.getUid()).child("key").getValue(String.class);

                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        SharedPreferences.Editor editor1 = userIdentifierSharedPreferences.edit();
                                        editor.putString(FIRST_SCREEN, AANGADIA_HOME_PAGE);
                                        editor1.putString(USER_IDENTITY, "aangadia");
                                        editor1.putString(AANGADIA_UID, userName_tx.substring(0,7));
                                        editor1.putString(AANGADIA_KEY, user.getUid());
                                        editor1.putString(PASSWORD_KEY, password_key);
                                        editor1.putInt(FIRST_TIME, 1);
                                        editor.apply();
                                        editor1.apply();
                                        getActivity().finish();

                                        startActivity(new Intent(getActivity(), AangadiaHomePage.class));
                                        progressDialog.dismiss();


                                    }
                                    else {
                                        new MaterialDialog.Builder(getActivity())
                                                .title("Failed")
                                                .titleColor(Color.WHITE)
                                                .content(userName_tx.substring(0,7)+" is not a Aangadia Account.")
                                                .icon(getResources().getDrawable(R.drawable.ic_warning))
                                                .contentColor(getResources().getColor(R.color.lightCoral))
                                                .backgroundColor(getResources().getColor(R.color.black90))
                                                .positiveText(R.string.ok)
                                                .show();
                                        mAuth.signOut();
                                        progressDialog.dismiss();
                                    }
                                } //if 4

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    new MaterialDialog.Builder(getActivity())
                                            .title("Failed")
                                            .titleColor(Color.WHITE)
                                            .content("Something went wrong. Try Again")
                                            .icon(getResources().getDrawable(R.drawable.ic_warning))
                                            .contentColor(getResources().getColor(R.color.lightCoral))
                                            .backgroundColor(getResources().getColor(R.color.black90))
                                            .positiveText(R.string.ok)
                                            .show();
                                    mAuth.signOut();
                                    progressDialog.dismiss();
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            new MaterialDialog.Builder(getActivity())
                                    .title("Failed")
                                    .titleColor(Color.WHITE)
                                    .content(Objects.requireNonNull(task.getException()).getLocalizedMessage())
                                    .icon(getResources().getDrawable(R.drawable.ic_warning))
                                    .contentColor(getResources().getColor(R.color.lightCoral))
                                    .backgroundColor(getResources().getColor(R.color.black90))
                                    .positiveText(R.string.ok)
                                    .show();
                            progressDialog.dismiss();

                        }//else
                    } //if 3
                });

    }
    //   sign in

    //    validations
    public boolean editTextValidations(){
        try {
            if(userName_tx.length() != 7){
                new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                        .title("Invalid UID")
                        .titleColor(Color.WHITE)
                        .content("UID is 7 in length")
                        .icon(getResources().getDrawable(R.drawable.ic_warning))
                        .contentColor(getResources().getColor(R.color.lightCoral))
                        .backgroundColor(getResources().getColor(R.color.black90))
                        .positiveText(R.string.ok)
                        .show();

                return false;
            }
            else if (TextUtils.isEmpty(password_tx)){
                new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
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
//           exception
        }
        return true;
    }// editTextValidations end



//end
}



















