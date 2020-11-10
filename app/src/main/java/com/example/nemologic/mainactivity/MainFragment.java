package com.example.nemologic.mainactivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.nemologic.R;
import com.example.nemologic.data.BigLevelData;
import com.example.nemologic.data.StringGetter;
import com.example.nemologic.gallery.GalleryFragment;
import com.example.nemologic.data.BigLevelThumbnailData;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.LevelThumbnailData;
import com.example.nemologic.data.SqlManager;
import com.example.nemologic.game.GameFragment;
import com.example.nemologic.levelcreate.LevelCreateFragment;
import com.example.nemologic.option.OptionDialog;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

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

        SharedPreferences lastPlayPref = ctx.getSharedPreferences("LASTPLAY", MODE_PRIVATE);

        //마지막 플레이한 레벨의 이름과 카테고리를 받아온다.
        final int lastPlayId = lastPlayPref.getInt("id", -1);

        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Cursor cursor;


        boolean dataLoad = false;

        ImageView iv_board = fragmentView.findViewById(R.id.iv_level_board);
        
        if(lastPlayId != -1)
        {
            cursor =  mDbOpenHelper.getBigLevelsCursorById(lastPlayId);

            if(cursor.getCount() > 0)
            {
                dataLoad = true;
                cursor.moveToNext();
                int p_id = cursor.getInt(cursor.getColumnIndex(SqlManager.BigLevelDBSql.P_ID));
                int number = cursor.getInt(cursor.getColumnIndex(SqlManager.BigLevelDBSql.NUMBER));
                int width = cursor.getInt(cursor.getColumnIndex(SqlManager.BigLevelDBSql.WIDTH));
                int height = cursor.getInt(cursor.getColumnIndex(SqlManager.BigLevelDBSql.HEIGHT));
                int progress = cursor.getInt(cursor.getColumnIndex(SqlManager.BigLevelDBSql.PROGRESS));
                byte[] dataSet = cursor.getBlob(cursor.getColumnIndex(SqlManager.BigLevelDBSql.DATASET));
                byte[] colorSet = cursor.getBlob(cursor.getColumnIndex(SqlManager.BigLevelDBSql.COLORSET));
                byte[] saveData = cursor.getBlob(cursor.getColumnIndex(SqlManager.BigLevelDBSql.SAVEDATA));

                Cursor bigPuzzleCursor = mDbOpenHelper.getBigPuzzleCursorById(p_id);

                bigPuzzleCursor.moveToNext();

                //나중에 수정
                String name = StringGetter.p_name.get(p_id);

                BigLevelData lastPlayLevel = new BigLevelData(lastPlayId, p_id, number, width, height, progress, dataSet, saveData, colorSet);

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(lastPlayLevel.getSaveBitmap(), 100, 100, false);
                iv_board.setImageBitmap(scaledBitmap);
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
                optionDialog.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
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
                LevelCreateFragment dest = new LevelCreateFragment(mainActivityCtx);
                ((MainActivity) Objects.requireNonNull(getActivity())).fragmentMove(dest);
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
                    gameFragment.setArguments(bundle);

                    ((MainActivity)ctx).fragmentMove(gameFragment);
                }
            }
        });

        //아티스트를 선택할 수 있는 갤러리 프래그먼트로 이동
        Button btn_gallery = fragmentView.findViewById(R.id.btn_gallery);

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GalleryFragment dest = new GalleryFragment(mainActivityCtx);

                ((MainActivity)ctx).fragmentMove(dest);
            }
        });


        return fragmentView;
    }
}