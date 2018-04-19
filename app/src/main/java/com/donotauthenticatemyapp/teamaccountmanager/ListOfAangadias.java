package com.donotauthenticatemyapp.teamaccountmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListOfAangadias extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;
    List<RecyclerViewListAangadiaData> list = new ArrayList<>();

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ImageButton back_ib;
    ProgressDialog progressDialog;

    LinearLayoutManager recyclerViewLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_aangadias);

        recyclerView = findViewById(R.id.loa_recyclerView);
        recyclerView.setHasFixedSize(true);

        recyclerViewLayoutManager = new LinearLayoutManager(ListOfAangadias.this);


        progressDialog = new ProgressDialog(ListOfAangadias.this);
        progressDialog.setMessage("Loading Data...");

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        back_ib = findViewById(R.id.loa_backImageButton);
        back_ib.setOnClickListener(this);

        LoadData();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.loa_backImageButton){
            onBackPressed();
        }
    }


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

    //end
}
