package com.example.nemologic.loadactivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nemologic.R;
import com.example.nemologic.data.DbManager;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.StringGetter;
import com.example.nemologic.mainactivity.MainActivity;

import java.sql.SQLException;

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