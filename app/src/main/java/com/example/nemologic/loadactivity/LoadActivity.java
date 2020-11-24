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
}