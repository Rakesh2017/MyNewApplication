package com.donotauthenticatemyapp.teamaccountmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.flags.impl.DataUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListOfAangadias extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerView;
    RelativeLayout emptyRelativeLayout, recyclerViewRelativeLayout;

    TextView emptyTextView;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;
    List<RecyclerViewListAangadiaData> list = new ArrayList<>();

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ImageButton back_ib, search_btn, reset_btn;
    ProgressDialog progressDialog;

    EditText uid_et, name_et;
    String uid_tx, name_tx;

    LinearLayoutManager recyclerViewLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_aangadias);

        recyclerView = findViewById(R.id.loa_recyclerView);
        recyclerViewRelativeLayout = findViewById(R.id.loa_recyclerViewRelativeLayout);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(ListOfAangadias.this);


        progressDialog = new ProgressDialog(ListOfAangadias.this);
        progressDialog.setMessage("Loading Data...");

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        uid_et = findViewById(R.id.loa_uidEditText);
        name_et = findViewById(R.id.loa_userNameEditText);
        search_btn = findViewById(R.id.loa_search);
        reset_btn = findViewById(R.id.loa_resetImageButton);

        back_ib = findViewById(R.id.loa_backImageButton);
        back_ib.setOnClickListener(this);
        search_btn.setOnClickListener(this);
        reset_btn.setOnClickListener(this);

        checkConnectionBeforeFirstTimeLoading();
    }


    //    internet check before load data
    public void checkConnectionBeforeFirstTimeLoading(){
        progressDialog.show();
        new CheckNetworkConnection(ListOfAangadias.this, new CheckNetworkConnection.OnConnectionCallback() {
            @Override
            public void onConnectionSuccess() {
                LoadData();
            }

            @Override
            public void onConnectionFail(String msg) {
                progressDialog.dismiss();
                try {
                    new MaterialDialog.Builder(ListOfAangadias.this)
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




    //    onStart
    public void onStart(){
        super.onStart();



    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.loa_backImageButton){
            onBackPressed();
        }
//        search button
        else if (id == R.id.loa_search){
            uid_tx = uid_et.getText().toString().trim();
            name_tx = name_et.getText().toString().trim();
            progressDialog.show();
            if (TextUtils.isEmpty(uid_tx) && TextUtils.isEmpty(name_tx)){ //if1
                //progressDialog.show();
                new CheckNetworkConnection(ListOfAangadias.this, new CheckNetworkConnection.OnConnectionCallback() {
                    @Override
                    public void onConnectionSuccess() {
                        enterEitherUidOrName();
                    }
                    @Override
                    public void onConnectionFail(String msg) {
                        NoInternetConnectionAlert noInternetConnectionAlert = new NoInternetConnectionAlert(ListOfAangadias.this);
                        noInternetConnectionAlert.DisplayNoInternetConnection();
                        progressDialog.dismiss();
                    }
                }).execute();

            } //if1
            else if (!TextUtils.isEmpty(name_tx) && TextUtils.isEmpty(uid_tx)){
                new CheckNetworkConnection(ListOfAangadias.this, new CheckNetworkConnection.OnConnectionCallback() {
                    @Override
                    public void onConnectionSuccess() {
                        FilterByUserName();
                    }
                    @Override
                    public void onConnectionFail(String msg) {
                        NoInternetConnectionAlert noInternetConnectionAlert = new NoInternetConnectionAlert(ListOfAangadias.this);
                        noInternetConnectionAlert.DisplayNoInternetConnection();
                        progressDialog.dismiss();
                    }
                }).execute();

            }
            else if (TextUtils.isEmpty(name_tx) && !TextUtils.isEmpty(uid_tx)) {
                new CheckNetworkConnection(ListOfAangadias.this, new CheckNetworkConnection.OnConnectionCallback() {
                    @Override
                    public void onConnectionSuccess() {
                        FilterByUID();
                    }
                    @Override
                    public void onConnectionFail(String msg) {
                        NoInternetConnectionAlert noInternetConnectionAlert = new NoInternetConnectionAlert(ListOfAangadias.this);
                        noInternetConnectionAlert.DisplayNoInternetConnection();
                        progressDialog.dismiss();
                    }
                }).execute();

            }
            else if (!TextUtils.isEmpty(name_tx) && !TextUtils.isEmpty(uid_tx)){
                new CheckNetworkConnection(ListOfAangadias.this, new CheckNetworkConnection.OnConnectionCallback() {
                    @Override
                    public void onConnectionSuccess() {
                        FilterByNameAndUID();
                    }
                    @Override
                    public void onConnectionFail(String msg) {
                        NoInternetConnectionAlert noInternetConnectionAlert = new NoInternetConnectionAlert(ListOfAangadias.this);
                        noInternetConnectionAlert.DisplayNoInternetConnection();
                        progressDialog.dismiss();
                    }
                }).execute();

            }
        }

        else if (id == R.id.loa_resetImageButton) {
            new CheckNetworkConnection(ListOfAangadias.this, new CheckNetworkConnection.OnConnectionCallback() {
                @Override
                public void onConnectionSuccess() {
                    LoadData();
                }
                @Override
                public void onConnectionFail(String msg) {
                    NoInternetConnectionAlert noInternetConnectionAlert = new NoInternetConnectionAlert(ListOfAangadias.this);
                    noInternetConnectionAlert.DisplayNoInternetConnection();
                    progressDialog.dismiss();
                }
            }).execute();
        }
    }


    //    filter by user name and uid
    private void FilterByNameAndUID() {
        progressDialog.show();
        databaseReference.child("AangadiaProfile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(list!=null) {
                    list.clear();  // v v v v important (eliminate duplication of data)

                }

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    RecyclerViewListAangadiaData aangadiaData = postSnapshot.getValue(RecyclerViewListAangadiaData.class);
                    //  ("raky", "user: "+aangadiaData.getUserName());
                    String str = aangadiaData.getUid();
                    String str1 = aangadiaData.getUserName();
                    String findStr = uid_tx;
                    String findStr1 = name_tx;
                    if (str.split(findStr, -1).length-1 > 0 && str1.split(findStr1, -1).length-1 > 0) list.add(aangadiaData);
                }

                if (list.isEmpty())
                    showEmptyPage();

                adapter = new ListOfAangadiasRecyclerViewAdapter(ListOfAangadias.this, list);
                recyclerView.setAdapter(adapter);

                progressDialog.dismiss();

                Toast.makeText(ListOfAangadias.this, "name and uid", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hiding the progress dialog.
                progressDialog.dismiss();
            }
        });

    }
//    filter by user name and uid


    //    filter by user name
    private void FilterByUID() {
        progressDialog.show();
        databaseReference.child("AangadiaProfile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(list!=null) {
                    list.clear();  // v v v v important (eliminate duplication of data)
                }

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    RecyclerViewListAangadiaData aangadiaData = postSnapshot.getValue(RecyclerViewListAangadiaData.class);
                    //  ("raky", "user: "+aangadiaData.getUserName());
                    String str = aangadiaData.getUid();
                    String findStr = uid_tx;
                    if (str.split(findStr, -1).length-1 > 0) list.add(aangadiaData);
                }

                if (list.isEmpty())
                    showEmptyPage();

                adapter = new ListOfAangadiasRecyclerViewAdapter(ListOfAangadias.this, list);
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


//    filter by user name
    private void FilterByUserName() {
        progressDialog.show();
        databaseReference.child("AangadiaProfile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(list!=null) {
                    list.clear();  // v v v v important (eliminate duplication of data)
                }

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    RecyclerViewListAangadiaData aangadiaData = postSnapshot.getValue(RecyclerViewListAangadiaData.class);
                  //  ("raky", "user: "+aangadiaData.getUserName());
                    String str = aangadiaData.getUserName();
                    String findStr = name_tx;
                    if (str.split(findStr, -1).length-1 > 0) list.add(aangadiaData);
                }

                if (list.isEmpty())
                    showEmptyPage();

                adapter = new ListOfAangadiasRecyclerViewAdapter(ListOfAangadias.this, list);
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


//    whole data
    public void LoadData(){

        progressDialog.show();
        databaseReference.child("AangadiaProfile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(list!=null) {
                    list.clear();  // v v v v important (eliminate duplication of data)
                }

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    RecyclerViewListAangadiaData aangadiaData = postSnapshot.getValue(RecyclerViewListAangadiaData.class);

                    list.add(aangadiaData);
                }

                if (list.isEmpty())
                    showEmptyPage();

                adapter = new ListOfAangadiasRecyclerViewAdapter(ListOfAangadias.this, list);
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

    public void enterEitherUidOrName(){
        try{
            new MaterialDialog.Builder(ListOfAangadias.this)
                    .title("Empty Search...")
                    .titleColor(Color.WHITE)
                    .content("Provide either uid or name or both")
                    .icon(getResources().getDrawable(R.drawable.ic_warning))
                    .contentColor(getResources().getColor(R.color.lightCoral))
                    .backgroundColor(getResources().getColor(R.color.black90))
                    .positiveText(R.string.ok)
                    .show();
        }
        catch (Exception e){
//            exception
        }

    }

//    show empty page
    public void showEmptyPage(){
        try{
            new MaterialDialog.Builder(ListOfAangadias.this)
                    .title("Empty!")
                    .titleColor(Color.BLACK)
                    .content("No Data Available")
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
    //end

    public void onDestroy(){
        super.onDestroy();
        if (progressDialog.isShowing()) progressDialog.dismiss();
    }

}
