package com.donotauthenticatemyapp.teamaccountmanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;


public class ListOfUserTransactionsRecyclerViewAdapter extends RecyclerView.Adapter<ListOfUserTransactionsRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<RecyclerViewListAangadiaData> MainImageUploadInfoList;


    private final String BALANCE_ADD = "Balance Add";
    private int delay = 100;

    ListOfUserTransactionsRecyclerViewAdapter(Context context, List<RecyclerViewListAangadiaData> TempList) {

        this.MainImageUploadInfoList = TempList;

        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_list_user_transactions, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final RecyclerViewListAangadiaData UploadInfo = MainImageUploadInfoList.get(position);

        holder.date_tv.setText(UploadInfo.getDateTime());
        if (TextUtils.equals(UploadInfo.getMode(),"moneyAdd")){
            holder.modeOFTransaction_tv.setText(BALANCE_ADD);
            holder.balanceCredited_tv.setTextColor(context.getResources().getColor(R.color.green));
        }
        final String total_balance = UploadInfo.getMoney_added();
        if (!TextUtils.isEmpty(total_balance)){
            NumberFormat formatter = new DecimalFormat("#,###");
            String formatted_balance = formatter.format(Long.parseLong(total_balance));
            holder.balanceCredited_tv.setText("Rs "+formatted_balance);
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

        TextView date_tv, balanceCredited_tv, modeOFTransaction_tv;
        CardView cardView;
        RelativeLayout relativeLayout;

        ViewHolder(View itemView) {
            super(itemView);

            date_tv = itemView.findViewById(R.id.lut_dateTextView);
            balanceCredited_tv = itemView.findViewById(R.id.lut_balanceCreditedTextView);
            modeOFTransaction_tv = itemView.findViewById(R.id.lut_modeOfTransactionTextView);

            cardView = itemView.findViewById(R.id.lut_cardview);
            relativeLayout = itemView.findViewById(R.id.lut_relativeLayout);
        }
    }
//end
}