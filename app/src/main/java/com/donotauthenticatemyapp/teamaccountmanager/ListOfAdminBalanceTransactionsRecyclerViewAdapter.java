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


public class ListOfAdminBalanceTransactionsRecyclerViewAdapter extends RecyclerView.Adapter<ListOfAdminBalanceTransactionsRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<RecyclerViewListAangadiaData> UploadInfoList;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


    private int delay = 100;

    ListOfAdminBalanceTransactionsRecyclerViewAdapter(Context context, List<RecyclerViewListAangadiaData> TempList) {

        this.UploadInfoList = TempList;

        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_list_admin_balance_transactions, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final RecyclerViewListAangadiaData UploadInfo = UploadInfoList.get(position);

        holder.dateTime_tv.setText(UploadInfo.getDateTime());

        if (!TextUtils.isEmpty(UploadInfo.getTransaction_amount())){
            NumberFormat formatter = new DecimalFormat("#,###");
            String formatted_balance = formatter.format(Long.parseLong(UploadInfo.getTransaction_amount()));
            holder.amount_tv.setText("Rs "+formatted_balance);
        }


        if (!TextUtils.isEmpty(UploadInfo.getCommission())){
            NumberFormat formatter = new DecimalFormat("#,###");
            String formatted_balance = formatter.format(Long.parseLong(UploadInfo.getCommission()));
            holder.commission_tv.setText("Rs "+formatted_balance+" ("+UploadInfo.getCommission_rate()+"%)");
        }

        databaseReference.child("userProfile")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String sender_name = null, sender_uid = null, receiver_name = null, receiver_uid = null;
                        try {
                            sender_name = dataSnapshot.child(UploadInfo.getSender_key()).child("userName").getValue(String.class);
                            sender_uid = dataSnapshot.child(UploadInfo.getSender_key()).child("uid").getValue(String.class);
                            receiver_name = dataSnapshot.child(UploadInfo.getReceiver_key()).child("userName").getValue(String.class);
                            receiver_uid = dataSnapshot.child(UploadInfo.getReceiver_key()).child("uid").getValue(String.class);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                        holder.sender_tv.setText(sender_name+", "+sender_uid);
                        holder.receiver_tv.setText(receiver_name+", "+receiver_uid);

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

        TextView commission_tv, sender_tv, receiver_tv, amount_tv, dateTime_tv;
        CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);

            commission_tv = itemView.findViewById(R.id.td_commissionTextView);
            sender_tv = itemView.findViewById(R.id.abt_senderTextView);
            receiver_tv = itemView.findViewById(R.id.abt_receiverTextView);
            amount_tv = itemView.findViewById(R.id.abt_transactionAmountTextView);
            dateTime_tv = itemView.findViewById(R.id.abt_dateTextView);

            cardView = itemView.findViewById(R.id.abt_cardview);

        }
    }

//end
}