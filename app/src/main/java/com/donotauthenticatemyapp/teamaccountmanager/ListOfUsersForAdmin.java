package com.donotauthenticatemyapp.teamaccountmanager;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListOfUsersForAdmin extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;
    List<RecyclerViewListAangadiaData> list = new ArrayList<>();

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ImageButton back_ib, search_btn, reset_btn;
    ProgressDialog progressDialog;

    AutoCompleteTextView state_et, city_et;
    final String [] india_states = new String[]{"Andra Pradesh","Arunachal Pradesh","Assam","Bihar","Chhattisgarh","Goa","Gujarat"
            ,"Haryana","Himachal Pradesh","Jammu and Kashmir","Jharkhand","Karnataka","Kerala","Madya Pradesh","Maharashtra"
            ,"Manipur","Meghalaya","Mizoram","Nagaland","Orissa","Punjab","Rajasthan","Sikkim"
            ,"Tamil Nadu","Tripura","Uttaranchal","Uttar Pradesh","West Bengal","Delhi","Lakshadeep","Pondicherry"
            ,"Andaman and Nicobar","Chandigarh","Dadar and Nagar","Daman and Diu"};

    final String [] india_cities = new String[]{ "Adilabad","Agra","Ahmedabad","Ahmednagar","Aizawl","Ajitgarh (Mohali)","Ajmer","Akola","Alappuzha","Aligarh","Alirajpur","Allahabad","Almora","Alwar","Ambala","Ambedkar Nagar","Amravati","Amreli district","Amritsar","Anand","Anantapur","Anantnag","Angul","Anjaw","Anuppur","Araria","Ariyalur","Arwal","Ashok Nagar","Auraiya","Aurangabad","Aurangabad","Azamgarh","Badgam","Bagalkot","Bageshwar","Bagpat","Bahraich","Baksa","Balaghat","Balangir","Balasore","Ballia","Balrampur","Banaskantha","Banda","Bandipora","Bangalore Rural","Bangalore Urban","Banka","Bankura","Banswara","Barabanki","Baramulla","Baran","Bardhaman","Bareilly","Bargarh (Baragarh)","Barmer","Barnala","Barpeta","Barwani","Bastar","Basti","Bathinda","Beed","Begusarai","Belgaum","Bellary","Betul","Bhadrak","Bhagalpur","Bhandara","Bharatpur","Bharuch","Bhavnagar","Bhilwara","Bhind","Bhiwani","Bhojpur","Bhopal","Bidar","Bijapur","Bijapur","Bijnor","Bikaner","Bilaspur","Bilaspur","Birbhum","Bishnupur","Bokaro","Bongaigaon","Boudh (Bauda)","Budaun","Bulandshahr","Buldhana","Bundi","Burhanpur","Buxar","Cachar","Central Delhi","Chamarajnagar","Chamba","Chamoli","Champawat","Champhai","Chandauli","Chandel","Chandigarh","Chandrapur","Changlang","Chatra","Chennai","Chhatarpur","Chhatrapati Shahuji Maharaj Nagar","Chhindwara","Chikkaballapur","Chikkamagaluru","Chirang","Chitradurga","Chitrakoot","Chittoor","Chittorgarh","Churachandpur","Churu","Coimbatore","Cooch Behar","Cuddalore","Cuttack","Dadra and Nagar Haveli","Dahod","Dakshin Dinajpur","Dakshina Kannada","Daman","Damoh","Dantewada","Darbhanga","Darjeeling","Darrang","Datia","Dausa","Davanagere","Debagarh (Deogarh)","Dehradun","Deoghar","Deoria","Dewas","Dhalai","Dhamtari","Dhanbad","Dhar","Dharmapuri","Dharwad","Dhemaji","Dhenkanal","Dholpur","Dhubri","Dhule","Dibang Valley","Dibrugarh","Dima Hasao","Dimapur","Dindigul","Dindori","Diu","Doda","Dumka","Dungapur","Durg","East Champaran","East Delhi","East Garo Hills","East Khasi Hills","East Siang","East Sikkim","East Singhbhum","Eluru","Ernakulam","Erode","Etah","Etawah","Faizabad","Faridabad","Faridkot","Farrukhabad","Fatehabad","Fatehgarh Sahib","Fatehpur","Fazilka","Firozabad","Firozpur","Gadag","Gadchiroli","Gajapati","Ganderbal","Gandhinagar","Ganganagar","Ganjam","Garhwa","Gautam Buddh Nagar","Gaya","Ghaziabad","Ghazipur","Giridih","Goalpara","Godda","Golaghat","Gonda","Gondia","Gopalganj","Gorakhpur","Gulbarga","Gumla","Guna","Guntur","Gurdaspur","Gurgaon","Gwalior","Hailakandi","Hamirpur","Hamirpur","Hanumangarh","Harda","Hardoi","Haridwar","Hassan","Haveri district","Hazaribag","Hingoli","Hissar","Hooghly","Hoshangabad","Hoshiarpur","Howrah","Hyderabad","Hyderabad","Idukki","Imphal East","Imphal West","Indore","Jabalpur","Jagatsinghpur","Jaintia Hills","Jaipur","Jaisalmer","Jajpur","Jalandhar","Jalaun","Jalgaon","Jalna","Jalore","Jalpaiguri","Jammu","Jamnagar","Jamtara","Jamui","Janjgir-Champa","Jashpur","Jaunpur district","Jehanabad","Jhabua","Jhajjar","Jhalawar","Jhansi","Jharsuguda","Jhunjhunu","Jind","Jodhpur","Jorhat","Junagadh","Jyotiba Phule Nagar","Kabirdham (formerly Kawardha)","Kadapa","Kaimur","Kaithal","Kakinada","Kalahandi","Kamrup","Kamrup Metropolitan","Kanchipuram","Kandhamal","Kangra","Kanker","Kannauj","Kannur","Kanpur","Kanshi Ram Nagar","Kanyakumari","Kapurthala","Karaikal","Karauli","Karbi Anglong","Kargil","Karimganj","Karimnagar","Karnal","Karur","Kasaragod","Kathua","Katihar","Katni","Kaushambi","Kendrapara","Kendujhar (Keonjhar)","Khagaria","Khammam","Khandwa (East Nimar)","Khargone (West Nimar)","Kheda","Khordha","Khowai","Khunti","Kinnaur","Kishanganj","Kishtwar","Kodagu","Koderma","Kohima","Kokrajhar","Kolar","Kolasib","Kolhapur","Kolkata","Kollam","Koppal","Koraput","Korba","Koriya","Kota","Kottayam","Kozhikode","Krishna","Kulgam","Kullu","Kupwara","Kurnool","Kurukshetra","Kurung Kumey","Kushinagar","Kutch","Lahaul and Spiti","Lakhimpur","Lakhimpur Kheri","Lakhisarai","Lalitpur","Latehar","Latur","Lawngtlai","Leh","Lohardaga","Lohit","Lower Dibang Valley","Lower Subansiri","Lucknow","Ludhiana","Lunglei","Madhepura","Madhubani","Madurai","Mahamaya Nagar","Maharajganj","Mahasamund","Mahbubnagar","Mahe","Mahendragarh","Mahoba","Mainpuri","Malappuram","Maldah","Malkangiri","Mamit","Mandi","Mandla","Mandsaur","Mandya","Mansa","Marigaon","Mathura","Mau","Mayurbhanj","Medak","Meerut","Mehsana","Mewat","Mirzapur","Moga","Mokokchung","Mon","Moradabad","Morena","Mumbai City","Mumbai suburban","Munger","Murshidabad","Muzaffarnagar","Muzaffarpur","Mysore","Nabarangpur","Nadia","Nagaon","Nagapattinam","Nagaur","Nagpur","Nainital","Nalanda","Nalbari","Nalgonda","Namakkal","Nanded","Nandurbar","Narayanpur","Narmada","Narsinghpur","Nashik","Navsari","Nawada","Nawanshahr","Nayagarh","Neemuch","Nellore","New Delhi","Nilgiris","Nizamabad","North 24 Parganas","North Delhi","North East Delhi","North Goa","North Sikkim","North Tripura","North West Delhi","Nuapada","Ongole","Osmanabad","Pakur","Palakkad","Palamu","Pali","Palwal","Panchkula","Panchmahal","Panchsheel Nagar district (Hapur)","Panipat","Panna","Papum Pare","Parbhani","Paschim Medinipur","Patan","Pathanamthitta","Pathankot","Patiala","Patna","Pauri Garhwal","Perambalur","Phek","Pilibhit","Pithoragarh","Pondicherry","Poonch","Porbandar","Pratapgarh","Pratapgarh","Pudukkottai","Pulwama","Pune","Purba Medinipur","Puri","Purnia","Purulia","Raebareli","Raichur","Raigad","Raigarh","Raipur","Raisen","Rajauri","Rajgarh","Rajkot","Rajnandgaon","Rajsamand","Ramabai Nagar (Kanpur Dehat)","Ramanagara","Ramanathapuram","Ramban","Ramgarh","Rampur","Ranchi","Ratlam","Ratnagiri","Rayagada","Reasi","Rewa","Rewari","Ri Bhoi","Rohtak","Rohtas","Rudraprayag","Rupnagar","Sabarkantha","Sagar","Saharanpur","Saharsa","Sahibganj","Saiha","Salem","Samastipur","Samba","Sambalpur","Sangli","Sangrur","Sant Kabir Nagar","Sant Ravidas Nagar","Saran","Satara","Satna","Sawai Madhopur","Sehore","Senapati","Seoni","Seraikela Kharsawan","Serchhip","Shahdol","Shahjahanpur","Shajapur","Shamli","Sheikhpura","Sheohar","Sheopur","Shimla","Shimoga","Shivpuri","Shopian","Shravasti","Sibsagar","Siddharthnagar","Sidhi","Sikar","Simdega","Sindhudurg","Singrauli","Sirmaur","Sirohi","Sirsa","Sitamarhi","Sitapur","Sivaganga","Siwan","Solan","Solapur","Sonbhadra","Sonipat","Sonitpur","South 24 Parganas","South Delhi","South Garo Hills","South Goa","South Sikkim","South Tripura","South West Delhi","Sri Muktsar Sahib","Srikakulam","Srinagar","Subarnapur (Sonepur)","Sultanpur","Sundergarh","Supaul","Surat","Surendranagar","Surguja","Tamenglong","Tarn Taran","Tawang","Tehri Garhwal","Thane","Thanjavur","The Dangs","Theni","Thiruvananthapuram","Thoothukudi","Thoubal","Thrissur","Tikamgarh","Tinsukia","Tirap","Tiruchirappalli","Tirunelveli","Tirupur","Tiruvallur","Tiruvannamalai","Tiruvarur","Tonk","Tuensang","Tumkur","Udaipur","Udalguri","Udham Singh Nagar","Udhampur","Udupi","Ujjain","Ukhrul","Umaria","Una","Unnao","Upper Siang","Upper Subansiri","Uttar Dinajpur","Uttara Kannada","Uttarkashi","Vadodara","Vaishali","Valsad","Varanasi","Vellore","Vidisha","Viluppuram","Virudhunagar","Visakhapatnam","Vizianagaram","Vyara","Warangal","Wardha","Washim","Wayanad","West Champaran","West Delhi","West Garo Hills","West Kameng","West Khasi Hills","West Siang","West Sikkim","West Singhbhum","West Tripura","Wokha","Yadgir","Yamuna Nagar","Yanam","Yavatmal","Zunheboto" };

    EditText uid_et, name_et, aangdiaName_et;
    String uid_tx, name_tx, state_tx, city_tx, aangadiaName_tx;

    LinearLayoutManager recyclerViewLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_users_for_admin);

        state_et = findViewById(R.id.lou_state);
        city_et = findViewById(R.id.lou_city);
        uid_et = findViewById(R.id.lou_uidEditText);
        name_et = findViewById(R.id.lou_userName);
        search_btn = findViewById(R.id.lou_search);
        reset_btn = findViewById(R.id.lou_resetImageButton);
        aangdiaName_et = findViewById(R.id.lou_aangadiaName);

        recyclerView = findViewById(R.id.lou_recyclerView);
        recyclerView.setHasFixedSize(true);

        recyclerViewLayoutManager = new LinearLayoutManager(ListOfUsersForAdmin.this);


        progressDialog = new ProgressDialog(ListOfUsersForAdmin.this);
        progressDialog.setMessage("Loading Data...");

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        back_ib = findViewById(R.id.lou_backImageButton);
        back_ib.setOnClickListener(this);
        search_btn.setOnClickListener(this);
        reset_btn.setOnClickListener(this);

        final ArrayAdapter<String> adapter_states = new ArrayAdapter<>
                (ListOfUsersForAdmin.this, android.R.layout.simple_list_item_1, india_states);
        state_et.setAdapter(adapter_states);

        final ArrayAdapter<String> adapter_cities = new ArrayAdapter<>
                (ListOfUsersForAdmin.this, android.R.layout.simple_list_item_1, india_cities);
        city_et.setAdapter(adapter_cities);

        checkConnectionBeforeFirstTimeLoading();
    }


//    internet check before load data
    public void checkConnectionBeforeFirstTimeLoading(){
        progressDialog.show();
        new CheckNetworkConnection(ListOfUsersForAdmin.this, new CheckNetworkConnection.OnConnectionCallback() {
            @Override
            public void onConnectionSuccess() {
                LoadData();
            }

            @Override
            public void onConnectionFail(String msg) {
                progressDialog.dismiss();
                try {
                    new MaterialDialog.Builder(ListOfUsersForAdmin.this)
                            .title("No Internet Access!")
                            .titleColor(Color.BLACK)
                            .content("No internet connectivity detected. Please make sure you have working internet connection and try again.")
                            .icon(getResources().getDrawable(R.drawable.ic_no_internet_connection))
                            .contentColor(getResources().getColor(R.color.black))
                            .backgroundColor(getResources().getColor(R.color.white))
                            .positiveColor(getResources().getColor(R.color.green))
                            .negativeText("Cancel")
                            .negativeColor(getResources().getColor(R.color.red))
                            .positiveText("Try Again!")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    checkConnectionBeforeFirstTimeLoading();
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .cancelable(false)
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).execute();
    }

    public void onStart(){
        super.onStart();
        //        check password change


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.lou_backImageButton){
            onBackPressed();
        }

        if (id == R.id.lou_resetImageButton){
            progressDialog.show();
            new CheckNetworkConnection(ListOfUsersForAdmin.this, new CheckNetworkConnection.OnConnectionCallback() {
                @Override
                public void onConnectionSuccess() {
                    LoadData();
                }
                @Override
                public void onConnectionFail(String msg) {
                    NoInternetConnectionAlert noInternetConnectionAlert = new NoInternetConnectionAlert(ListOfUsersForAdmin.this);
                    noInternetConnectionAlert.DisplayNoInternetConnection();
                    progressDialog.dismiss();
                }
            }).execute();

        }

//        search button
        if (id == R.id.lou_search){
            state_tx = state_et.getText().toString().trim();
            city_tx = city_et.getText().toString().trim();
            uid_tx = uid_et.getText().toString().trim();
            name_tx = name_et.getText().toString().trim();
            aangadiaName_tx = aangdiaName_et.getText().toString().trim();

            new CheckNetworkConnection(ListOfUsersForAdmin.this, new CheckNetworkConnection.OnConnectionCallback() {
                @Override
                public void onConnectionSuccess() {
                    if (!TextUtils.isEmpty(state_tx)) FilterByState();
                    else if (!TextUtils.isEmpty(city_tx)) FilterByCity();
                    else if(!TextUtils.isEmpty(aangadiaName_tx)) FilterByAangadiaName();
                    else if (!TextUtils.isEmpty(uid_tx)) FilterByUID();
                    else if (!TextUtils.isEmpty(name_tx)) FilterByUserName();
                }
                @Override
                public void onConnectionFail(String msg) {
                    NoInternetConnectionAlert noInternetConnectionAlert = new NoInternetConnectionAlert(ListOfUsersForAdmin.this);
                    noInternetConnectionAlert.DisplayNoInternetConnection();
                    progressDialog.dismiss();
                }
            }).execute();

        }

    }

//    created by aangadia
    private void FilterByAangadiaName() {
        progressDialog.show();
        databaseReference.child("userProfile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(list!=null) {
                    list.clear();  // v v v v important (eliminate duplication of data)
                }

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    RecyclerViewListAangadiaData userData = postSnapshot.getValue(RecyclerViewListAangadiaData.class);
                    String str = userData.getAangadia_userName();
                    String findStr = aangadiaName_tx;
                    try{
                        if (str.split(findStr, -1).length-1 > 0) list.add(userData);
                    }
                    catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                if (list.isEmpty())
                    showEmptyPage();

                adapter = new ListOfUsersForAdminRecyclerViewAdapter(ListOfUsersForAdmin.this, list);

                recyclerView.setAdapter(adapter);

                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hiding the progress dialog.
                progressDialog.dismiss();
            }
        });
    }

    //    filter by state
    private void FilterByState() {
        progressDialog.show();
        databaseReference.child("userProfile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(list!=null) {
                    list.clear();  // v v v v important (eliminate duplication of data)
                }

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    RecyclerViewListAangadiaData userData = postSnapshot.getValue(RecyclerViewListAangadiaData.class);
                    String str = userData.getState();
                    String findStr = state_tx;
                    if (str.split(findStr, -1).length-1 > 0) list.add(userData);
                }

                if (list.isEmpty())
                    showEmptyPage();

                adapter = new ListOfUsersForAdminRecyclerViewAdapter(ListOfUsersForAdmin.this, list);
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hiding the progress dialog.
                progressDialog.dismiss();
            }
        });
    }
//    filter by state

    //    filter by city
    private void FilterByCity() {
        progressDialog.show();
        databaseReference.child("userProfile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(list!=null) {
                    list.clear();  // v v v v important (eliminate duplication of data)
                }

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    RecyclerViewListAangadiaData userData = postSnapshot.getValue(RecyclerViewListAangadiaData.class);
                    String str = userData.getCity();
                    String findStr = city_tx;
                    if (str.split(findStr, -1).length-1 > 0) list.add(userData);
                }

                if (list.isEmpty())
                    showEmptyPage();

                adapter = new ListOfUsersForAdminRecyclerViewAdapter(ListOfUsersForAdmin.this, list);
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hiding the progress dialog.
                progressDialog.dismiss();
            }
        });
    }
//    filter by city


    //    filter by uid
    private void FilterByUID() {
        progressDialog.show();
        databaseReference.child("userProfile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(list!=null) {
                    list.clear();  // v v v v important (eliminate duplication of data)
                }

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    RecyclerViewListAangadiaData userData = postSnapshot.getValue(RecyclerViewListAangadiaData.class);
                    String str = userData.getUid();
                    String findStr = uid_tx;
                    if (str.split(findStr, -1).length-1 > 0) list.add(userData);
                }

                if (list.isEmpty())
                    showEmptyPage();
                adapter = new ListOfUsersForAdminRecyclerViewAdapter(ListOfUsersForAdmin.this, list);
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hiding the progress dialog.
                progressDialog.dismiss();
            }
        });
    }
//    filter by uid

    //    filter by user name
    private void FilterByUserName() {
        progressDialog.show();
        databaseReference.child("userProfile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(list!=null) {
                    list.clear();  // v v v v important (eliminate duplication of data)
                }

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    RecyclerViewListAangadiaData userData = postSnapshot.getValue(RecyclerViewListAangadiaData.class);
                    String str = userData.getUserName();
                    String findStr = name_tx;
                    if (str.split(findStr, -1).length-1 > 0) list.add(userData);
                }

                if (list.isEmpty())
                    showEmptyPage();
                adapter = new ListOfUsersForAdminRecyclerViewAdapter(ListOfUsersForAdmin.this, list);
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hiding the progress dialog.
                progressDialog.dismiss();
            }
        });
    }
//    filter by user name


    public void LoadData(){

        progressDialog.show();

        databaseReference.child("userProfile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(list!=null) {
                    list.clear();  // v v v v important (eliminate duplication of data)
                }

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    RecyclerViewListAangadiaData userData = postSnapshot.getValue(RecyclerViewListAangadiaData.class);
                    list.add(userData);
                }

                if (list.isEmpty())
                    showEmptyPage();
                adapter = new ListOfUsersForAdminRecyclerViewAdapter(ListOfUsersForAdmin.this, list);

                recyclerView.setAdapter(adapter);

                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hiding the progress dialog.
                progressDialog.dismiss();
            }
        });

    }

    //    show empty page
    public void showEmptyPage(){
        try{
            new MaterialDialog.Builder(ListOfUsersForAdmin.this)
                    .title("Empty!")
                    .titleColor(Color.BLACK)
                    .content("No Data Available.")
                    .icon(getResources().getDrawable(R.drawable.ic_warning))
                    .contentColor(getResources().getColor(R.color.black))
                    .backgroundColor(getResources().getColor(R.color.white))
                    .positiveText(R.string.ok)
                    .show();
        }
        catch (Exception e){
//            exception
        }

    }

    public void onDestroy(){
        super.onDestroy();
        if (progressDialog.isShowing()) progressDialog.dismiss();
    }

    //end
}
