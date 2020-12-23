package com.sinwindis.logicgallery.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.sinwindis.logicgallery.R;
import com.sinwindis.logicgallery.animation.ButtonAnimation;
import com.sinwindis.logicgallery.data.DbOpenHelper;
import com.sinwindis.logicgallery.data.LevelDto;
import com.sinwindis.logicgallery.dialog.OptionDialog;
import com.sinwindis.logicgallery.dialog.TutorialDialog;

import java.sql.SQLException;

public class GameFragment extends Fragment {

    private LevelDto levelDto;

    private View fragmentView;

    private GameBoardController gameBoardController;
    private TouchMode touchMode;

    private int levelId;
    private boolean isCustom = false;

    public GameFragment() {
    }

    @SuppressLint("ClickableViewAccessibility")


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_game, container, false);
        Context ctx = fragmentView.getContext();

        //db와 연결해 해당 게임레벨 데이터를 받아올 준비를 한다.
        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //bundle 로 받은 id 를 저장한다.
        if (getArguments() != null) {
            levelId = getArguments().getInt("id");
            isCustom = getArguments().getBoolean("custom");

            Log.d("GameFragment", "id: " + levelId + " custom: " + isCustom);
        }
        LevelDto levelDto;

        if (isCustom) {
            levelDto = mDbOpenHelper.getCustomLevelDto(levelId);

        } else {

            levelDto = mDbOpenHelper.getLevelDto(levelId);
        }

        mDbOpenHelper.close();

        TextView tv_title = fragmentView.findViewById(R.id.tv_title);
        tv_title.setText(levelDto.getName());

        //gameBoard 를 제작한다.
        gameBoardController = new GameBoardController(ctx, fragmentView, levelDto);
        touchMode = new TouchMode();
        gameBoardController.setTouchMode(touchMode);

        setButtonListeners();

        return fragmentView;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setButtonListeners() {

        //버튼 이벤트
        //터치 모드 버튼
        final ImageView ivButtonO = fragmentView.findViewById(R.id.img_button_o);
        final ImageView ivButtonX = fragmentView.findViewById(R.id.img_button_x);
        final ImageView ivButtonLock = fragmentView.findViewById(R.id.img_button_lock);
        final ImageView ivButtonHint = fragmentView.findViewById(R.id.img_button_hint);
        @SuppressLint("UseCompatLoadingForDrawables") final Drawable background = getResources().getDrawable(R.drawable.background_btn_oval_normal);

        TouchModeButtonController touchModeButtonController = new TouchModeButtonController(ivButtonO, ivButtonX, ivButtonLock, ivButtonHint);
        touchModeButtonController.setTouchMode(touchMode);
        touchModeButtonController.setup(background);

        //다음 스택 버튼
        ImageView img_next = fragmentView.findViewById(R.id.img_nextstack);
        ButtonAnimation.setOvalButtonAnimationShadow(img_next);

        img_next.setOnClickListener(view -> gameBoardController.moveToNext());

        ImageView img_prev = fragmentView.findViewById(R.id.img_prevstack);
        ButtonAnimation.setOvalButtonAnimationShadow(img_prev);

        img_prev.setOnClickListener(view -> gameBoardController.moveToPrev());

        //튜토리얼 버튼
        ImageView img_tutorial = fragmentView.findViewById(R.id.img_tutorial);

        ButtonAnimation.setOvalButtonAnimationBlack(img_tutorial);
        img_tutorial.setOnClickListener(view -> {

            TutorialDialog tutorialDialog = new TutorialDialog();

            tutorialDialog.makeDialog(getActivity());
            tutorialDialog.dialog.show();
            tutorialDialog.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        });

        //옵션 버튼
        ImageView img_option = fragmentView.findViewById(R.id.img_option);
        ButtonAnimation.setOvalButtonAnimationBlack(img_option);

        img_option.setOnClickListener(view -> {

            OptionDialog optionDialog = new OptionDialog();

            optionDialog.makeDialog(getActivity());
            optionDialog.dialog.setOnDismissListener(dialogInterface -> gameBoardController.loadPref());
            optionDialog.dialog.show();
            optionDialog.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        });

        //뒤로가기 버튼
        ImageView img_back = fragmentView.findViewById(R.id.img_back);
        ButtonAnimation.setOvalButtonAnimationBlack(img_back);

        img_back.setOnClickListener(view -> {

            assert getFragmentManager() != null;
            getFragmentManager().popBackStackImmediate();

        });

        //뒤로가기 버튼
        ImageView img_end = fragmentView.findViewById(R.id.img_end);
        ButtonAnimation.setOvalButtonAnimationBlack(img_back);

        img_end.setOnClickListener(view -> {

            gameBoardController.completeGame();

        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        gameBoardController.onDestroy();
    }
}