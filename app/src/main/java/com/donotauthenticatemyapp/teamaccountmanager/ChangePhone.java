package com.donotauthenticatemyapp.teamaccountmanager;


import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mehdi.sakout.fancybuttons.FancyButton;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePhone extends Fragment {

    String  newPhone_tx;
    EditText newPhone_et;
    TextView uid_header, userName;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    FancyButton submit;

    private static final String UPDATE_PREF = "change_password_pref";
    private static final String KEY = "key";
    private static final String UID = "uid";
    private static final String USER_NAME = "userName";
    private static final String PATH = "path";
    SharedPreferences passwordSharedPreferences;

    RelativeLayout relativeLayout;

    ProgressDialog progressDialog;

    private String uid_tx, oldName_tx, key_tx, path_tx;


    public ChangePhone() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_phone, container, false);

        uid_header = view.findViewById(R.id.cm_uidTextViewHeader);
        userName = view.findViewById(R.id.cm_nameViewHeader);
        newPhone_et = view.findViewById(R.id.cm_phoneEditText);
        submit = view.findViewById(R.id.cm_submitButton);
        relativeLayout = view.findViewById(R.id.cm_relativeLayout);

        passwordSharedPreferences = getActivity().getSharedPreferences(UPDATE_PREF, MODE_PRIVATE);
        uid_tx = passwordSharedPreferences.getString(UID, "");
        oldName_tx = passwordSharedPreferences.getString(USER_NAME, "");
        key_tx = passwordSharedPreferences.getString(KEY, "");
        path_tx = passwordSharedPreferences.getString(PATH, "");
       // Toast.makeText(getContext(), ""+key_tx, Toast.LENGTH_SHORT).show();

        progressDialog = new ProgressDialog(getContext());
        // Setting up message in Progress dialog.
        progressDialog.setMessage("updating...");

        uid_header.setText(uid_tx);
        userName.setText(oldName_tx);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newPhone_tx = newPhone_et.getText().toString().trim();
                if(EditTextValidations() && !TextUtils.isEmpty(key_tx)) updatePhone();
            }
        });

        return view;
    }

    public void onStart(){
        super.onStart();

        LoadAnimation();
    }

    private void LoadAnimation() {
        YoYo.with(Techniques.Landing)
                .duration(1000)
                .repeat(0)
                .playOn(relativeLayout);
    }

    //    update phone
    private void updatePhone() {
        progressDialog.show();
        databaseReference.child(path_tx).child(key_tx).child("phone").setValue(newPhone_tx)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            new MaterialDialog.Builder(getActivity())
                                    .title("Phone Number updated.")
                                    .titleColor(Color.WHITE)
                                    .content("New Phone Number: " + newPhone_tx)
                                    .icon(getResources().getDrawable(R.drawable.ic_success))
                                    .contentColor(getResources().getColor(R.color.lightCoral))
                                    .backgroundColor(getResources().getColor(R.color.black90))
                                    .positiveText(R.string.ok)
                                    .show();

                        }else {
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
                    }
                });
        progressDialog.dismiss();
    }

    //validations
    public boolean EditTextValidations(){
        if (newPhone_et.length() < 10){
            new MaterialDialog.Builder(getActivity())
                    .title("Invalid Mobile Number!")
                    .titleColor(Color.WHITE)
                    .content("Please recheck the mobile number")
                    .icon(getResources().getDrawable(R.drawable.ic_warning))
                    .contentColor(getResources().getColor(R.color.lightCoral))
                    .backgroundColor(getResources().getColor(R.color.black90))
                    .positiveText(R.string.ok)
                    .show();
            return false;
        }
        return true;
    }

}
