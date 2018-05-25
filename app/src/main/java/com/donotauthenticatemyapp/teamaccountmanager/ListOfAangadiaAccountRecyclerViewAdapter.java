package com.donotauthenticatemyapp.teamaccountmanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class ListOfAangadiaAccountRecyclerViewAdapter extends RecyclerView.Adapter<ListOfAangadiaAccountRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<RecyclerViewListAangadiaData> UploadInfoList;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


    private int delay = 100;

    ListOfAangadiaAccountRecyclerViewAdapter(Context context, List<RecyclerViewListAangadiaData> TempList) {

        this.UploadInfoList = TempList;

        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_aangadia_account_transactions, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final RecyclerViewListAangadiaData UploadInfo = UploadInfoList.get(position);

        holder.dateTime_tv.setText(UploadInfo.getDateTime());
        holder.transactionIDtv.setText(UploadInfo.getTransaction_id());
        holder.serialNumber_tv.setText(String.valueOf(position+1));

        if (!TextUtils.isEmpty(UploadInfo.getMoney_added())){
            NumberFormat formatter = new DecimalFormat("#,###");
            String formatted_balance = formatter.format(Long.parseLong(UploadInfo.getMoney_added()));
            holder.amount_tv.setText("Rs "+formatted_balance);
        }

        databaseReference.child("userProfile")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = null, uid = null;
                        try {
                            name = dataSnapshot.child(UploadInfo.getUser_key()).child("userName").getValue(String.class);
                            uid = dataSnapshot.child(UploadInfo.getUser_key()).child("uid").getValue(String.class);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                        holder.user_tv.setText(name+", "+uid);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        YoYo.with(Techniques.ZoomIn)
                .duration(delay)
                .repeat(0)
                .playOn(holder.cardView);

        delay+=100;
        if (delay == 500) delay = 100;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return UploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView user_tv, amount_tv, dateTime_tv, transactionIDtv, serialNumber_tv;

        CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);

            user_tv = itemView.findViewById(R.id.rva_userTextView);
            amount_tv = itemView.findViewById(R.id.rva_balanceCreditedTextView);
            dateTime_tv = itemView.findViewById(R.id.rva_dateTextView);
            cardView = itemView.findViewById(R.id.rva_cardview);
            transactionIDtv = itemView.findViewById(R.id.rva_transactionIDTextView);
            serialNumber_tv = itemView.findViewById(R.id.rva_serialNumberTextView);


        }
    }

//end
}