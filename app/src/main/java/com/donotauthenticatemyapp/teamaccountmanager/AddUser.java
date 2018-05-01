package com.donotauthenticatemyapp.teamaccountmanager;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddUser extends Fragment implements View.OnClickListener {

    protected View view;

    private RelativeLayout relativeLayout;
    protected EditText userName_et, password_et, answer_et, phone_et;
    AutoCompleteTextView state_et, city_et;
    protected String userName_tx, password_tx, answer_tx, phone_tx, question_tx, state_tx, city_tx;
    protected FancyButton button;

    protected FirebaseAuth mAuth1;
    protected FirebaseAuth mAuth2;

    protected FirebaseApp myApp;

    SharedPreferences userIdentifierSharedPreferences;

    private static final String USER_IDENTIFIER_PREF = "userIdentifierPref";
    private static final String USER_IDENTITY = "userIdentity";

    protected static final String TIME_SERVER = "time-a.nist.gov";
    private static final String USER_PROFILE = "userProfile";

    String sub_id;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    protected Spinner spinner;
    final String [] states = new String[]{"Select Security Question...", "which is your favourite sport?", "where do you born?", "who is your favourite actor?"
            , "what is your pet's name?", "which is your favourite movie?", "who is your favourite singer?", "what is your best friend's name?"};

    ProgressDialog progressDialog;

    protected static final String PLAY_EMAIL = "@play.org";

    protected static String date, time, year;

    final String [] india_states = new String[]{"Andra Pradesh","Arunachal Pradesh","Assam","Bihar","Chhattisgarh","Goa","Gujarat"
            ,"Haryana","Himachal Pradesh","Jammu and Kashmir","Jharkhand","Karnataka","Kerala","Madya Pradesh","Maharashtra"
            ,"Manipur","Meghalaya","Mizoram","Nagaland","Orissa","Punjab","Rajasthan","Sikkim"
            ,"Tamil Nadu","Tripura","Uttaranchal","Uttar Pradesh","West Bengal","Delhi","Lakshadeep","Pondicherry"
            ,"Andaman and Nicobar","Chandigarh","Dadar and Nagar","Daman and Diu"};

    final String [] india_cities = new String[]{ "Adilabad","Agra","Ahmedabad","Ahmednagar","Aizawl","Ajitgarh (Mohali)","Ajmer","Akola","Alappuzha","Aligarh","Alirajpur","Allahabad","Almora","Alwar","Ambala","Ambedkar Nagar","Amravati","Amreli district","Amritsar","Anand","Anantapur","Anantnag","Angul","Anjaw","Anuppur","Araria","Ariyalur","Arwal","Ashok Nagar","Auraiya","Aurangabad","Aurangabad","Azamgarh","Badgam","Bagalkot","Bageshwar","Bagpat","Bahraich","Baksa","Balaghat","Balangir","Balasore","Ballia","Balrampur","Banaskantha","Banda","Bandipora","Bangalore Rural","Bangalore Urban","Banka","Bankura","Banswara","Barabanki","Baramulla","Baran","Bardhaman","Bareilly","Bargarh (Baragarh)","Barmer","Barnala","Barpeta","Barwani","Bastar","Basti","Bathinda","Beed","Begusarai","Belgaum","Bellary","Betul","Bhadrak","Bhagalpur","Bhandara","Bharatpur","Bharuch","Bhavnagar","Bhilwara","Bhind","Bhiwani","Bhojpur","Bhopal","Bidar","Bijapur","Bijapur","Bijnor","Bikaner","Bilaspur","Bilaspur","Birbhum","Bishnupur","Bokaro","Bongaigaon","Boudh (Bauda)","Budaun","Bulandshahr","Buldhana","Bundi","Burhanpur","Buxar","Cachar","Central Delhi","Chamarajnagar","Chamba","Chamoli","Champawat","Champhai","Chandauli","Chandel","Chandigarh","Chandrapur","Changlang","Chatra","Chennai","Chhatarpur","Chhatrapati Shahuji Maharaj Nagar","Chhindwara","Chikkaballapur","Chikkamagaluru","Chirang","Chitradurga","Chitrakoot","Chittoor","Chittorgarh","Churachandpur","Churu","Coimbatore","Cooch Behar","Cuddalore","Cuttack","Dadra and Nagar Haveli","Dahod","Dakshin Dinajpur","Dakshina Kannada","Daman","Damoh","Dantewada","Darbhanga","Darjeeling","Darrang","Datia","Dausa","Davanagere","Debagarh (Deogarh)","Dehradun","Deoghar","Deoria","Dewas","Dhalai","Dhamtari","Dhanbad","Dhar","Dharmapuri","Dharwad","Dhemaji","Dhenkanal","Dholpur","Dhubri","Dhule","Dibang Valley","Dibrugarh","Dima Hasao","Dimapur","Dindigul","Dindori","Diu","Doda","Dumka","Dungapur","Durg","East Champaran","East Delhi","East Garo Hills","East Khasi Hills","East Siang","East Sikkim","East Singhbhum","Eluru","Ernakulam","Erode","Etah","Etawah","Faizabad","Faridabad","Faridkot","Farrukhabad","Fatehabad","Fatehgarh Sahib","Fatehpur","Fazilka","Firozabad","Firozpur","Gadag","Gadchiroli","Gajapati","Ganderbal","Gandhinagar","Ganganagar","Ganjam","Garhwa","Gautam Buddh Nagar","Gaya","Ghaziabad","Ghazipur","Giridih","Goalpara","Godda","Golaghat","Gonda","Gondia","Gopalganj","Gorakhpur","Gulbarga","Gumla","Guna","Guntur","Gurdaspur","Gurgaon","Gwalior","Hailakandi","Hamirpur","Hamirpur","Hanumangarh","Harda","Hardoi","Haridwar","Hassan","Haveri district","Hazaribag","Hingoli","Hissar","Hooghly","Hoshangabad","Hoshiarpur","Howrah","Hyderabad","Hyderabad","Idukki","Imphal East","Imphal West","Indore","Jabalpur","Jagatsinghpur","Jaintia Hills","Jaipur","Jaisalmer","Jajpur","Jalandhar","Jalaun","Jalgaon","Jalna","Jalore","Jalpaiguri","Jammu","Jamnagar","Jamtara","Jamui","Janjgir-Champa","Jashpur","Jaunpur district","Jehanabad","Jhabua","Jhajjar","Jhalawar","Jhansi","Jharsuguda","Jhunjhunu","Jind","Jodhpur","Jorhat","Junagadh","Jyotiba Phule Nagar","Kabirdham (formerly Kawardha)","Kadapa","Kaimur","Kaithal","Kakinada","Kalahandi","Kamrup","Kamrup Metropolitan","Kanchipuram","Kandhamal","Kangra","Kanker","Kannauj","Kannur","Kanpur","Kanshi Ram Nagar","Kanyakumari","Kapurthala","Karaikal","Karauli","Karbi Anglong","Kargil","Karimganj","Karimnagar","Karnal","Karur","Kasaragod","Kathua","Katihar","Katni","Kaushambi","Kendrapara","Kendujhar (Keonjhar)","Khagaria","Khammam","Khandwa (East Nimar)","Khargone (West Nimar)","Kheda","Khordha","Khowai","Khunti","Kinnaur","Kishanganj","Kishtwar","Kodagu","Koderma","Kohima","Kokrajhar","Kolar","Kolasib","Kolhapur","Kolkata","Kollam","Koppal","Koraput","Korba","Koriya","Kota","Kottayam","Kozhikode","Krishna","Kulgam","Kullu","Kupwara","Kurnool","Kurukshetra","Kurung Kumey","Kushinagar","Kutch","Lahaul and Spiti","Lakhimpur","Lakhimpur Kheri","Lakhisarai","Lalitpur","Latehar","Latur","Lawngtlai","Leh","Lohardaga","Lohit","Lower Dibang Valley","Lower Subansiri","Lucknow","Ludhiana","Lunglei","Madhepura","Madhubani","Madurai","Mahamaya Nagar","Maharajganj","Mahasamund","Mahbubnagar","Mahe","Mahendragarh","Mahoba","Mainpuri","Malappuram","Maldah","Malkangiri","Mamit","Mandi","Mandla","Mandsaur","Mandya","Mansa","Marigaon","Mathura","Mau","Mayurbhanj","Medak","Meerut","Mehsana","Mewat","Mirzapur","Moga","Mokokchung","Mon","Moradabad","Morena","Mumbai City","Mumbai suburban","Munger","Murshidabad","Muzaffarnagar","Muzaffarpur","Mysore","Nabarangpur","Nadia","Nagaon","Nagapattinam","Nagaur","Nagpur","Nainital","Nalanda","Nalbari","Nalgonda","Namakkal","Nanded","Nandurbar","Narayanpur","Narmada","Narsinghpur","Nashik","Navsari","Nawada","Nawanshahr","Nayagarh","Neemuch","Nellore","New Delhi","Nilgiris","Nizamabad","North 24 Parganas","North Delhi","North East Delhi","North Goa","North Sikkim","North Tripura","North West Delhi","Nuapada","Ongole","Osmanabad","Pakur","Palakkad","Palamu","Pali","Palwal","Panchkula","Panchmahal","Panchsheel Nagar district (Hapur)","Panipat","Panna","Papum Pare","Parbhani","Paschim Medinipur","Patan","Pathanamthitta","Pathankot","Patiala","Patna","Pauri Garhwal","Perambalur","Phek","Pilibhit","Pithoragarh","Pondicherry","Poonch","Porbandar","Pratapgarh","Pratapgarh","Pudukkottai","Pulwama","Pune","Purba Medinipur","Puri","Purnia","Purulia","Raebareli","Raichur","Raigad","Raigarh","Raipur","Raisen","Rajauri","Rajgarh","Rajkot","Rajnandgaon","Rajsamand","Ramabai Nagar (Kanpur Dehat)","Ramanagara","Ramanathapuram","Ramban","Ramgarh","Rampur","Ranchi","Ratlam","Ratnagiri","Rayagada","Reasi","Rewa","Rewari","Ri Bhoi","Rohtak","Rohtas","Rudraprayag","Rupnagar","Sabarkantha","Sagar","Saharanpur","Saharsa","Sahibganj","Saiha","Salem","Samastipur","Samba","Sambalpur","Sangli","Sangrur","Sant Kabir Nagar","Sant Ravidas Nagar","Saran","Satara","Satna","Sawai Madhopur","Sehore","Senapati","Seoni","Seraikela Kharsawan","Serchhip","Shahdol","Shahjahanpur","Shajapur","Shamli","Sheikhpura","Sheohar","Sheopur","Shimla","Shimoga","Shivpuri","Shopian","Shravasti","Sibsagar","Siddharthnagar","Sidhi","Sikar","Simdega","Sindhudurg","Singrauli","Sirmaur","Sirohi","Sirsa","Sitamarhi","Sitapur","Sivaganga","Siwan","Solan","Solapur","Sonbhadra","Sonipat","Sonitpur","South 24 Parganas","South Delhi","South Garo Hills","South Goa","South Sikkim","South Tripura","South West Delhi","Sri Muktsar Sahib","Srikakulam","Srinagar","Subarnapur (Sonepur)","Sultanpur","Sundergarh","Supaul","Surat","Surendranagar","Surguja","Tamenglong","Tarn Taran","Tawang","Tehri Garhwal","Thane","Thanjavur","The Dangs","Theni","Thiruvananthapuram","Thoothukudi","Thoubal","Thrissur","Tikamgarh","Tinsukia","Tirap","Tiruchirappalli","Tirunelveli","Tirupur","Tiruvallur","Tiruvannamalai","Tiruvarur","Tonk","Tuensang","Tumkur","Udaipur","Udalguri","Udham Singh Nagar","Udhampur","Udupi","Ujjain","Ukhrul","Umaria","Una","Unnao","Upper Siang","Upper Subansiri","Uttar Dinajpur","Uttara Kannada","Uttarkashi","Vadodara","Vaishali","Valsad","Varanasi","Vellore","Vidisha","Viluppuram","Virudhunagar","Visakhapatnam","Vizianagaram","Vyara","Warangal","Wardha","Washim","Wayanad","West Champaran","West Delhi","West Garo Hills","West Kameng","West Khasi Hills","West Siang","West Sikkim","West Singhbhum","West Tripura","Wokha","Yadgir","Yamuna Nagar","Yanam","Yavatmal","Zunheboto" };

    public AddUser() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_user, container, false);

        CreatingFirebaseAuthInstance();

        userName_et = view.findViewById(R.id.fau_userNameEditText);
        password_et = view.findViewById(R.id.fau_passwordEditText);
        answer_et = view.findViewById(R.id.fau_securityAnswerEditText);
        phone_et = view.findViewById(R.id.fau_phoneEditText);
        answer_et = view.findViewById(R.id.fau_securityAnswerEditText);
        state_et = view.findViewById(R.id.fau_stateEditText);
        city_et = view.findViewById(R.id.fau_cityEditText);

        spinner = view.findViewById(R.id.fau_securityQuestionsSpinner);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (getActivity(),android.R.layout.simple_list_item_1,states);
        spinner.setAdapter(adapter);

        relativeLayout = view.findViewById(R.id.fau_mainRelativeLayout);

        button = view.findViewById(R.id.fau_submitButton);

        final ArrayAdapter<String> adapter_states = new ArrayAdapter<>
                (getActivity(),android.R.layout.simple_list_item_1,india_states);
        state_et.setAdapter(adapter_states);

        final ArrayAdapter<String> adapter_cities = new ArrayAdapter<>
                (getActivity(),android.R.layout.simple_list_item_1,india_cities);
        city_et.setAdapter(adapter_cities);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Creating User Account...");

        userIdentifierSharedPreferences = getActivity().getSharedPreferences(USER_IDENTIFIER_PREF, MODE_PRIVATE);


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
        button.setOnClickListener(this);
        return view;
    }


//    onStart
    public void onStart(){
        super.onStart();
//        functions
        LoadAnimations();
        new GetDateTime().execute();
        //changePassword();

    }
//    onStart

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

        if (id == R.id.fau_submitButton){  //main if
            answer_tx = answer_et.getText().toString().trim();
            password_tx = password_et.getText().toString().trim();
            userName_tx = userName_et.getText().toString().trim();
            phone_tx = phone_et.getText().toString().trim();
            state_tx = state_et.getText().toString().trim();
            city_tx = city_et.getText().toString().trim();
         //   Toast.makeText(getActivity(), "clicked", Toast.LENGTH_SHORT).show();
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
        final int Low = 10000;
        int High = 100000;
        int Result = r.nextInt(High-Low) + Low;

        sub_id =  year + String.valueOf(Result);
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
                            String identity = userIdentifierSharedPreferences.getString(USER_IDENTITY, "");
                            Log.w("raky", "uid: "+mAuth1.getCurrentUser().getUid());
                            Log.w("raky", "identity: "+identity);

                            if (TextUtils.equals(identity, "aangadia")) UserCreatedByAangadia();
                            else if (TextUtils.equals(identity, "admin")) UserCreatedByAdmin();


                        }

                    }
                });

    }

//    user created by aangadia
    private void UserCreatedByAangadia() {
        final String aangadia_key = mAuth1.getCurrentUser().getUid();
        databaseReference.child("AangadiaProfile").child(aangadia_key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String aangadia_name = dataSnapshot.child("userName").getValue(String.class);
                        String aangadia_UID = dataSnapshot.child("uid").getValue(String.class);
                        String userUID = mAuth2.getCurrentUser().getUid();
                        databaseReference.child(USER_PROFILE).child(userUID).child("userName").setValue(userName_tx);
                        databaseReference.child(USER_PROFILE).child(userUID).child("uid").setValue(sub_id);
                        databaseReference.child(USER_PROFILE).child(userUID).child("password").setValue(password_tx);
                        databaseReference.child(USER_PROFILE).child(userUID).child("phone").setValue(phone_tx);
                        databaseReference.child(USER_PROFILE).child(userUID).child("security_answer").setValue(answer_tx);
                        databaseReference.child(USER_PROFILE).child(userUID).child("security_question").setValue(question_tx);
                        databaseReference.child(USER_PROFILE).child(userUID).child("key").setValue(userUID);
                        databaseReference.child(USER_PROFILE).child(userUID).child("state").setValue(state_tx);
                        databaseReference.child(USER_PROFILE).child(userUID).child("city").setValue(city_tx);
                        databaseReference.child(USER_PROFILE).child(userUID).child("created_by").setValue("aangadia");
                        databaseReference.child(USER_PROFILE).child(userUID).child("aangadia_userName").setValue(aangadia_name);
                        databaseReference.child(USER_PROFILE).child(userUID).child("aangadia_key").setValue(aangadia_key);
                        databaseReference.child(USER_PROFILE).child(userUID).child("aangadia_uid").setValue(aangadia_UID);

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

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }


//    user created by admin
    public void UserCreatedByAdmin(){
        String userUID = mAuth2.getCurrentUser().getUid();
        databaseReference.child(USER_PROFILE).child(userUID).child("userName").setValue(userName_tx);
        databaseReference.child(USER_PROFILE).child(userUID).child("uid").setValue(sub_id);
        databaseReference.child(USER_PROFILE).child(userUID).child("password").setValue(password_tx);
        databaseReference.child(USER_PROFILE).child(userUID).child("phone").setValue(phone_tx);
        databaseReference.child(USER_PROFILE).child(userUID).child("security_answer").setValue(answer_tx);
        databaseReference.child(USER_PROFILE).child(userUID).child("security_question").setValue(question_tx);
        databaseReference.child(USER_PROFILE).child(userUID).child("key").setValue(userUID);
        databaseReference.child(USER_PROFILE).child(userUID).child("state").setValue(state_tx);
        databaseReference.child(USER_PROFILE).child(userUID).child("city").setValue(city_tx);
        databaseReference.child(USER_PROFILE).child(userUID).child("created_by").setValue("admin");

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


//    validations
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

    else if(state_tx.length() < 3){
        new MaterialDialog.Builder(getActivity())
                .title("Failed!")
                .titleColor(Color.WHITE)
                .content("Please provide State")
                .icon(getResources().getDrawable(R.drawable.ic_warning))
                .contentColor(getResources().getColor(R.color.lightCoral))
                .backgroundColor(getResources().getColor(R.color.black90))
                .positiveText(R.string.ok)
                .show();
        return false;
    }

    else if(city_tx.length() < 3){
        new MaterialDialog.Builder(getActivity())
                .title("Failed!")
                .titleColor(Color.WHITE)
                .content("Please provide city")
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

//    creating second auth
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
//    creating second auth

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

//    ends
}
