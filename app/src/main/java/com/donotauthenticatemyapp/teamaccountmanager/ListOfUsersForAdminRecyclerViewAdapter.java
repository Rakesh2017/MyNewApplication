package com.donotauthenticatemyapp.teamaccountmanager;

//ListOfUsersForAdminRecyclerViewAdapter

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
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

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ListOfUsersForAdminRecyclerViewAdapter extends RecyclerView.Adapter<ListOfUsersForAdminRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<RecyclerViewListAangadiaData> MainImageUploadInfoList;
    private static final String USER_UID_PREF = "user_uid_pref";
    private static final String USER_UID = "user_uid";
    private static final String USER_ID_DETAIL = "user_id_detail";
    private static final String USER_NAME_DETAIL = "user_name_detail";
    SharedPreferences sharedPreferences;

    private int delay = 100;

    ListOfUsersForAdminRecyclerViewAdapter(Context context, List<RecyclerViewListAangadiaData> TempList) {

        this.MainImageUploadInfoList = TempList;

        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_list_of_users_for_admin, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final RecyclerViewListAangadiaData UploadInfo = MainImageUploadInfoList.get(position);

        holder.name.setText(UploadInfo.getUserName());
        holder.uid.setText(UploadInfo.getUid());

        if (TextUtils.equals(UploadInfo.getCreated_by(), "admin")) holder.createdBy.setText("Admin");
        else if (TextUtils.equals(UploadInfo.getCreated_by(), "aangadia")){
            holder.aangadiaName.setVisibility(View.VISIBLE);
            holder.aangadiaName1.setVisibility(View.VISIBLE);
            holder.createdBy.setText("Aangadia");
            holder.aangadiaName.setText(UploadInfo.getAangadia_userName());
        }

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = context.getSharedPreferences(USER_UID_PREF, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(USER_UID, UploadInfo.getKey());
                editor.putString(USER_ID_DETAIL, UploadInfo.getUid());
                editor.putString(USER_NAME_DETAIL, UploadInfo.getUserName());
                editor.apply();
                try {
                    context.startActivity(new Intent(context, UserDetails.class));
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

        //adding padding to last cardview
        if( position == getItemCount() -1){
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins((int) context.getResources().getDimension(R.dimen.activity_horizontal_margin)
                    ,(int) context.getResources().getDimension(R.dimen.activity_horizontal_margin),
                    (int) context.getResources().getDimension(R.dimen.activity_horizontal_margin),
                    (int) context.getResources().getDimension(R.dimen.activity_horizontal_margin));
            holder.cardView.setLayoutParams(params);
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

        TextView name, uid, createdBy, aangadiaName, aangadiaName1 ;
        ImageButton button;

        CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.ufa_nameTextView);
            uid = itemView.findViewById(R.id.ufa_uidTextView);
            createdBy = itemView.findViewById(R.id.ufa_createdByTextView);
            aangadiaName = itemView.findViewById(R.id.ufa_aangadiaNameTextView);
            aangadiaName1 = itemView.findViewById(R.id.ufa_aangadiaNameTextView1);
            cardView = itemView.findViewById(R.id.ufa_cardview);

            button = itemView.findViewById(R.id.ufa_forwardImageButton);

        }
    }
//end
}