package com.example.nemologic.mainactivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nemologic.categoryactivity.CategoryActivity;
import com.example.nemologic.R;
import com.example.nemologic.data.DataManager;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.SqlManager;
import com.example.nemologic.gameactivity.GameActivity;
import com.example.nemologic.optionactivity.OptionActivity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_continue = findViewById(R.id.btn_continue);
        Button btn_category = findViewById(R.id.btn_category);
        Button btn_option = findViewById(R.id.btn_option);

        final Intent categoryIntent = new Intent(this, CategoryActivity.class);
        final Intent gameIntent = new Intent(this, GameActivity.class);
        final Intent optionIntent = new Intent(this, OptionActivity.class);

        //DB를 갱신해 준다.
        DataManager.loadLevel(this);
        DataManager.loadCategory(this);

        SharedPreferences lastPlayPref = getSharedPreferences("LASTPLAY", MODE_PRIVATE);

        //마지막 플레이한 레벨의 이름과 카테고리를 받아온다.
        final String lastPlayName = lastPlayPref.getString("name", "");
        gameIntent.putExtra("name", lastPlayName);
        final String lastPlayCategory = lastPlayPref.getString("category", "");
        gameIntent.putExtra("category", lastPlayCategory);

        DbOpenHelper mDbOpenHelper = new DbOpenHelper(this);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mDbOpenHelper.create();

        Cursor cursor;

        TextView tv_item_level_name = findViewById(R.id.tv_item_level_name);

        TextView tv_item_level_size = findViewById(R.id.tv_item_level_size);

        if(lastPlayName.isEmpty() || lastPlayCategory.isEmpty())
        {

        }
        else
        {
            cursor =  mDbOpenHelper.getLevelCursorByCategoryAndName(lastPlayCategory, lastPlayName);

            cursor.moveToNext();
            int lastPlayWidth = cursor.getInt(cursor.getColumnIndex(SqlManager.CreateLevelDB.WIDTH));
            int lastPlayHeight = cursor.getInt(cursor.getColumnIndex(SqlManager.CreateLevelDB.HEIGHT));



            if(cursor.getCount() > 0)
            {
                tv_item_level_name.setText(lastPlayName);
                tv_item_level_size.setText(lastPlayWidth + " X " + lastPlayHeight);
            }
        }


        mDbOpenHelper.close();

        btn_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(categoryIntent);
            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!lastPlayCategory.isEmpty() && !lastPlayName.isEmpty())
                    startActivity(gameIntent);
            }
        });

        btn_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(optionIntent, 1);
            }
        });

    }
}