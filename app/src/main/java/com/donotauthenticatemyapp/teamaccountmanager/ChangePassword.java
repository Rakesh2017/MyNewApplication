package com.donotauthenticatemyapp.teamaccountmanager;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.UUID;

import mehdi.sakout.fancybuttons.FancyButton;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePassword extends Fragment {

    EditText password;
    String password_tx, oldPassword_tx, uid_tx;

    FancyButton submit;

    protected FirebaseAuth mAuth1;
    protected FirebaseAuth mAuth2;

    protected FirebaseApp myApp;

    private static final String CHANGE_PASSWORD_PREF = "change_password_pref";
    private static final String OLD_PASSWORD = "old_password";
    private static final String UID = "uid";

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
        passwordSharedPreferences = getActivity().getSharedPreferences(CHANGE_PASSWORD_PREF, MODE_PRIVATE);
        oldPassword_tx = passwordSharedPreferences.getString(OLD_PASSWORD, "");
        uid_tx = passwordSharedPreferences.getString(UID, "");

        password = view.findViewById(R.id.cp_passwordEditText);

        submit = view.findViewById(R.id.cp_submitButton);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePassword();
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

        mAuth2.signInWithEmailAndPassword(uid_tx, password_tx)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(getContext(), "failed!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.updatePassword(password_tx)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //Log.w("raky", user.getDisplayName());
                                                Log.d("raky", "User password updated.");
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


//ends
}
