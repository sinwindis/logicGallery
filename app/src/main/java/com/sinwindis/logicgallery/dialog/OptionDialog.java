package com.sinwindis.logicgallery.dialog;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AlertDialog;

import com.sinwindis.logicgallery.R;
import com.sinwindis.logicgallery.data.sharedpref.OptionPreference;

import static android.content.Context.MODE_PRIVATE;

public class OptionDialog {

    public AlertDialog dialog;
    private OptionPreference optionPreference;


    public OptionDialog() {

    }

    public void makeDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_option, null);
        builder.setView(view);

        final CheckBox cb_smartDrag = view.findViewById(R.id.cb_smartdrag);
        final CheckBox cb_oneLineDrag = view.findViewById(R.id.cb_oneline);
        final CheckBox cb_autoX = view.findViewById(R.id.cb_autox);

        optionPreference = new OptionPreference(activity);

        boolean smartDrag = optionPreference.isSmartDrag();
        boolean oneLineDrag = optionPreference.isOneLineDrag();
        boolean autoX = optionPreference.isAutoX();

        cb_smartDrag.setChecked(smartDrag);
        cb_oneLineDrag.setChecked(oneLineDrag);
        cb_autoX.setChecked(autoX);

        Button btn_accept = view.findViewById(R.id.btn_accept);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);

        dialog = builder.create();
        btn_accept.setOnClickListener(v -> {
            optionPreference.setSmartDrag(cb_smartDrag.isChecked());
            optionPreference.setOneLineDrag(cb_oneLineDrag.isChecked());
            optionPreference.setAutoX(cb_autoX.isChecked());
            dialog.dismiss();
        });

        btn_cancel.setOnClickListener(v -> dialog.dismiss());
    }
}