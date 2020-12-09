package com.sinwindis.logicgallery.mainactivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.sinwindis.logicgallery.R;
import com.sinwindis.logicgallery.data.BitmapMaker;
import com.sinwindis.logicgallery.data.LevelDto;
import com.sinwindis.logicgallery.data.SaveData;
import com.sinwindis.logicgallery.data.StringGetter;
import com.sinwindis.logicgallery.gallery.GalleryFragment;
import com.sinwindis.logicgallery.data.DbOpenHelper;
import com.sinwindis.logicgallery.data.SqlManager;
import com.sinwindis.logicgallery.game.GameFragment;
import com.sinwindis.logicgallery.levelcreate.LevelCreateFragment;
import com.sinwindis.logicgallery.dialog.OptionDialog;
import com.sinwindis.logicgallery.dialog.TutorialDialog;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import com.sinwindis.logicgallery.dialog.RewardDialog;

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

        @SuppressLint("UseCompatLoadingForDrawables") Drawable backgroundBtnOvalShadow = ctx.getDrawable(R.drawable.background_btn_oval_shadow);

        //버튼 클릭리스너 부분
        //옵션 버튼
        ImageView img_option = fragmentView.findViewById(R.id.img_option);

        img_option.setOnTouchListener((view, motionEvent) -> {

            switch (motionEvent.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    view.setBackground(backgroundBtnOvalShadow);
                    break;

                case MotionEvent.ACTION_UP:
                    view.setBackground(null);
                    break;
            }

            return false;
        });

        img_option.setOnClickListener(view -> {
            OptionDialog optionDialog = new OptionDialog();

            optionDialog.makeDialog(getActivity());
            optionDialog.dialog.show();
            Objects.requireNonNull(optionDialog.dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        });

        //추가 버튼
        ImageView img_plus = fragmentView.findViewById(R.id.img_plus);
        img_plus.setOnTouchListener((view, motionEvent) -> {

            switch (motionEvent.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    view.setBackground(backgroundBtnOvalShadow);
                    break;

                case MotionEvent.ACTION_UP:
                    view.setBackground(null);
                    break;
            }

            return false;
        });

        img_plus.setOnClickListener(view -> {
            LevelCreateFragment dest = new LevelCreateFragment(ctx);
            ((MainActivity) Objects.requireNonNull(getActivity())).fragmentMove(dest);
        });

        //아티스트를 선택할 수 있는 갤러리 프래그먼트로 이동
        Button btn_gallery = fragmentView.findViewById(R.id.btn_gallery);

        btn_gallery.setOnClickListener(view -> {
            GalleryFragment dest = new GalleryFragment(ctx);

            ((MainActivity) ctx).fragmentMove(dest);
        });

        showFirstTutorialDialog();

        return fragmentView;
    }

    private void showFirstTutorialDialog() {
        SharedPreferences tutorialPref = ctx.getSharedPreferences("TUTORIAL", MODE_PRIVATE);
        if (!tutorialPref.getBoolean("tutorial", false)) {
            TutorialDialog tutorialDialog = new TutorialDialog();

            tutorialDialog.makeDialog(getActivity());
            tutorialDialog.dialog.show();
            Objects.requireNonNull(tutorialDialog.dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            SharedPreferences.Editor editor = tutorialPref.edit();

            editor.putBoolean("tutorial", true);
            editor.apply();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onResume() {
        super.onResume();

        //가장 최근에 플레이한 레벨 데이터 받아오기


        SharedPreferences lastPlayPref = ctx.getSharedPreferences("LASTPLAY", MODE_PRIVATE);

        //마지막 플레이한 레벨의 이름과 카테고리를 받아온다.
        final int lastPlayId = lastPlayPref.getInt("id", -1);
        final boolean custom = lastPlayPref.getBoolean("custom", false);

        Log.d("lastPlayData", "id: " + lastPlayId + " custom: " + custom);

        //날짜가 하루 이상 지났으면
        if (isDateChanged()) {
            //hint 1개 추가
            addHint();
            showRewardDialog();
        }

        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        final LevelDto lastPlayLevelDto;

        if (custom) {
            lastPlayLevelDto = mDbOpenHelper.getCustomLevelDto(lastPlayId);
        } else {
            lastPlayLevelDto = mDbOpenHelper.getLevelDto(lastPlayId);
        }

        mDbOpenHelper.close();


        Log.d("MainFragment", "LevelDto: " + lastPlayLevelDto.toString());


        final boolean isComplete = (lastPlayLevelDto.getProgress() == 2 || lastPlayLevelDto.getProgress() == 4);

        iv_thumbnail.post(() -> {

            Bitmap srcBitmap;
            Bitmap scaledBitmap;
            if (isComplete) {
                srcBitmap = BitmapMaker.getColorBitmap(lastPlayLevelDto.getColorBlob());
            } else {
                final SaveData saveData = lastPlayLevelDto.getSaveData();
                srcBitmap = BitmapMaker.getGrayScaleBitmap(saveData.getValues());
            }
            scaledBitmap = Bitmap.createScaledBitmap(srcBitmap, 400, 400, false);
            iv_thumbnail.setImageBitmap(scaledBitmap);
        });


        Log.d("MainFragment", "isComplete: " + isComplete);

        //이어하기 버튼
        Button btn_continue = fragmentView.findViewById(R.id.btn_continue);
        if (isComplete) {
            btn_continue.setVisibility(View.INVISIBLE);
        } else {
            btn_continue.setOnClickListener(view -> {
                GameFragment gameFragment = new GameFragment();

                Bundle bundle = new Bundle();
                bundle.putInt("id", lastPlayId);
                gameFragment.setArguments(bundle);

                ((MainActivity) ctx).fragmentMove(gameFragment);
            });
        }
    }

    private void addHint() {
        SharedPreferences hintPref = ctx.getSharedPreferences("PROPERTY", MODE_PRIVATE);
        SharedPreferences.Editor editor = hintPref.edit();

        int hintCount = hintPref.getInt("hint", 4);
        editor.putInt("hint", hintCount + 1);
        editor.apply();
    }

    private void showRewardDialog() {

        RewardDialog rewardDialog = new RewardDialog();
        rewardDialog.makeDialog(getActivity());
        rewardDialog.dialog.show();
        Objects.requireNonNull(rewardDialog.dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    private boolean isDateChanged() {
        //현재 날짜 확인
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        String[] currentDate = formattedDate.split("-");


        //기존 날짜 확인
        SharedPreferences datePref = ctx.getSharedPreferences("PROPERTY", MODE_PRIVATE);
        String lastDateStr = datePref.getString("date", "00-00-0000");
        String[] lastDate = lastDateStr.split("-");

        SharedPreferences.Editor editor = datePref.edit();

        editor.putString("date", formattedDate);
        editor.apply();

        int lastYear = Integer.parseInt(lastDate[2]);
        int currentYear = Integer.parseInt(currentDate[2]);
        int lastMonth = Integer.parseInt(lastDate[1]);
        int currentMonth = Integer.parseInt(currentDate[1]);
        int lastDay = Integer.parseInt(lastDate[0]);
        int currentDay = Integer.parseInt(currentDate[0]);

        return (lastYear < currentYear || lastMonth < currentMonth || lastDay < currentDay);
    }
}
