package com.donotauthenticatemyapp.teamaccountmanager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import com.afollestad.materialdialogs.MaterialDialog;

public class NoInternetConnectionAlert {

    private Context context;

    NoInternetConnectionAlert(Context context){
        this.context = context;
    }

    public void DisplayNoInternetConnection(){
        try {
            new MaterialDialog.Builder(context)
                    .title("No Internet Access!")
                    .titleColor(Color.BLACK)
                    .content("No internet connectivity detected. Please make sure have working internet connection and try again.")
                    .icon(context.getResources().getDrawable(R.drawable.ic_no_internet_connection))
                    .contentColor(context.getResources().getColor(R.color.black))
                    .backgroundColor(context.getResources().getColor(R.color.white))
                    .positiveColor(context.getResources().getColor(R.color.red))
                    .positiveText(R.string.ok)
                    .show();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

}
