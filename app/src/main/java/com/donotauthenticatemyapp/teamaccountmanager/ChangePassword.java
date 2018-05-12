package com.donotauthenticatemyapp.teamaccountmanager;


import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import mehdi.sakout.fancybuttons.FancyButton;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePassword extends Fragment {

    EditText password;
    String password_tx, oldPassword_tx, uid_tx, name_tx, path_tx, currentPassword_tx;
    TextView uid_header, userName, currentPassword_tv;

    RelativeLayout relativeLayout;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    FancyButton submit;

    protected FirebaseAuth mAuth1;
    protected FirebaseAuth mAuth2;

    protected FirebaseApp myApp;

    private static final String UPDATE_PREF = "change_password_pref";
    private static final String OLD_PASSWORD = "old_password";
    private static final String UID = "uid";
    private static final String USER_NAME = "userName";
    private static final String PLAY_EMAIL = "@play.org";
    private static final String PATH = "path";

    ProgressDialog progressDialog;

    SharedPreferences passwordSharedPreferences;

    public ChangePassword() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        CreatingFirebaseAuthInstance();

        uid_header = view.findViewById(R.id.cp_uidTextViewHeader);
        userName = view.findViewById(R.id.cp_nameViewHeader);
        relativeLayout = view.findViewById(R.id.cp_relativeLayout);

        currentPassword_tv = view.findViewById(R.id.cp_currentPasswordTextView);

        passwordSharedPreferences = getActivity().getSharedPreferences(UPDATE_PREF, MODE_PRIVATE);
        oldPassword_tx = passwordSharedPreferences.getString(OLD_PASSWORD, "");
        uid_tx = passwordSharedPreferences.getString(UID, "");
        name_tx = passwordSharedPreferences.getString(USER_NAME, "");
        path_tx = passwordSharedPreferences.getString(PATH, "");
        currentPassword_tx = passwordSharedPreferences.getString(OLD_PASSWORD, "");

        uid_header.setText(uid_tx);
        userName.setText(name_tx);
        currentPassword_tv.setText(currentPassword_tx);

        progressDialog = new ProgressDialog(getContext());
        // Setting up message in Progress dialog.
        progressDialog.setMessage("changing password...");

        password = view.findViewById(R.id.cp_passwordEditText);

        submit = view.findViewById(R.id.cp_submitButton);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password_tx = password.getText().toString().trim();
                if(EditTextValidations()){
                    new CheckNetworkConnection(getActivity(), new CheckNetworkConnection.OnConnectionCallback() {
                        @Override
                        public void onConnectionSuccess() {
                            updatePassword(); // update password
                        }
                        @Override
                        public void onConnectionFail(String msg) {
                            NoInternetConnectionAlert noInternetConnectionAlert = new NoInternetConnectionAlert(getActivity());
                            noInternetConnectionAlert.DisplayNoInternetConnection();
                            progressDialog.dismiss();
                        }
                    }).execute();

                }
            }
        });

        return view;
    }

    public void onStart(){
        super.onStart();

        LoadAnimation();
    }

    private void LoadAnimation() {
        YoYo.with(Techniques.SlideInRight)
                .duration(300)
                .repeat(0)
                .playOn(relativeLayout);
    }


    //    creating second auth instance
    public void CreatingFirebaseAuthInstance(){
        mAuth1 = FirebaseAuth.getInstance();
      //  Toast.makeText(getActivity(), ""+mAuth1.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
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

//    update password
    public void updatePassword(){

        progressDialog.show();
        Log.w("raky", uid_tx+" "+oldPassword_tx);
        uid_tx = uid_tx.concat(PLAY_EMAIL);
        mAuth2.signInWithEmailAndPassword(uid_tx, oldPassword_tx)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            new MaterialDialog.Builder(getActivity())
                                    .title("Failed.")
                                    .titleColor(Color.WHITE)
                                    .content(task.getException().getLocalizedMessage())
                                    .icon(getResources().getDrawable(R.drawable.ic_warning))
                                    .contentColor(getResources().getColor(R.color.lightCoral))
                                    .backgroundColor(getResources().getColor(R.color.black90))
                                    .positiveText(R.string.ok)
                                    .show();
                        }
                        else {
                          //  FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            mAuth2.getCurrentUser().updatePassword(password_tx)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                final String password_key = getPasswordKey();

                                                Log.d("raky", "User password updated.");
                                                String key = mAuth2.getCurrentUser().getUid();
                                                databaseReference.child(path_tx).child(key)
                                                        .child("password").setValue(password_tx);
                                                databaseReference.child("PasswordKey").child(key)
                                                        .child("key").setValue(password_key);
                                                ChangeValuesOnUpdation();
                                                PasswordUpdateSuccessful();
                                                progressDialog.dismiss();
                                            }
                                            else {
                                                try {
                                                    new MaterialDialog.Builder(getActivity())
                                                            .title("Failed.")
                                                            .titleColor(Color.WHITE)
                                                            .content(task.getException().getLocalizedMessage())
                                                            .icon(getResources().getDrawable(R.drawable.ic_warning))
                                                            .contentColor(getResources().getColor(R.color.lightCoral))
                                                            .backgroundColor(getResources().getColor(R.color.black90))
                                                            .positiveText(R.string.ok)
                                                            .show();
                                                    progressDialog.dismiss();
                                                }
                                                catch (Exception e){
//                                                    exception
                                                }


                                            }
                                        }
                                    });
                        } // else ends

                        mAuth2.signOut();
                      //  progressDialog.dismiss();
                    }
                });
    }
//    update password

//    password updated
    public void PasswordUpdateSuccessful() {
        try {
            new MaterialDialog.Builder(getActivity())
                    .title("Password Updated")
                    .titleColor(Color.WHITE)
                    .content("New Password: " + password_tx)
                    .icon(getResources().getDrawable(R.drawable.ic_success))
                    .contentColor(getResources().getColor(R.color.lightCoral))
                    .backgroundColor(getResources().getColor(R.color.black90))
                    .positiveText(R.string.ok)
                    .show();
        }
        catch (Exception e){

        }

    }


//validations
    public boolean EditTextValidations(){
        if (password_tx.length() < 8){
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
        else if (TextUtils.equals(password_tx, oldPassword_tx)){
            new MaterialDialog.Builder(getActivity())
                    .title("Cannot Proceed...")
                    .titleColor(Color.WHITE)
                    .content("New Password cannot be same as old password. In that case no need to change password.")
                    .icon(getResources().getDrawable(R.drawable.ic_warning))
                    .contentColor(getResources().getColor(R.color.lightCoral))
                    .backgroundColor(getResources().getColor(R.color.black90))
                    .positiveText(R.string.ok)
                    .show();
            return false;
        }
        return true;
    }

    //    changing values on updation
    public void ChangeValuesOnUpdation(){
        currentPassword_tv.setText(password_tx);
    }


    //    generate 4 digit password check key
    public String getPasswordKey() {
        return UUID.randomUUID().toString().substring(0,4);
    }

//ends
}



