package com.donotauthenticatemyapp.teamaccountmanager;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import mehdi.sakout.fancybuttons.FancyButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddAangadia extends Fragment implements View.OnClickListener{

    protected View view;

    private RelativeLayout relativeLayout;
    protected EditText userName_et, password_et, answer_et, phone_et;
    protected String userName_tx, password_tx, answer_tx, phone_tx, question_tx;
    protected FancyButton button;

    protected FirebaseAuth mAuth1;
    protected FirebaseAuth mAuth2;

    //private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    //private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    protected FirebaseApp myApp;

    protected static final String TIME_SERVER = "time-a.nist.gov";

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    protected Spinner spinner;
    final String [] states = new String[]{"Select Security Question...", "which is your favourite sport?", "where do you born?", "who is your favourite actor?"
    , "what is your pet's name?", "which is your favourite movie?", "who is your favourite singer?", "what is your best friend's name?"};

    ProgressDialog progressDialog;

    protected static final String PLAY_EMAIL = "@play.org";

    protected static String date, time, year;

    int count = 1;

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
        answer_et = view.findViewById(R.id.fad_securityAnswerEditText);
        phone_et = view.findViewById(R.id.fad_phoneEditText);
        answer_et = view.findViewById(R.id.fad_securityAnswerEditText);

        spinner = view.findViewById(R.id.fad_securityQuestionsSpinner);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (getActivity(),android.R.layout.simple_list_item_1,states);
        spinner.setAdapter(adapter);

        relativeLayout = view.findViewById(R.id.fad_mainRelativeLayout);

        button = view.findViewById(R.id.fad_submitButton);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Creating Aangadia Account...");

        button.setOnClickListener(this);

//        spinner on touch
        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?>arg0, View view, int arg2, long arg3) {

                            String selected = spinner.getSelectedItem().toString();
                            if (!TextUtils.equals(selected,"Select Security Question...")){
                                question_tx = selected;
                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                            // TODO Auto-generated method stub

                        }
                    });

                }
                return false;
            }
        });
//        spinner on touch


        return view;
    }

    public void onStart(){
        super.onStart();
//        functions
        LoadAnimations();
        new GetDateTime().execute();
        //changePassword();

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
            answer_tx = answer_et.getText().toString().trim();
            password_tx = password_et.getText().toString().trim();
            userName_tx = userName_et.getText().toString().trim();
            phone_tx = phone_et.getText().toString().trim();
            CheckingValidations();



        }//if ends
    }//onclick ends

//    checking validations
    public void CheckingValidations()
    {
        if (EditTextValidations()){
            if (TextUtils.isEmpty(year)){
                new MaterialDialog.Builder(getActivity())
                        .title("Failed!")
                        .titleColor(Color.WHITE)
                        .content("Server Error, Try Again...")
                        .icon(getResources().getDrawable(R.drawable.ic_warning))
                        .contentColor(getResources().getColor(R.color.lightCoral))
                        .backgroundColor(getResources().getColor(R.color.black90))
                        .positiveText(R.string.ok)
                        .show();
            }
            else if (TextUtils.isEmpty(question_tx)){
                new MaterialDialog.Builder(getActivity())
                        .title("Failed!")
                        .titleColor(Color.WHITE)
                        .content("re-select Security Question!")
                        .icon(getResources().getDrawable(R.drawable.ic_warning))
                        .contentColor(getResources().getColor(R.color.lightCoral))
                        .backgroundColor(getResources().getColor(R.color.black90))
                        .positiveText(R.string.ok)
                        .show();
            }
            else CreateAangadia();

        }
    }
//    checking validations

//    create aangadia
    public void CreateAangadia(){

            progressDialog.show();

           Log.w("ray", "pain "+year);

            Random r = new Random();
            int Low = 10000;
            int High = 100000;
            int Result = r.nextInt(High-Low) + Low;

            final String sub_id =  year + String.valueOf(Result);
            final String unique_id = sub_id + PLAY_EMAIL;

            mAuth2.createUserWithEmailAndPassword(unique_id, password_tx)
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
                                databaseReference.child("AangadiaProfile").child(aangadiaUid).child("userName").setValue(userName_tx);
                                databaseReference.child("AangadiaProfile").child(aangadiaUid).child("uid").setValue(unique_id);
                                databaseReference.child("AangadiaProfile").child(aangadiaUid).child("password").setValue(password_tx);
                                databaseReference.child("AangadiaProfile").child(aangadiaUid).child("phone").setValue(phone_tx);
                                databaseReference.child("AangadiaProfile").child(aangadiaUid).child("security_answer").setValue(answer_tx);
                                databaseReference.child("AangadiaProfile").child(aangadiaUid).child("security_question").setValue(question_tx);
                                databaseReference.child("AangadiaProfile").child(aangadiaUid).child("key").setValue(aangadiaUid);

                                new MaterialDialog.Builder(getActivity())
                                        .title("Account Successfully Created")
                                        .titleColor(Color.WHITE)
                                        .content("Unique Id: "+ sub_id
                                                +"\nPassword: "+password_tx
                                                +"\nuserName: "+userName_tx
                                                +"\nPhone: "+phone_tx
                                                +"\n"+question_tx+": "+answer_tx)
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

        }
//        create aangadia

    public boolean EditTextValidations(){
         if (userName_tx.isEmpty()){
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

        else if(TextUtils.isEmpty(answer_tx)){
            new MaterialDialog.Builder(getActivity())
                    .title("Invalid Answer")
                    .titleColor(Color.WHITE)
                    .content("For Security purpose you have to answer one of the above questions.")
                    .icon(getResources().getDrawable(R.drawable.ic_warning))
                    .contentColor(getResources().getColor(R.color.lightCoral))
                    .backgroundColor(getResources().getColor(R.color.black90))
                    .positiveText(R.string.ok)
                    .show();
            return false;
        }
            return true;
    }

    //    validate email
  /*  public boolean validateEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }*/

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

    public void changePassword(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String newPassword = "password123";
        Log.w("raky", "hit");

        user.updatePassword(newPassword)
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
    }


//    getting date and time
    private static class GetDateTime extends AsyncTask<Void, Void, String> {

        Date dateTime;
        @Override
        protected String doInBackground(Void... voids) {
            try {
                NTPUDPClient timeClient = new NTPUDPClient();
                InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
                TimeInfo timeInfo = timeClient.getTime(inetAddress);
                long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();   //server time
                dateTime = new Date(returnTime);
            }
            catch (IOException e){
                Log.w("raky", e.getCause());
            }
            return String.valueOf(dateTime);
        }

        @Override
        protected void onPostExecute(String myDate){
                Log.w("raky", "this year: "+myDate);
                AddAangadia addAangadia = new AddAangadia();
              //  date = myDate.substring(0, 10);
               // time = myDate.substring(12, 16);
                year = myDate.substring(myDate.length()-2, myDate.length());
            }

    }
//    getting date and time

    //end
}
