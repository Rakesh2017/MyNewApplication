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
public class ChangeName extends Fragment {

    String  newName_tx;
    EditText newName_et;
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

    public ChangeName() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_name, container, false);

        uid_header = view.findViewById(R.id.cn_uidTextViewHeader);
        userName = view.findViewById(R.id.cn_nameViewHeader);
        newName_et = view.findViewById(R.id.cn_userNameEditText);
        submit = view.findViewById(R.id.cn_submitButton);
        relativeLayout = view.findViewById(R.id.cn_relativeLayout);

        passwordSharedPreferences = getActivity().getSharedPreferences(UPDATE_PREF, MODE_PRIVATE);
        uid_tx = passwordSharedPreferences.getString(UID, "");
        oldName_tx = passwordSharedPreferences.getString(USER_NAME, "");
        key_tx = passwordSharedPreferences.getString(KEY, "");
        path_tx = passwordSharedPreferences.getString(PATH, "");
      //  Toast.makeText(getContext(), ""+key_tx, Toast.LENGTH_SHORT).show();

        progressDialog = new ProgressDialog(getContext());
        // Setting up message in Progress dialog.
        progressDialog.setMessage("updating...");

        uid_header.setText(uid_tx);
        userName.setText(oldName_tx);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newName_tx = newName_et.getText().toString().trim();
                if(EditTextValidations() && !TextUtils.isEmpty(key_tx)) updateName();
            }
        });

        return view;
    }

//    onStart
    public void onStart(){
        super.onStart();

        LoadAnimation();
    }
//    onStart

//    load animation
    private void LoadAnimation() {
        YoYo.with(Techniques.Landing)
                .duration(1000)
                .repeat(0)
                .playOn(relativeLayout);
    }
//    load animation

//    update Name
    private void updateName() {
        progressDialog.show();
        databaseReference.child(path_tx).child(key_tx).child("userName").setValue(newName_tx)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            new MaterialDialog.Builder(getActivity())
                                    .title("User Name Updated")
                                    .titleColor(Color.WHITE)
                                    .content("New User Name: " + newName_tx)
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
//    update name

    //validations
    public boolean EditTextValidations(){
        if (TextUtils.isEmpty(newName_tx)){
            new MaterialDialog.Builder(getActivity())
                    .title("Empty")
                    .titleColor(Color.WHITE)
                    .content("Enter name please")
                    .icon(getResources().getDrawable(R.drawable.ic_warning))
                    .contentColor(getResources().getColor(R.color.lightCoral))
                    .backgroundColor(getResources().getColor(R.color.black90))
                    .positiveText(R.string.ok)
                    .show();
            return false;
        }
        return true;
    }
//    validations

//end
}
