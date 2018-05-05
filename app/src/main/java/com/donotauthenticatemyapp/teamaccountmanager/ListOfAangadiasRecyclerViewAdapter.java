package com.donotauthenticatemyapp.teamaccountmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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

public class ListOfAangadiasRecyclerViewAdapter extends RecyclerView.Adapter<ListOfAangadiasRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<RecyclerViewListAangadiaData> UploadInfoList;
    private static final String AANGADIA_UID_PREF = "aangadia_uid_pref";
    private static final String AANGADIA_UID = "aangadia_uid";
    SharedPreferences sharedPreferences;

    private int delay = 100;

    ListOfAangadiasRecyclerViewAdapter(Context context, List<RecyclerViewListAangadiaData> TempList) {

        this.UploadInfoList = TempList;

        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_list_of_aangadias, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final RecyclerViewListAangadiaData UploadInfo = UploadInfoList.get(position);

        holder.name.setText(UploadInfo.getUserName());
        holder.uid.setText(UploadInfo.getUid());


        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = context.getSharedPreferences(AANGADIA_UID_PREF, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(AANGADIA_UID, UploadInfo.getKey());
                editor.apply();
                try {
                    context.startActivity(new Intent(context, AangadiaDetails.class));
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
        return UploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, uid;
        ImageButton button;
        CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.rla_nameTextView);
            uid = itemView.findViewById(R.id.rla_uidTextView);
            cardView = itemView.findViewById(R.id.rla_cardview);

            button = itemView.findViewById(R.id.rla_forwardImageButton);

        }
    }
//end
}