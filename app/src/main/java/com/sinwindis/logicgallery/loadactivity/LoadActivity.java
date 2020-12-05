package com.sinwindis.logicgallery.loadactivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.sinwindis.logicgallery.R;
import com.sinwindis.logicgallery.data.DbManager;
import com.sinwindis.logicgallery.data.DbOpenHelper;
import com.sinwindis.logicgallery.data.StringGetter;
import com.sinwindis.logicgallery.mainactivity.MainActivity;

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

                intentHandler.post(new Runnable() {
                    @Override
                    public void run() {
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