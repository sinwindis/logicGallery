package com.example.nemologic.optionactivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nemologic.R;

public class OptionActivity extends AppCompatActivity {


    CheckBox cb_smartDrag;
    CheckBox cb_oneLineDrag;
    CheckBox cb_autoX;

    Button btn_accept;
    Button btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        cb_smartDrag = findViewById(R.id.cb_smartdrag);
        cb_oneLineDrag = findViewById(R.id.cb_oneline);
        cb_autoX = findViewById(R.id.cb_autox);

        btn_accept = findViewById(R.id.btn_accept);
        btn_cancel = findViewById(R.id.btn_cancel);

        SharedPreferences optionPref = getSharedPreferences("OPTION", MODE_PRIVATE);
        boolean smartDrag = optionPref.getBoolean("smartDrag", false);
        boolean oneLineDrag = optionPref.getBoolean("oneLineDrag", false);
        boolean autoX = optionPref.getBoolean("autoX", false);

        cb_smartDrag.setChecked(smartDrag);
        cb_oneLineDrag.setChecked(oneLineDrag);
        cb_autoX.setChecked(autoX);

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences optionPref = getSharedPreferences("OPTION", MODE_PRIVATE);

                SharedPreferences.Editor editor = optionPref.edit();
                editor.putBoolean("smartDrag", cb_smartDrag.isChecked());
                editor.putBoolean("oneLineDrag", cb_oneLineDrag.isChecked());
                editor.putBoolean("autoX", cb_autoX.isChecked());

                editor.apply();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //옵션 저장할지 물어보기
    }


}