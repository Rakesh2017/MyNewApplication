package com.donotauthenticatemyapp.teamaccountmanager;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class PassbookListRecyclerViewAdapter extends RecyclerView.Adapter<PassbookListRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<RecyclerViewListAangadiaData> MainImageUploadInfoList;
    private static final String USER_UID_PREF = "user_uid_pref";
    private static final String USER_UID = "user_uid";
    private static final String USER_ID_DETAIL = "user_id_detail";
    private static final String USER_NAME_DETAIL = "user_name_detail";
    SharedPreferences sharedPreferences;
    private int delay = 100;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    String dateTime_tx, balanceCredited_tx, mode_tx, transactionBy_tx, previousBalance_tx
            , aangadiaKey_tx, balanceAfterTransaction_tx, commission_tx, serialNumber_tx, transactionID_tx, remarks_tx;

    PassbookListRecyclerViewAdapter(Context context, List<RecyclerViewListAangadiaData> TempList) {

        this.MainImageUploadInfoList = TempList;

        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_list_passbook, parent, false);

        return new ViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final RecyclerViewListAangadiaData UploadInfo = MainImageUploadInfoList.get(position);

        holder.setIsRecyclable(false);

        holder.serialNumber_tv.setText("Sr No: "+String.valueOf(position+1));

        dateTime_tx = UploadInfo.getDateTime();
        mode_tx = UploadInfo.getMode();
        transactionID_tx = UploadInfo.getTransaction_id();

        holder.dateTime_tv.setText(dateTime_tx);
        holder.transactionID_tv.setText(transactionID_tx);
        holder.serialNumber_tv.setText("Sr No: "+String.valueOf(position+1));


//        if mode is money Add
        if(TextUtils.equals(mode_tx, "moneyAdd")) {
            holder.remarks_tv.setVisibility(View.GONE);
            holder.remarks1_tv.setVisibility(View.GONE);
            holder.commission_tv.setVisibility(View.GONE);
            holder.commission1_tv.setVisibility(View.GONE);
            holder.balanceCredited1_tv.setText("Credit: ");
            balanceCredited_tx = UploadInfo.getMoney_added();
            transactionBy_tx = UploadInfo.getMoney_added_by();
            previousBalance_tx = UploadInfo.getPrevious_balance();
            aangadiaKey_tx = UploadInfo.getAangadia_key();
            balanceAfterTransaction_tx = UploadInfo.getCurrent_balance();



            if (!TextUtils.isEmpty(balanceCredited_tx)) {
                NumberFormat formatter = new DecimalFormat("#,###");
                String formatted_balance = formatter.format(Long.parseLong(balanceCredited_tx));
                holder.balanceCredited_tv.setText("Rs " + formatted_balance);
            } else {
                holder.balanceCredited_tv.setText("Rs 0.00");
            }

            if (!TextUtils.isEmpty(balanceAfterTransaction_tx)) {
                NumberFormat formatter = new DecimalFormat("#,###");
                String formatted_balance = formatter.format(Long.parseLong(balanceAfterTransaction_tx));
                holder.balanceAfterTransaction_tv.setText("Rs " + formatted_balance);
            } else {
                holder.balanceAfterTransaction_tv.setText("Rs 0.00");
            }

            String BALANCE_ADD = "Balance Add";
            holder.mode_tv.setText(BALANCE_ADD);
            holder.balanceCredited_tv.setTextColor(Objects.requireNonNull(context).getResources().getColor(R.color.green));

            if (!TextUtils.isEmpty(previousBalance_tx)) {
                NumberFormat formatter = new DecimalFormat("#,###");
                String formatted_balance = formatter.format(Long.parseLong(previousBalance_tx));
                holder.previousBalance_tv.setText("Rs " + formatted_balance);
            } else {
                holder.previousBalance_tv.setText("Rs 0.00");
            }


            if (TextUtils.equals(transactionBy_tx, "aangadia") && !TextUtils.isEmpty(transactionBy_tx)){
                holder.transactionBy_tv0.setText("Credited by: ");
                databaseReference.child("AangadiaProfile").child(aangadiaKey_tx)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String name = dataSnapshot.child("userName").getValue(String.class);
                                String uid = dataSnapshot.child("uid").getValue(String.class);

                                holder.transactionBy_tv.setText("Aangadia"+ " (" + uid +", "+name+")");
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

            else if ((TextUtils.equals(transactionBy_tx, "admin") && !TextUtils.isEmpty(transactionBy_tx))){
                holder.transactionBy_tv0.setText("Credited by: ");
                holder.transactionBy_tv.setText("Owner");
            }

        }// if mode is money add


//        if mode is credit
        else if (TextUtils.equals(mode_tx, "credit")){
            holder.remarks_tv.setVisibility(View.VISIBLE);
            holder.remarks1_tv.setVisibility(View.VISIBLE);
            transactionBy_tx = UploadInfo.getSender_key();
            balanceCredited_tx = UploadInfo.getBalance_credited();
            previousBalance_tx = UploadInfo.getCurrent_balance();
            balanceAfterTransaction_tx = UploadInfo.getBalance_after_credit();
            commission_tx = UploadInfo.getCommission();
            String commission_rate = UploadInfo.getCommission_rate();

            databaseReference.child("userProfile")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String uid = dataSnapshot.child(transactionBy_tx).child("uid").getValue(String.class);
                            String name = dataSnapshot.child(transactionBy_tx).child("userName").getValue(String.class);
                            holder.transactionBy_tv.setText(uid+", "+name);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            holder.balanceCredited1_tv.setText("Credit: ");
            databaseReference.child("transaction_remarks").child("remarks")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                if (dataSnapshot.hasChild(transactionID_tx)){
                                    String remarks = dataSnapshot.child(transactionID_tx).getValue(String.class);
                                    if (!TextUtils.isEmpty(remarks))
                                        holder.remarks_tv.setText(remarks);
                                    else {
                                        holder.remarks_tv.setTextColor(Color.GRAY);
                                        holder.remarks_tv.setText("NO REMARKS");
                                    }

                                }
                                else{
                                    holder.remarks_tv.setTextColor(Color.GRAY);
                                    holder.remarks_tv.setText("NO REMARKS");
                                }

                            }
                            catch (DatabaseException e){
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            holder.transactionBy_tv0.setHint("Credited By: ");


            String credit = "Credit";
            holder.mode_tv.setText(credit);
            holder.balanceCredited_tv.setTextColor(Objects.requireNonNull(context).getResources().getColor(R.color.green));

            if (!TextUtils.isEmpty(balanceCredited_tx)) {
                NumberFormat formatter = new DecimalFormat("#,###");
                String formatted_balance = formatter.format(Long.parseLong(balanceCredited_tx));
                holder.balanceCredited_tv.setText("Rs " + formatted_balance);
            } else {
                holder.balanceCredited_tv.setText("Rs 0.00");
            }

            if (!TextUtils.isEmpty(balanceAfterTransaction_tx)) {
                NumberFormat formatter = new DecimalFormat("#,###");
                String formatted_balance = formatter.format(Long.parseLong(balanceAfterTransaction_tx));
                holder.balanceAfterTransaction_tv.setText("Rs " + formatted_balance);
            } else {
                holder.balanceAfterTransaction_tv.setText("Rs 0.00");
            }

            if (!TextUtils.isEmpty(previousBalance_tx)) {
                NumberFormat formatter = new DecimalFormat("#,###");
                String formatted_balance = formatter.format(Long.parseLong(previousBalance_tx));
                holder.previousBalance_tv.setText("Rs " + formatted_balance);
            } else {
                holder.previousBalance_tv.setText("Rs 0.00");
            }
            if (!TextUtils.isEmpty(commission_tx)) {
                NumberFormat formatter = new DecimalFormat("#,###");
                String formatted_balance = formatter.format(Long.parseLong(commission_tx));
                holder.commission_tv.setText("Rs " + formatted_balance+" ("+commission_rate+"%)");
            } else {
                holder.commission_tv.setText("Rs 0 (0%)");
            }

        }


        //        if mode is debit
        else if (TextUtils.equals(mode_tx, "debit")) {
            holder.remarks_tv.setVisibility(View.VISIBLE);
            holder.remarks1_tv.setVisibility(View.VISIBLE);
            holder.transactionBy_tv0.setVisibility(View.VISIBLE);
            holder.transactionBy_tv.setVisibility(View.VISIBLE);

            holder.transactionBy_tv0.setHint("Debited To: ");

            transactionBy_tx = UploadInfo.getReceiver_key();
            balanceCredited_tx = UploadInfo.getBalance_debited();
            previousBalance_tx = UploadInfo.getCurrent_balance();
            balanceAfterTransaction_tx = UploadInfo.getBalance_after_debit();
            commission_tx = UploadInfo.getCommission();
            String commission_rate = UploadInfo.getCommission_rate();

            databaseReference.child("userProfile")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String uid = dataSnapshot.child(transactionBy_tx).child("uid").getValue(String.class);
                            String name = dataSnapshot.child(transactionBy_tx).child("userName").getValue(String.class);
                            holder.transactionBy_tv.setText(uid+", "+name);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            holder.balanceCredited1_tv.setText("Debit: ");
            databaseReference.child("transaction_remarks").child("remarks")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                if (dataSnapshot.hasChild(transactionID_tx)){
                                    String remarks = dataSnapshot.child(transactionID_tx).getValue(String.class);
                                    if (!TextUtils.isEmpty(remarks))
                                        holder.remarks_tv.setText(remarks);
                                    else {
                                        holder.remarks_tv.setTextColor(Color.GRAY);
                                        holder.remarks_tv.setText("NO REMARKS");
                                    }

                                }
                                else{
                                    holder.remarks_tv.setTextColor(Color.GRAY);
                                    holder.remarks_tv.setText("NO REMARKS");
                                }

                            }
                            catch (DatabaseException e){
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


            String debit = "Debit";
            holder.mode_tv.setText(debit);
            holder.balanceCredited_tv.setTextColor(Objects.requireNonNull(context).getResources().getColor(R.color.red));

            if (!TextUtils.isEmpty(balanceCredited_tx)) {
                NumberFormat formatter = new DecimalFormat("#,###");
                String formatted_balance = formatter.format(Long.parseLong(balanceCredited_tx));
                holder.balanceCredited_tv.setText("Rs " + formatted_balance);
            } else {
                holder.balanceCredited_tv.setText("Rs 0.00");
            }

            if (!TextUtils.isEmpty(balanceAfterTransaction_tx)) {
                NumberFormat formatter = new DecimalFormat("#,###");
                String formatted_balance = formatter.format(Long.parseLong(balanceAfterTransaction_tx));
                holder.balanceAfterTransaction_tv.setText("Rs " + formatted_balance);
            } else {
                holder.balanceAfterTransaction_tv.setText("Rs 0.00");
            }

            if (!TextUtils.isEmpty(previousBalance_tx)) {
                NumberFormat formatter = new DecimalFormat("#,###");
                String formatted_balance = formatter.format(Long.parseLong(previousBalance_tx));
                holder.previousBalance_tv.setText("Rs " + formatted_balance);
            } else {
                holder.previousBalance_tv.setText("Rs 0.00");
            }
            if (!TextUtils.isEmpty(commission_tx)) {
                NumberFormat formatter = new DecimalFormat("#,###");
                String formatted_balance = formatter.format(Long.parseLong(commission_tx));
                holder.commission_tv.setText("Rs " + formatted_balance + " (" + commission_rate + "%)");
            } else {
                holder.commission_tv.setText("Rs 0 (0%)");
            }

        }

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
        return MainImageUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView dateTime_tv, balanceCredited_tv, mode_tv, transactionBy_tv
                , transactionBy_tv0, previousBalance_tv, balanceAfterTransaction_tv, commission_tv, commission1_tv
                ,serialNumber_tv, transactionID_tv, remarks_tv, remarks1_tv, balanceCredited1_tv;;

        ViewHolder(View itemView) {
            super(itemView);

            dateTime_tv = itemView.findViewById(R.id.lpx_dateTimeTextView);
            balanceCredited_tv = itemView.findViewById(R.id.lpx_balanceAddedTextView);
            balanceCredited1_tv = itemView.findViewById(R.id.lpx_balanceAdded);
            mode_tv = itemView.findViewById(R.id.lpx_modeTextView);
            transactionBy_tv = itemView.findViewById(R.id.lpx_moneyAddedByTextView);
            transactionBy_tv0 = itemView.findViewById(R.id.lpx_moneyAddedBy);
            previousBalance_tv = itemView.findViewById(R.id.lpx_previousBalanceTextView);
            balanceAfterTransaction_tv = itemView.findViewById(R.id.lpx_balanceAfterTransactionTextView);
            commission_tv = itemView.findViewById(R.id.lpx_commissionTextView);
            commission1_tv = itemView.findViewById(R.id.lpx_commission);
            transactionID_tv = itemView.findViewById(R.id.lpx_transactionIDTextView);
            serialNumber_tv = itemView.findViewById(R.id.lpx_serialNumberTextView);
            remarks_tv = itemView.findViewById(R.id.lpx_remarksTextView);
            remarks1_tv = itemView.findViewById(R.id.lpx_remarks);

        }
    }
}
//end
