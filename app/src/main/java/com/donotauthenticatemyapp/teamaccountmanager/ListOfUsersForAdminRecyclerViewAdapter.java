package com.donotauthenticatemyapp.teamaccountmanager;

//ListOfUsersForAdminRecyclerViewAdapter

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ListOfUsersForAdminRecyclerViewAdapter extends RecyclerView.Adapter<ListOfUsersForAdminRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<RecyclerViewListAangadiaData> MainImageUploadInfoList;
    private static final String USER_UID_PREF = "user_uid_pref";
    private static final String USER_UID = "user_uid";
    SharedPreferences sharedPreferences;


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


        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = context.getSharedPreferences(USER_UID_PREF, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(USER_UID, UploadInfo.getKey());
                editor.apply();
                try {
                    context.startActivity(new Intent(context, UserDetails.class));
                }
                catch (IllegalStateException e){
                    e.printStackTrace();
                }
            }
        });

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

        TextView name, uid;
        ImageButton button;

        ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.ufa_nameTextView);
            uid = itemView.findViewById(R.id.ufa_uidTextView);

            button = itemView.findViewById(R.id.ufa_forwardImageButton);

        }
    }
//end
}