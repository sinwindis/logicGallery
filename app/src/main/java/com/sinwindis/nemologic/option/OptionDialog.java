package com.sinwindis.nemologic.option;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AlertDialog;

import com.sinwindis.nemologic.R;

import static android.content.Context.MODE_PRIVATE;

public class OptionDialog {

    public AlertDialog dialog;

    public OptionDialog()
    {

    }

    public void makeDialog(Activity activity)
    {
        final Activity finalActivity = activity;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_option, null);
        builder.setView(view);

        final CheckBox cb_smartDrag = view.findViewById(R.id.cb_smartdrag);
        final CheckBox cb_oneLineDrag = view.findViewById(R.id.cb_oneline);
        final CheckBox cb_autoX = view.findViewById(R.id.cb_autox);

        SharedPreferences optionPref = activity.getSharedPreferences("OPTION", MODE_PRIVATE);
        boolean smartDrag = optionPref.getBoolean("smartDrag", true);
        boolean oneLineDrag = optionPref.getBoolean("oneLineDrag", true);
        boolean autoX = optionPref.getBoolean("autoX", false);

        cb_smartDrag.setChecked(smartDrag);
        cb_oneLineDrag.setChecked(oneLineDrag);
        cb_autoX.setChecked(autoX);

        Button btn_accept = view.findViewById(R.id.btn_accept);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);

        dialog = builder.create();
        btn_accept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences optionPref = finalActivity.getSharedPreferences("OPTION", MODE_PRIVATE);

                SharedPreferences.Editor editor = optionPref.edit();
                editor.putBoolean("smartDrag", cb_smartDrag.isChecked());
                editor.putBoolean("oneLineDrag", cb_oneLineDrag.isChecked());
                editor.putBoolean("autoX", cb_autoX.isChecked());

                editor.apply();
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}