package com.donotauthenticatemyapp.teamaccountmanager;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.MODE_PRIVATE;

public class PasswordCheck {

    SharedPreferences userIdentifierSharedPreferences;
    private static final String USER_IDENTIFIER_PREF = "userIdentifierPref";
    private static final String AANGADIA_KEY = "aangadia_key";
    private static final String USER_KEY = "user_key";
    private static final String PASSWORD_KEY = "password_key";
    private static final String LANDING_ACTIVITY = "landingActivity";
    private static final String FIRST_SCREEN = "firstScreen";

    private Context context;

//    constructor
    public PasswordCheck(Context context){
         this.context = context;
    }

    //    check if password changed
    public void checkIfPasswordChanged(){
        try {
            userIdentifierSharedPreferences = context.getSharedPreferences(USER_IDENTIFIER_PREF, MODE_PRIVATE);
            final String key = userIdentifierSharedPreferences.getString(AANGADIA_KEY, "");
            final String saved_password_key = userIdentifierSharedPreferences.getString(PASSWORD_KEY, "");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("PasswordKey").child(key)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String password_key = dataSnapshot.child("key").getValue(String.class);

                            if (!TextUtils.isEmpty(password_key)) {
                                if (!TextUtils.equals(password_key, saved_password_key)) {
                                    SharedPreferences sharedpreferences = context.getSharedPreferences(LANDING_ACTIVITY, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString(FIRST_SCREEN, "");
                                    editor.clear();
                                    editor.apply();
                                    ((Activity) context).finish();
                                    // (context).startActivity(new Intent((context), Login.class));
                                    Toast.makeText((context), "Password Changed, " +
                                            "Please Login with new Password", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    //    check if password changed
    public void checkIfPasswordChangedForUser(){
        try {
            userIdentifierSharedPreferences = context.getSharedPreferences(USER_IDENTIFIER_PREF, MODE_PRIVATE);
            final String key = userIdentifierSharedPreferences.getString(USER_KEY, "");
            final String saved_password_key = userIdentifierSharedPreferences.getString(PASSWORD_KEY, "");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("PasswordKey").child(key)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String password_key = dataSnapshot.child("key").getValue(String.class);

                            if (!TextUtils.isEmpty(password_key)) {
                                if (!TextUtils.equals(password_key, saved_password_key)) {
                                    SharedPreferences sharedpreferences = context.getSharedPreferences(LANDING_ACTIVITY, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString(FIRST_SCREEN, "");
                                    editor.clear();
                                    editor.apply();
                                    ((Activity) context).finish();
                                    // (context).startActivity(new Intent((context), Login.class));
                                    Toast.makeText((context), "Password Changed, " +
                                            "Please Login with new Password", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    //    check if password changed
}
