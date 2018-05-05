package com.donotauthenticatemyapp.teamaccountmanager;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;


public class ListOfUserTransactionsRecyclerViewAdapter extends RecyclerView.Adapter<ListOfUserTransactionsRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<RecyclerViewListAangadiaData> MainImageUploadInfoList;
    SharedPreferences transactionSharedPreferences;
    private final String transactionPref = "transactionPref";
    private final String AANGADIA_KEY = "aangadia_key";
    private final String CURRENT_BALANCE = "current_balance";
    private final String PREVIOUS_BALANCE = "previous_balance";
    private final String DATE_TIME = "date_time";
    private final String MODE = "mode";
    private final String MONEY_ADDED = "money_added";
    private final String MONEY_ADDED_BY = "money_added_by";
    private final String BALANCE_AFTER_DEBIT = "balance_after_debit";
    private final String BALANCE_AFTER_CREDIT = "balance_after_credit";
    private final String BALANCE_DEBITED = "balance_debited";
    private final String BALANCE_CREDITED = "balance_credited";
    private final String RECEIVER_KEY = "receiver_key";
    private final String SENDER_KEY = "sender_key";

    SharedPreferences userIdentifierSharedPreferences;

    private static final String USER_IDENTIFIER_PREF = "userIdentifierPref";
    private static final String USER_IDENTITY = "userIdentity";

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

        String money_added_to_account = null;

        holder.date_tv.setText(UploadInfo.getDateTime());


        //if money add
        if (TextUtils.equals(UploadInfo.getMode(),"moneyAdd")){
            String BALANCE_ADD = "Balance Add";
            holder.modeOFTransaction_tv.setText(BALANCE_ADD);
            holder.balanceCredited_tv.setTextColor(context.getResources().getColor(R.color.green));
            money_added_to_account = UploadInfo.getMoney_added();
        }

//        if debited
        else if (TextUtils.equals(UploadInfo.getMode(),"debit")){
            String DEBIT = "Debit";
            holder.modeOFTransaction_tv.setText(DEBIT);
            holder.balanceCredited_tv.setTextColor(context.getResources().getColor(R.color.red));
            money_added_to_account = UploadInfo.getBalance_debited();
        }

        //        if credited
        else if (TextUtils.equals(UploadInfo.getMode(),"credit")){
            String CREDIT = "Credit";
            holder.modeOFTransaction_tv.setText(CREDIT);
            holder.balanceCredited_tv.setTextColor(context.getResources().getColor(R.color.green));
            money_added_to_account = UploadInfo.getBalance_credited();
        }



        if (!TextUtils.isEmpty(money_added_to_account)){
            NumberFormat formatter = new DecimalFormat("#,###");
            String formatted_balance = formatter.format(Long.parseLong(money_added_to_account));
            holder.balanceCredited_tv.setText("Rs "+formatted_balance);
        }


        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transactionSharedPreferences = context.getSharedPreferences(transactionPref, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = transactionSharedPreferences.edit();

                try{

//                    if balance add
                    if (TextUtils.equals(UploadInfo.getMode(), "moneyAdd")){
                        editor.putString(MONEY_ADDED, UploadInfo.getMoney_added());
                        editor.putString(AANGADIA_KEY, UploadInfo.getAangadia_key());
                        editor.putString(MONEY_ADDED_BY, UploadInfo.getMoney_added_by());
                        editor.putString(PREVIOUS_BALANCE, UploadInfo.getPrevious_balance());
                    }
//credit
                    else if (TextUtils.equals(UploadInfo.getMode(), "credit")){
                        editor.putString(BALANCE_CREDITED, UploadInfo.getBalance_credited());
                        editor.putString(BALANCE_AFTER_CREDIT, UploadInfo.getBalance_after_credit());
                        editor.putString(SENDER_KEY, UploadInfo.getSender_key());
                    }

                    //debit
                    else if (TextUtils.equals(UploadInfo.getMode(), "credit")){
                        editor.putString(BALANCE_DEBITED, UploadInfo.getBalance_debited());
                        editor.putString(BALANCE_AFTER_DEBIT, UploadInfo.getBalance_after_debit());
                        editor.putString(RECEIVER_KEY, UploadInfo.getReceiver_key());
                    }


                    editor.putString(MODE, UploadInfo.getMode());
                    editor.putString(DATE_TIME, UploadInfo.getDateTime());

                    editor.apply();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    AppCompatActivity activity = (AppCompatActivity) context;
                    userIdentifierSharedPreferences = context.getSharedPreferences(USER_IDENTIFIER_PREF, Context.MODE_PRIVATE);
                    String identity = userIdentifierSharedPreferences.getString(USER_IDENTITY, "");

                    if (TextUtils.equals(identity, "aangadia") || TextUtils.equals(identity, "admin"))
                        activity.getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_user_details, new TransactionDetails()).addToBackStack("transactionDetails").commit();

                    else if (TextUtils.equals(identity, "user"))
                        activity.getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_user_home_page, new TransactionDetails()).addToBackStack("transactionDetails").commit();

                }
                catch (IllegalStateException e){
                    e.printStackTrace();
                }
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
        return MainImageUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView date_tv, balanceCredited_tv, modeOFTransaction_tv, balanceCredited;
        CardView cardView;
        RelativeLayout relativeLayout;
        ImageButton button;

        ViewHolder(View itemView) {
            super(itemView);

            date_tv = itemView.findViewById(R.id.lut_dateTextView);
            balanceCredited_tv = itemView.findViewById(R.id.lut_balanceCreditedTextView);
            balanceCredited = itemView.findViewById(R.id.lut_balanceCredited);
            modeOFTransaction_tv = itemView.findViewById(R.id.lut_modeOfTransactionTextView);

            cardView = itemView.findViewById(R.id.lut_cardview);
            relativeLayout = itemView.findViewById(R.id.lut_relativeLayout);
            button = itemView.findViewById(R.id.lut_nextButton);
        }
    }
//end
}