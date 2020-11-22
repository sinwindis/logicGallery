package com.example.nemologic.mainactivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
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
import com.example.nemologic.data.BigLevelData;
import com.example.nemologic.gallery.GalleryFragment;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.SqlManager;
import com.example.nemologic.game.GameFragment;
import com.example.nemologic.levelcreate.LevelCreateFragment;
import com.example.nemologic.option.OptionDialog;
import com.example.nemologic.tutorial.TutorialDialog;

import java.sql.SQLException;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment {

    Context ctx;
    View fragmentView;



    ImageView iv_thumbnail;

    public MainFragment(Context ctx) {
        this.ctx = ctx;
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_main, container, false);

        iv_thumbnail = fragmentView.findViewById(R.id.iv_level_board);

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
                        view.setBackground(ctx.getResources().getDrawable(R.drawable.background_btn_oval_shadow));
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
                Objects.requireNonNull(optionDialog.dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
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
                        view.setBackground(ctx.getResources().getDrawable(R.drawable.background_btn_oval_shadow));
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
                LevelCreateFragment dest = new LevelCreateFragment(ctx);
                ((MainActivity) Objects.requireNonNull(getActivity())).fragmentMove(dest);
            }
        });

        //아티스트를 선택할 수 있는 갤러리 프래그먼트로 이동
        Button btn_gallery = fragmentView.findViewById(R.id.btn_gallery);

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GalleryFragment dest = new GalleryFragment(ctx);

                ((MainActivity)ctx).fragmentMove(dest);
            }
        });


        return fragmentView;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onResume()
    {
        super.onResume();

        //가장 최근에 플레이한 레벨 데이터 받아오기
        SharedPreferences tutorialPref = ctx.getSharedPreferences("TUTORIAL", MODE_PRIVATE);
        if(!tutorialPref.getBoolean("tutorial", false))
        {
            TutorialDialog tutorialDialog = new TutorialDialog();

            tutorialDialog.makeDialog(getActivity());
            tutorialDialog.dialog.show();
            Objects.requireNonNull(tutorialDialog.dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            SharedPreferences.Editor editor = tutorialPref.edit();

            editor.putBoolean("tutorial", true);
            editor.apply();
        }

        SharedPreferences lastPlayPref = ctx.getSharedPreferences("LASTPLAY", MODE_PRIVATE);

        //마지막 플레이한 레벨의 이름과 카테고리를 받아온다.
        final int lastPlayId = lastPlayPref.getInt("id", 0);
        final boolean custom = lastPlayPref.getBoolean("custom", false);



        iv_thumbnail.post(new Runnable() {
            @Override
            public void run() {

                DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
                try {
                    mDbOpenHelper.open();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                
                
                Cursor cursor;

                boolean dataLoad = false;

                if(custom)
                {
                    if(lastPlayId != 0)
                    {
                        if(lastPlayId > 0)
                        {
                            cursor =  mDbOpenHelper.getCustomBigLevelCursorById(lastPlayId);
                            dataLoad = true;
                        }
                        else
                        {
                            cursor =  mDbOpenHelper.getCustomBigLevelCursorById(-1*lastPlayId);
                        }


                        if(cursor.getCount() > 0)
                        {

                            cursor.moveToNext();
                            int id = cursor.getInt(cursor.getColumnIndex(SqlManager.CustomBigLevelDBSql.ID));
                            int p_id = cursor.getInt(cursor.getColumnIndex(SqlManager.CustomBigLevelDBSql.P_ID));
                            int number = cursor.getInt(cursor.getColumnIndex(SqlManager.CustomBigLevelDBSql.NUMBER));
                            int width = cursor.getInt(cursor.getColumnIndex(SqlManager.CustomBigLevelDBSql.WIDTH));
                            int height = cursor.getInt(cursor.getColumnIndex(SqlManager.CustomBigLevelDBSql.HEIGHT));
                            int progress = cursor.getInt(cursor.getColumnIndex(SqlManager.CustomBigLevelDBSql.PROGRESS));
                            byte[] dataSet = cursor.getBlob(cursor.getColumnIndex(SqlManager.CustomBigLevelDBSql.DATASET));
                            byte[] colorSet = cursor.getBlob(cursor.getColumnIndex(SqlManager.CustomBigLevelDBSql.COLORSET));
                            byte[] saveData = cursor.getBlob(cursor.getColumnIndex(SqlManager.CustomBigLevelDBSql.SAVEDATA));

                            Cursor bigPuzzleCursor = mDbOpenHelper.getBigPuzzleCursorById(p_id);

                            bigPuzzleCursor.moveToNext();

                            BigLevelData lastPlayLevel = new BigLevelData(id, p_id, number, width, height, progress, dataSet, saveData, colorSet, true);

                            Bitmap scaledBitmap;



                            if(lastPlayId > 0)
                            {
                                scaledBitmap = Bitmap.createScaledBitmap(lastPlayLevel.getSaveBitmap(), iv_thumbnail.getMeasuredWidth(), iv_thumbnail.getMeasuredHeight(), false);
                            }
                            else
                            {
                                scaledBitmap = Bitmap.createScaledBitmap(lastPlayLevel.getColorBitmap(), iv_thumbnail.getMeasuredWidth(), iv_thumbnail.getMeasuredHeight(), false);
                            }

                            iv_thumbnail.setImageBitmap(scaledBitmap);
                        }

                    }
                }
                else
                {
                    if(lastPlayId != 0)
                    {
                        if(lastPlayId > 0)
                        {
                            cursor =  mDbOpenHelper.getBigLevelCursorById(lastPlayId);
                            dataLoad = true;
                        }
                        else
                        {
                            cursor =  mDbOpenHelper.getBigLevelCursorById(-1*lastPlayId);
                        }


                        if(cursor.getCount() > 0)
                        {

                            cursor.moveToNext();
                            int id = cursor.getInt(cursor.getColumnIndex(SqlManager.BigLevelDBSql.ID));
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

                            BigLevelData lastPlayLevel = new BigLevelData(id, p_id, number, width, height, progress, dataSet, saveData, colorSet, false);

                            Bitmap scaledBitmap;



                            if(lastPlayId > 0)
                            {
                                scaledBitmap = Bitmap.createScaledBitmap(lastPlayLevel.getSaveBitmap(), iv_thumbnail.getMeasuredWidth(), iv_thumbnail.getMeasuredHeight(), false);
                            }
                            else
                            {
                                scaledBitmap = Bitmap.createScaledBitmap(lastPlayLevel.getColorBitmap(), iv_thumbnail.getMeasuredWidth(), iv_thumbnail.getMeasuredHeight(), false);
                            }

                            iv_thumbnail.setImageBitmap(scaledBitmap);
                        }

                    }
                }
                
                
                mDbOpenHelper.close();

                final boolean isContinue = dataLoad;

                //이어하기 버튼
                Button btn_continue = fragmentView.findViewById(R.id.btn_continue);
                if(!isContinue)
                {
                    ConstraintLayout cl_frame = fragmentView.findViewById(R.id.cl_frame);
                    cl_frame.setVisibility(View.INVISIBLE);
                    btn_continue.setVisibility(View.INVISIBLE);
                }

                btn_continue.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if(isContinue)
                        {
                            GameFragment gameFragment = new GameFragment(ctx);

                            Bundle bundle = new Bundle();
                            bundle.putInt("id", lastPlayId);
                            gameFragment.setArguments(bundle);

                            ((MainActivity)ctx).fragmentMove(gameFragment);
                        }
                    }
                });
            }
        });




    }
}