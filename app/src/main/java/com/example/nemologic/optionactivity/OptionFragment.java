package com.example.nemologic.optionactivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.fragment.app.Fragment;

import com.example.nemologic.R;

import static android.content.Context.MODE_PRIVATE;

public class OptionFragment extends Fragment {

    private Context ctx;
    CheckBox cb_smartDrag;
    CheckBox cb_oneLineDrag;
    CheckBox cb_autoX;

    Button btn_accept;
    Button btn_cancel;

    public OptionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_option, container, false);
        final Context thisContext = fragmentView.getContext();

        cb_smartDrag = fragmentView.findViewById(R.id.cb_smartdrag);
        cb_oneLineDrag = fragmentView.findViewById(R.id.cb_oneline);
        cb_autoX = fragmentView.findViewById(R.id.cb_autox);

        btn_accept = fragmentView.findViewById(R.id.btn_accept);
        btn_cancel = fragmentView.findViewById(R.id.btn_cancel);

        SharedPreferences optionPref = thisContext.getSharedPreferences("OPTION", MODE_PRIVATE);
        boolean smartDrag = optionPref.getBoolean("smartDrag", true);
        boolean oneLineDrag = optionPref.getBoolean("oneLineDrag", true);
        boolean autoX = optionPref.getBoolean("autoX", false);

        cb_smartDrag.setChecked(smartDrag);
        cb_oneLineDrag.setChecked(oneLineDrag);
        cb_autoX.setChecked(autoX);

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences optionPref = thisContext.getSharedPreferences("OPTION", MODE_PRIVATE);

                SharedPreferences.Editor editor = optionPref.edit();
                editor.putBoolean("smartDrag", cb_smartDrag.isChecked());
                editor.putBoolean("oneLineDrag", cb_oneLineDrag.isChecked());
                editor.putBoolean("autoX", cb_autoX.isChecked());

                editor.apply();

                getFragmentManager().popBackStack();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        return fragmentView;
    }
}