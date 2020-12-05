package com.sinwindis.logicgallery.mainactivity;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.sinwindis.logicgallery.R;

public class RewardDialog {

    public AlertDialog dialog;

    public RewardDialog() {

    }

    public void makeDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_reward, null);
        builder.setView(view);

        dialog = builder.create();

        Button btn_accept = view.findViewById(R.id.btn_accept);

        btn_accept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}