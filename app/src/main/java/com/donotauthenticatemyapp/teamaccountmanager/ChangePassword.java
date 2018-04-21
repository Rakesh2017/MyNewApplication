package com.donotauthenticatemyapp.teamaccountmanager;


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
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    String password_tx, oldPassword_tx, uid_tx, name_tx;
    TextView uid_header, userName;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    FancyButton submit;

    protected FirebaseAuth mAuth1;
    protected FirebaseAuth mAuth2;

    protected FirebaseApp myApp;

    private static final String CHANGE_PASSWORD_PREF = "change_password_pref";
    private static final String OLD_PASSWORD = "old_password";
    private static final String UID = "uid";
    private static final String USER_NAME = "userName";

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

        passwordSharedPreferences = getActivity().getSharedPreferences(CHANGE_PASSWORD_PREF, MODE_PRIVATE);
        oldPassword_tx = passwordSharedPreferences.getString(OLD_PASSWORD, "");
        uid_tx = passwordSharedPreferences.getString(UID, "").concat("@play.org");
        name_tx = passwordSharedPreferences.getString(USER_NAME, "");

        uid_header.setText(uid_tx);
        userName.setText(name_tx);

        password = view.findViewById(R.id.cp_passwordEditText);

        submit = view.findViewById(R.id.cp_submitButton);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password_tx = password.getText().toString().trim();
                if(EditTextValidations()) updatePassword();
            }
        });

        return view;
    }

    //    creating second auth instance
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

//    update password
    public void updatePassword(){

        Log.w("raky", uid_tx+" "+oldPassword_tx);
        mAuth2.signInWithEmailAndPassword(uid_tx, oldPassword_tx)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(getContext(), "failed!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                          //  FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            mAuth2.getCurrentUser().updatePassword(password_tx)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //Log.w("raky", user.getDisplayName());
                                                Log.d("raky", "User password updated.");
                                                String key = mAuth2.getCurrentUser().getUid();
                                                databaseReference.child("AangadiaProfile").child(key)
                                                        .child("password").setValue(password_tx);
                                            }
                                            else {
                                                Log.d("raky", "User password failed.");
                                            }
                                        }
                                    });
                        } // else ends

                        mAuth2.signOut();
                    }
                });
    }

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
        return true;
    }


//ends
}



