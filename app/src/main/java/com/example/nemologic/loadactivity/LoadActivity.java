package com.example.nemologic.loadactivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nemologic.R;
import com.example.nemologic.data.DbManager;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.StringGetter;
import com.example.nemologic.mainactivity.MainActivity;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        final Handler intentHandler = new Handler();

        Thread loadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Context ctx = getBaseContext();

                //날짜가 하루 이상 지났으면
                if(isDateChanged())
                {
                    //hint 1개 추가
                    addHint();
                }

                DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
                try {
                    mDbOpenHelper.open();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                mDbOpenHelper.create();
                mDbOpenHelper.close();

                DbManager dbManger = new DbManager(ctx);


                dbManger.loadData();
                StringGetter.loadData(ctx);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                intentHandler.post(new Runnable(){
                    @Override
                    public void run()
                    {
                        // UI 작업 수행 O
                        Intent intent = new Intent(LoadActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });

        loadThread.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void addHint()
    {
        SharedPreferences hintPref = getSharedPreferences("PROPERTY", MODE_PRIVATE);
        SharedPreferences.Editor editor = hintPref.edit();

        int hintCount = hintPref.getInt("hint", 4);
        editor.putInt("hint", hintCount + 1);
        editor.apply();
    }

    private boolean isDateChanged()
    {
        //현재 날짜 확인
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        String[] currentDate = formattedDate.split("-");


        //기존 날짜 확인
        SharedPreferences datePref = getSharedPreferences("PROPERTY", MODE_PRIVATE);
        String lastDateStr = datePref.getString("date", "00-00-0000");
        String[] lastDate = lastDateStr.split("-");

        SharedPreferences.Editor editor = datePref.edit();

        editor.putString("date", formattedDate);
        editor.apply();

        int lastYear = Integer.parseInt(lastDate[2]);
        int currentYear = Integer.parseInt(currentDate[2]);
        int lastMonth = Integer.parseInt(lastDate[1]);
        int currentMonth = Integer.parseInt(currentDate[1]);
        int lastDay = Integer.parseInt(lastDate[0]);
        int currentDay = Integer.parseInt(currentDate[0]);

        return (lastYear < currentYear || lastMonth < currentMonth || lastDay < currentDay);
    }
}