package com.sinwindis.logicgallery.mainactivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
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
import com.sinwindis.logicgallery.data.sharedpref.LastPlayPreference;
import com.sinwindis.logicgallery.data.sharedpref.PropertyPreference;
import com.sinwindis.logicgallery.data.sharedpref.TutorialPreference;
import com.sinwindis.logicgallery.gallery.GalleryFragment;
import com.sinwindis.logicgallery.data.DbOpenHelper;
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

public class MainFragment extends Fragment {

    private View fragmentView;
    private ImageView iv_thumbnail;

    private TutorialPreference tutorialPreference;
    private PropertyPreference propertyPreference;
    private LastPlayPreference lastPlayPreference;

    public MainFragment() {
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        iv_thumbnail = fragmentView.findViewById(R.id.iv_level_board);

        tutorialPreference = new TutorialPreference(getContext());
        propertyPreference = new PropertyPreference(getContext());
        lastPlayPreference = new LastPlayPreference(getContext());

        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable backgroundBtnOvalShadow = Objects.requireNonNull(getContext()).getDrawable(R.drawable.background_btn_oval_shadow);

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
            LevelCreateFragment dest = new LevelCreateFragment(getContext());
            ((MainActivity) Objects.requireNonNull(getActivity())).fragmentMove(dest);
        });

        //아티스트를 선택할 수 있는 갤러리 프래그먼트로 이동
        Button btn_gallery = fragmentView.findViewById(R.id.btn_gallery);

        btn_gallery.setOnClickListener(view -> {
            GalleryFragment dest = new GalleryFragment(getContext());

            ((MainActivity) getContext()).fragmentMove(dest);
        });

        showFirstTutorialDialog();

        return fragmentView;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onResume() {
        super.onResume();

        //가장 최근에 플레이한 레벨 데이터 받아오기

        //마지막 플레이한 레벨의 이름과 카테고리를 받아온다.
        final int lastPlayId = lastPlayPreference.getLastPlayId();
        final boolean custom = lastPlayPreference.isCustom();

        Log.d("lastPlayData", "id: " + lastPlayId + " custom: " + custom);

        //날짜가 하루 이상 지났으면
        if (isDateChanged()) {
            //hint 1개 추가
            propertyPreference.increaseHintCount();
            showRewardDialog();
        }

        DbOpenHelper mDbOpenHelper = new DbOpenHelper(getContext());
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
            assert srcBitmap != null;
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

                ((MainActivity) getContext()).fragmentMove(gameFragment);
            });
        }
    }

    private void showFirstTutorialDialog() {
        if (!tutorialPreference.isTutorialExperienced()) {
            TutorialDialog tutorialDialog = new TutorialDialog();

            tutorialDialog.makeDialog(getActivity());
            tutorialDialog.dialog.show();
            Objects.requireNonNull(tutorialDialog.dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            tutorialPreference.setTutorialExperienced();
        }
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

        int currentYear = Integer.parseInt(currentDate[2]);
        int currentMonth = Integer.parseInt(currentDate[1]);
        int currentDay = Integer.parseInt(currentDate[0]);


        //기존 날짜 확인
        String lastDateStr = propertyPreference.getDate();
        String[] lastDate = lastDateStr.split("-");

        int lastYear = Integer.parseInt(lastDate[2]);
        int lastMonth = Integer.parseInt(lastDate[1]);
        int lastDay = Integer.parseInt(lastDate[0]);

        if (lastYear < currentYear || lastMonth < currentMonth || lastDay < currentDay) {
            propertyPreference.setDate(formattedDate);
            return true;
        } else {
            return false;
        }
    }
}
