package com.example.nemologic.mainactivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.nemologic.R;
import com.example.nemologic.bigpuzzle.BigPuzzleFragment;
import com.example.nemologic.category.CategoryFragment;
import com.example.nemologic.data.BigLevelThumbnailData;
import com.example.nemologic.data.DbManager;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.LevelThumbnailData;
import com.example.nemologic.data.SqlManager;
import com.example.nemologic.game.GameFragment;
import com.example.nemologic.levelcreate.LevelCreateFragment;
import com.example.nemologic.option.OptionDialog;

import java.sql.SQLException;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment {

    Context ctx;


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
        DbManager.loadLevelFromXmlToDb(ctx);
        DbManager.loadCategory(ctx);

        SharedPreferences lastPlayPref = ctx.getSharedPreferences("LASTPLAY", MODE_PRIVATE);

        //마지막 플레이한 레벨의 이름과 카테고리를 받아온다.
        final int lastPlayId = lastPlayPref.getInt("id", -1);
        final int type = lastPlayPref.getInt("type", -1);

        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mDbOpenHelper.create();

        Cursor cursor;


        boolean dataLoad = false;

        ImageView iv_board = fragmentView.findViewById(R.id.iv_level_board);
        
        if(lastPlayId != -1)
        {
            if(type == 0)
            {
                cursor =  mDbOpenHelper.getLevelCursorById(lastPlayId);

                if(cursor.getCount() > 0)
                {
                    dataLoad = true;
                    cursor.moveToNext();
                    String name = cursor.getString(cursor.getColumnIndex(SqlManager.LevelDBSql.NAME));
                    String category = cursor.getString(cursor.getColumnIndex(SqlManager.LevelDBSql.CATEGORY));
                    int width = cursor.getInt(cursor.getColumnIndex(SqlManager.LevelDBSql.WIDTH));
                    int height = cursor.getInt(cursor.getColumnIndex(SqlManager.LevelDBSql.HEIGHT));
                    int progress = cursor.getInt(cursor.getColumnIndex(SqlManager.LevelDBSql.PROGRESS));
                    byte[] dataSet = cursor.getBlob(cursor.getColumnIndex(SqlManager.LevelDBSql.DATASET));
                    byte[] colorSet = cursor.getBlob(cursor.getColumnIndex(SqlManager.LevelDBSql.COLORSET));
                    byte[] saveData = new byte[0];
                    int custom = cursor.getInt(cursor.getColumnIndex(SqlManager.LevelDBSql.CUSTOM));
                    if(progress == 1)
                        saveData = cursor.getBlob(cursor.getColumnIndex(SqlManager.LevelDBSql.SAVEDATA));

                    LevelThumbnailData lastPlayLevel = new LevelThumbnailData(lastPlayId, category, name, width, height, progress, dataSet, saveData, colorSet, 0, custom);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(lastPlayLevel.getSaveBitmap(), 100, 100, false);
                    iv_board.setImageBitmap(scaledBitmap);
                }
            }
            else if(type == 1)
            {
                cursor =  mDbOpenHelper.getBigLevelsCursorById(lastPlayId);

                if(cursor.getCount() > 0)
                {
                    dataLoad = true;
                    cursor.moveToNext();
                    int p_id = cursor.getInt(cursor.getColumnIndex(SqlManager.BigLevelDBSql.P_ID));
                    int width = cursor.getInt(cursor.getColumnIndex(SqlManager.BigLevelDBSql.WIDTH));
                    int height = cursor.getInt(cursor.getColumnIndex(SqlManager.BigLevelDBSql.HEIGHT));
                    int progress = cursor.getInt(cursor.getColumnIndex(SqlManager.BigLevelDBSql.PROGRESS));
                    byte[] dataSet = cursor.getBlob(cursor.getColumnIndex(SqlManager.BigLevelDBSql.DATASET));
                    byte[] colorSet = cursor.getBlob(cursor.getColumnIndex(SqlManager.BigLevelDBSql.COLORSET));
                    byte[] saveData = new byte[0];
                    if(progress == 1)
                        saveData = cursor.getBlob(cursor.getColumnIndex(SqlManager.BigLevelDBSql.SAVEDATA));

                    Cursor bigPuzzleCursor = mDbOpenHelper.getBigPuzzleCursorById(p_id);

                    bigPuzzleCursor.moveToNext();

                    String name = bigPuzzleCursor.getString(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.NAME));


                    BigLevelThumbnailData lastPlayLevel = new BigLevelThumbnailData(lastPlayId, name, width, height, progress, dataSet, saveData, colorSet);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(lastPlayLevel.getSaveBitmap(), 100, 100, false);
                    iv_board.setImageBitmap(scaledBitmap);
                }
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
                        view.setBackground(ctx.getResources().getDrawable(R.drawable.background_btn_shadow));
                        break;

                    case MotionEvent.ACTION_UP:
                        view.setBackground(null);
                        break;
                }

                return false;
            }
        });

        img_option.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                OptionDialog optionDialog = new OptionDialog();

                optionDialog.makeDialog(getActivity());
                optionDialog.dialog.show();
            }
        });

        //추가 버튼
        ImageView img_plus = fragmentView.findViewById(R.id.img_plus);
        img_plus.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getActionMasked())
                {
                    case MotionEvent.ACTION_DOWN:
                        view.setBackground(ctx.getResources().getDrawable(R.drawable.background_btn_shadow));
                        break;

                    case MotionEvent.ACTION_UP:
                        view.setBackground(null);
                        break;
                }

                return false;
            }
        });

        img_plus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).fragmentMove(new LevelCreateFragment(mainActivityCtx));
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
                    GameFragment gameFragment = new GameFragment(mainActivityCtx);

                    Bundle bundle = new Bundle();
                    bundle.putInt("id", lastPlayId);
                    bundle.putInt("type", type);
                    gameFragment.setArguments(bundle);

                    ((MainActivity)ctx).fragmentMove(gameFragment);
                }
            }
        });

        //카테고리 버튼
        Button btn_category = fragmentView.findViewById(R.id.btn_category);

        btn_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CategoryFragment dest = new CategoryFragment(mainActivityCtx);

                ((MainActivity)ctx).fragmentMove(dest);
            }
        });

        //빅 퍼즐 버튼
        Button btn_bigPuzzle = fragmentView.findViewById(R.id.btn_bigpuzzle);

        btn_bigPuzzle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BigPuzzleFragment dest = new BigPuzzleFragment(mainActivityCtx);

                ((MainActivity)ctx).fragmentMove(dest);
            }
        });


        return fragmentView;
    }
}