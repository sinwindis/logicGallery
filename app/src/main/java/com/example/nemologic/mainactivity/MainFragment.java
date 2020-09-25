package com.example.nemologic.mainactivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;
import com.example.nemologic.category.CategoryFragment;
import com.example.nemologic.data.DataManager;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.LevelData;
import com.example.nemologic.data.SqlManager;
import com.example.nemologic.game.GameFragment;
import com.example.nemologic.level.RvLevelBoardAdapter;
import com.example.nemologic.levelcreate.LevelCreateFragment;
import com.example.nemologic.option.OptionDialog;

import java.sql.SQLException;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment {

    Context ctx;
    LevelData lastPlayLevel;

    public MainFragment(Context ctx) {
        this.ctx = ctx;
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Context mainActivityCtx = ctx;

        final View fragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        Context ctx = fragmentView.getContext();

        ImageView img_continue = fragmentView.findViewById(R.id.img_continue);
        ConstraintLayout cl_category = fragmentView.findViewById(R.id.cl_category);
        ImageView img_option = fragmentView.findViewById(R.id.img_option);
        ImageView img_plus = fragmentView.findViewById(R.id.img_plus);

        img_option.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        view.setAlpha(0.5F);
                        break;
                    case MotionEvent.ACTION_UP:
                        view.setAlpha(1F);
                        OptionDialog optionDialog = new OptionDialog();

                        optionDialog.makeOptionDialog(getActivity());
                        optionDialog.dialog.show();
                }
                return false;
            }
        });

        img_option.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

            }
        });

        img_plus.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        view.setAlpha(0.5F);
                        break;
                    case MotionEvent.ACTION_UP:
                        view.setAlpha(1F);
                        ((MainActivity) Objects.requireNonNull(getActivity())).fragmentMove(new LevelCreateFragment(mainActivityCtx));

                }
                return false;
            }
        });

        img_plus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

            }
        });

        //DB를 갱신해 준다.
        DataManager.loadLevel(ctx);
        DataManager.loadCategory(ctx);

        SharedPreferences lastPlayPref = ctx.getSharedPreferences("LASTPLAY", MODE_PRIVATE);

        //마지막 플레이한 레벨의 이름과 카테고리를 받아온다.
        final String lastPlayName = lastPlayPref.getString("name", "");

        final String lastPlayCategory = lastPlayPref.getString("category", "");


        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mDbOpenHelper.create();

        Cursor cursor;

        TextView tv_item_level_name = fragmentView.findViewById(R.id.tv_item_level_name);

        TextView tv_item_level_size = fragmentView.findViewById(R.id.tv_item_level_size);

        assert lastPlayName != null;
        assert lastPlayCategory != null;
        
        if(!lastPlayName.isEmpty() && !lastPlayCategory.isEmpty())
        {
            cursor =  mDbOpenHelper.getLevelCursorByCategoryAndName(lastPlayCategory, lastPlayName);

            if(cursor.getCount() > 0)
            {
                cursor.moveToNext();
                int width = cursor.getInt(cursor.getColumnIndex(SqlManager.CreateLevelDB.WIDTH));
                int height = cursor.getInt(cursor.getColumnIndex(SqlManager.CreateLevelDB.HEIGHT));
                int progress = cursor.getInt(cursor.getColumnIndex(SqlManager.CreateLevelDB.PROGRESS));
                String dataSet = cursor.getString(cursor.getColumnIndex(SqlManager.CreateLevelDB.DATASET));
                String saveData = "";
                if(progress == 1)
                    saveData = cursor.getString(cursor.getColumnIndex(SqlManager.CreateLevelDB.SAVEDATA));

                lastPlayLevel = new LevelData(lastPlayCategory, lastPlayName, width, height, progress, dataSet, saveData);

                tv_item_level_name.setText(lastPlayName);
                tv_item_level_size.setText(width + " X " + height);
            }

        }
        mDbOpenHelper.close();

        RecyclerView rv_board = fragmentView.findViewById(R.id.rv_level_board);
        rv_board.setLayoutManager(new GridLayoutManager(ctx, lastPlayLevel.getWidth()));
        rv_board.setAdapter(new RvLevelBoardAdapter(lastPlayLevel.getParsedSaveData()));
//
        img_continue.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        view.setAlpha(0.5F);
                        break;
                    case MotionEvent.ACTION_UP:
                        view.setAlpha(1F);
                        GameFragment gameFragment = new GameFragment();

                        Bundle bundle = new Bundle();
                        bundle.putString("category", lastPlayCategory);
                        bundle.putString("name", lastPlayName);
                        gameFragment.setArguments(bundle);

                        ((MainActivity) Objects.requireNonNull(getActivity())).fragmentMove(gameFragment);

                }
                return false;
            }
        });
        img_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        cl_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) Objects.requireNonNull(getActivity())).fragmentMove(new CategoryFragment(mainActivityCtx));
            }
        });


        return fragmentView;
    }
}