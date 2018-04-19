package com.donotauthenticatemyapp.teamaccountmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ListOfAangadiasRecyclerViewAdapter extends RecyclerView.Adapter<ListOfAangadiasRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<RecyclerViewListAangadiaData> MainImageUploadInfoList;
    private static final String AANGADIA_UID_PREF = "aangadia_uid_pref";
    private static final String AANGADIA_UID = "aangadia_uid";
    SharedPreferences sharedPreferences;


    ListOfAangadiasRecyclerViewAdapter(Context context, List<RecyclerViewListAangadiaData> TempList) {

        this.MainImageUploadInfoList = TempList;

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
        final RecyclerViewListAangadiaData UploadInfo = MainImageUploadInfoList.get(position);

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

            name = itemView.findViewById(R.id.rla_nameTextView);
            uid = itemView.findViewById(R.id.rla_uidTextView);

            button = itemView.findViewById(R.id.rla_forwardImageButton);

        }
    }
//end
}