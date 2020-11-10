package com.example.nemologic.loadactivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nemologic.R;
import com.example.nemologic.data.DbManager;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.StringGetter;
import com.example.nemologic.mainactivity.MainActivity;

import java.sql.SQLException;

public class LoadActivity extends AppCompatActivity {

    private void load()
    {
        DbOpenHelper mDbOpenHelper = new DbOpenHelper(this);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mDbOpenHelper.create();
        mDbOpenHelper.close();

        DbManager dbManger = new DbManager(this);

        dbManger.loadData();
        StringGetter.loadData(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);





        Handler loadHandler = new Handler();

        Handler mainHandler = new Handler();


        loadHandler.post(new Runnable() {
            @Override
            public void run() {
                load();

                Intent intent = new Intent(LoadActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}