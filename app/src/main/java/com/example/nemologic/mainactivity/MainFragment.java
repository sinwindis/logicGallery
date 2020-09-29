package com.example.nemologic.mainactivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

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
        final Context ctx = fragmentView.getContext();



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

        boolean dataLoad = false;

        assert lastPlayName != null;
        assert lastPlayCategory != null;
        RecyclerView rv_board = fragmentView.findViewById(R.id.rv_level_board);
        
        if(!lastPlayName.isEmpty() && !lastPlayCategory.isEmpty())
        {
            cursor =  mDbOpenHelper.getLevelCursorByCategoryAndName(lastPlayCategory, lastPlayName);

            if(cursor.getCount() > 0)
            {
                dataLoad = true;
                cursor.moveToNext();
                int width = cursor.getInt(cursor.getColumnIndex(SqlManager.CreateLevelDB.WIDTH));
                int height = cursor.getInt(cursor.getColumnIndex(SqlManager.CreateLevelDB.HEIGHT));
                int progress = cursor.getInt(cursor.getColumnIndex(SqlManager.CreateLevelDB.PROGRESS));
                String dataSet = cursor.getString(cursor.getColumnIndex(SqlManager.CreateLevelDB.DATASET));
                String saveData = "";
                if(progress == 1)
                    saveData = cursor.getString(cursor.getColumnIndex(SqlManager.CreateLevelDB.SAVEDATA));

                lastPlayLevel = new LevelData(lastPlayCategory, lastPlayName, width, height, progress, dataSet, saveData);

                rv_board.setLayoutManager(new GridLayoutManager(ctx, lastPlayLevel.getWidth()));
                rv_board.setAdapter(new RvContinueLevelAdapter(lastPlayLevel.getParsedSaveData()));
            }
        }
        mDbOpenHelper.close();

        final boolean isContinue = dataLoad;

        //버튼 클릭리스너 부분
        //옵션 버튼
        ImageView img_option = fragmentView.findViewById(R.id.img_option);

        img_option.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getActionMasked())
                {
                    case MotionEvent.ACTION_DOWN:
                        view.setBackground(ctx.getResources().getDrawable(R.drawable.background_btn_shadow_dark));
                        break;

                    case MotionEvent.ACTION_UP:
                        view.setBackground(ctx.getResources().getDrawable(R.drawable.background_btn_shadow_bright));
                        break;
                }

                return false;
            }
        });

        img_option.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                OptionDialog optionDialog = new OptionDialog();

                optionDialog.makeOptionDialog(getActivity());
                optionDialog.dialog.show();
            }
        });

        //추가 버튼
        ImageView img_plus = fragmentView.findViewById(R.id.img_plus);
        img_plus.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getActionMasked())
                {
                    case MotionEvent.ACTION_DOWN:
                        view.setBackground(ctx.getResources().getDrawable(R.drawable.background_btn_shadow_dark));
                        break;

                    case MotionEvent.ACTION_UP:
                        view.setBackground(ctx.getResources().getDrawable(R.drawable.background_btn_shadow_bright));
                        break;
                }

                return false;
            }
        });

        img_plus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ((MainActivity) Objects.requireNonNull(getActivity())).fragmentMove(new LevelCreateFragment(mainActivityCtx));
            }
        });

        //이어하기 버튼
        Button btn_continue = fragmentView.findViewById(R.id.btn_continue);
        if(!isContinue)
        {
            btn_continue.setLayoutParams(new ConstraintLayout.LayoutParams(0, 0));
        }

        btn_continue.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(isContinue)
                {
                    GameFragment gameFragment = new GameFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString("category", lastPlayCategory);
                    bundle.putString("name", lastPlayName);
                    gameFragment.setArguments(bundle);

                    ((MainActivity) Objects.requireNonNull(getActivity())).fragmentMove(gameFragment);
                }
            }
        });

        //카테고리 버튼
        Button btn_category = fragmentView.findViewById(R.id.btn_category);

        btn_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) Objects.requireNonNull(getActivity())).fragmentMove(new CategoryFragment(mainActivityCtx));
            }
        });


        return fragmentView;
    }
}