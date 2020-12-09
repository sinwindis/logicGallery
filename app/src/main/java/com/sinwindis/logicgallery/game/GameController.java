package com.sinwindis.logicgallery.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sinwindis.logicgallery.R;
import com.sinwindis.logicgallery.data.DbOpenHelper;
import com.sinwindis.logicgallery.data.LevelDto;
import com.sinwindis.logicgallery.data.Progress;
import com.sinwindis.logicgallery.data.SaveData;
import com.sinwindis.logicgallery.end.EndFragment;
import com.sinwindis.logicgallery.listener.BoardItemTouchListener;
import com.sinwindis.logicgallery.mainactivity.MainActivity;

import java.sql.SQLException;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class GameController {

    //property
    private boolean smartDrag;
    private boolean oneLineDrag;
    private boolean autoX;

    private int touchStartX;
    private int touchStartY;
    private int touchEndX;
    private int touchEndY;

    private int hintCount;
    private int progress;

    private float viewSize;

    private boolean[][] dragTemp;

    private final Context ctx;

    //Model
    public Board board;
    //for loading
    private final LevelDto levelDto;
    //for save
    private LevelDto saveLevelDto;

    private ColumnIndexDataManager columnIndexDataManager;
    private RowIndexDataManager rowIndexDataManager;
    private GridLayoutManager glm;

    //View
    private final View targetView;

    private RecyclerView rv_board;
    private RecyclerView rv_column;
    private RecyclerView rv_row;

    private RvColumnAdapter rvColumnAdapter;
    private RvRowAdapter rvRowAdapter;

    private TextView tv_stack;
    private TextView tv_hint;
    private TextView tv_count;

    private ConstraintLayout cl_count;
    private LinearLayout ll_drag;


    //0: 체크, 1: X, 2: 힌트
    int touchMode = 0;
    //0: 공백 1: 체크 2: X 3: 힌트(정답)
    int macroMode = 0;

    public GameController(Context ctx, View view, LevelDto levelDto) {
        targetView = view;
        this.levelDto = levelDto;
        this.ctx = ctx;

        initialize();
    }

    private void initialize() {
        Log.d("GameController", levelDto.toString());

        board = new Board(levelDto.getHeight(), levelDto.getWidth());
        board.setCorrectValues(levelDto.getDataSet());
        board.setSaveData(levelDto.getSaveData());

        Log.d("initialize", "board width: " + board.getWidth() + "board height: " + board.getHeight());

        dragTemp = new boolean[levelDto.getHeight()][levelDto.getWidth()];

        rv_board = targetView.findViewById(R.id.rv_board);
        rv_row = targetView.findViewById(R.id.cl_row);
        rv_column = targetView.findViewById(R.id.cl_column);

        cl_count = targetView.findViewById(R.id.cl_count);
        ll_drag = targetView.findViewById(R.id.ll_drag);

        tv_count = targetView.findViewById(R.id.tv_count);
        tv_stack = targetView.findViewById(R.id.tv_stack);
        tv_hint = targetView.findViewById(R.id.tv_hint);

        Log.d("GameController", "initialize end");

        loadPref();
        setProgressPlaying();

        makeGameBoard();
    }

    public void loadPref() {
        //option preferences
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("OPTION", MODE_PRIVATE);
        smartDrag = sharedPreferences.getBoolean("smartDrag", true);
        oneLineDrag = sharedPreferences.getBoolean("oneLineDrag", true);
        autoX = sharedPreferences.getBoolean("autoX", false);

        //hint properties
        sharedPreferences = ctx.getSharedPreferences("PROPERTY", MODE_PRIVATE);
        hintCount = sharedPreferences.getInt("hint", 3);
    }


    private void removeDragTemp() {
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                dragTemp[y][x] = false;
            }
        }

        cl_count.setLayoutParams(new ConstraintLayout.LayoutParams(0, 0));
        ll_drag.setLayoutParams(new ConstraintLayout.LayoutParams(0, 0));
    }

    private void dragManage() {
        int dragStartX;
        int dragStartY;
        int dragEndX;
        int dragEndY;

        //한 줄만 드래그하기 옵션의 경우 터치가 끝난 지점과 실제 드래그한 지점이 다를 수 있기 때문에 정확한 값을 저장하기 위함
        int lastX;
        int lastY;

        int dragCount;

        //저번 드래그했을 때 남아있는 드래그 범위를 없애준다
        removeDragTemp();

        //터치한 시작점과 끝점을 이용해서 드래그한 범위를 지정해줌
        if (touchStartX > touchEndX) {
            dragStartX = touchEndX;
            dragEndX = touchStartX;
        } else {
            dragStartX = touchStartX;
            dragEndX = touchEndX;
        }

        if (touchStartY > touchEndY) {
            dragStartY = touchEndY;
            dragEndY = touchStartY;
        } else {
            dragStartY = touchStartY;
            dragEndY = touchEndY;
        }


        if (oneLineDrag) {
            //한 줄만 드래그하기 옵션이 켜져 있을 경우
            if (dragEndX - dragStartX > dragEndY - dragStartY) {
                //가로 방향 드래그가 더 길 경우
                //가로 방향 한 줄만 드래그시킨다.
                dragCount = dragEndX - dragStartX + 1;
                dragEndY = touchStartY;
                dragStartY = touchStartY;

                //마지막으로 선택된 칸의 위치를 lastX와 lastY에 저장해 둔다.
                lastX = touchEndX;
                lastY = touchStartY;
            } else {
                //세로 방향 드래그가 더 길 경우
                dragCount = dragEndY - dragStartY + 1;
                dragEndX = touchStartX;
                dragStartX = touchStartX;

                lastX = touchStartX;
                lastY = touchEndY;
            }
        } else {
            //마지막으로 터치한 지점의 좌표값과 드래그한 넓이를 대입해준다.
            lastX = touchEndX;
            lastY = touchEndY;
            dragCount = (dragEndX - dragStartX + 1) * (dragEndY - dragStartY + 1);
        }

        for (int y = dragStartY; y <= dragEndY; y++) {
            for (int x = dragStartX; x <= dragEndX; x++) {
                if (!smartDrag) {
                    //스마트드래그가 꺼져 있다면 드래그한 모든 칸을 dragTemp 에 저장한다.
                    dragTemp[y][x] = true;
                } else if (board.getCell(y, x).getCurrentValue() == board.getCell(touchStartY, touchStartX).getCurrentValue()) {
                    //본인이 처음 선택한 것이랑 현재 드래그된 칸이 똑같은 유형이라면
                    //dragTemp 에 해당 칸을 저장해 둔다.
                    dragTemp[y][x] = true;
                }
            }
        }
        if (dragCount > 1) {
            tv_count.setText(String.valueOf(dragCount));
            View firstTouchView = glm.findViewByPosition(touchStartY * board.getWidth() + touchStartX);

            assert firstTouchView != null;

            cl_count.setX((float) (firstTouchView.getX() + viewSize * 0.1));
            cl_count.setY((float) (firstTouchView.getY() + viewSize * 0.1));

            cl_count.setLayoutParams(new ConstraintLayout.LayoutParams((int) viewSize, (int) viewSize));
        }


        View firstDragView = glm.findViewByPosition(dragStartY * board.getWidth() + dragStartX);
        View lastDragView = glm.findViewByPosition(dragEndY * board.getWidth() + dragEndX);

        assert firstDragView != null;
        assert lastDragView != null;

        int dragWidth = (int) (lastDragView.getX() - firstDragView.getX() + lastDragView.getWidth()) + 16;
        int dragHeight = (int) (lastDragView.getY() - firstDragView.getY() + lastDragView.getHeight()) + 16;

        ll_drag.setX(firstDragView.getX() - 8);
        ll_drag.setY(firstDragView.getY() - 8);

        ll_drag.setLayoutParams(new ConstraintLayout.LayoutParams(dragWidth, dragHeight));
    }

    public void checkAutoX() {
        if (!autoX) {
            //autoX 옵션이 꺼져있으면 바로 종료
            return;
        }

        //row 의 경우
        for (int row = 0; row < board.getHeight(); row++) {
            if (rowIndexDataManager.isIdxComplete[row]) {
                //해당 row 가 완성됐다면
                for (int i = 0; i < board.getWidth(); i++) {
                    Cell targetCell = board.getCell(row, i);
                    if (targetCell.getCurrentValue() == 0) {
                        //해당 칸이 공백이면 x로 채워준다.
                        targetCell.setCurrentValue((byte) 2);
                    }
                }
            }
        }

        //column 의 경우
        for (int column = 0; column < board.getWidth(); column++) {
            if (columnIndexDataManager.isIdxComplete[column]) {
                //해당 column 가 완성됐다면
                for (int i = 0; i < board.getHeight(); i++) {
                    Cell targetCell = board.getCell(i, column);
                    if (targetCell.getCurrentValue() == 0) {
                        //해당 칸이 공백이면 x로 채워준다.
                        targetCell.setCurrentValue((byte) 2);
                    }
                }
            }
        }
    }

    private void pushDragValues() {
        //드래그한 내용을 dataSet 에 저장해 줌

        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {

                Cell targetCell = board.getCell(y, x);

                if (dragTemp[y][x]) {


                    switch (macroMode) {
                        case 0:
                        case 1:
                        case 2:
                            //입력된 값을 그대로 push
                            targetCell.push((byte) macroMode);
                            break;
                        case 3:
                            //해당 셀의 힌트 사용
                            if(hintCount < 1)
                                break;
                            if (targetCell.useHint()) {
                                //힌트를 사용했으면 힌트 카운트를 줄여준다.
                                hintCount--;
                            }

                    }
                }
                else {
                    targetCell.push(targetCell.getCurrentValue());
                }

            }
        }
    }

    private void setMacroMode(int pos) {
        int x = pos % board.getWidth();
        int y = pos / board.getWidth();

        touchStartX = x;
        touchStartY = y;

        byte touchedCellValue = board.getCell(y, x).getCurrentValue();

        switch (touchMode) {
            case 0:
                //체크모드
                if (touchedCellValue == 1) {
                    //체크 -> 공백
                    macroMode = 0;
                } else {
                    //체크
                    macroMode = 1;
                }
                break;
            case 1:
                //X모드
                if (touchedCellValue == 2) {
                    //X -> 공백
                    macroMode = 0;
                } else {
                    //X
                    macroMode = 2;
                }
                break;
            case 2:
                //hint 모드
                macroMode = 3;
                break;
        }
    }

    private void setText() {
        String str_stack = board.getStackIdx() + "/" + board.getStackMax();

        tv_stack.setText(str_stack);
        tv_hint.setText(String.valueOf(hintCount));
    }

    private void moveToEndFragment() {
        //클릭 시 해당 게임을 플레이하는 GameActivity 로 이동
        EndFragment endFragment = new EndFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("leveldto", saveLevelDto);

        endFragment.setArguments(bundle);

        ((MainActivity) ctx).fragmentMoveNoStack(endFragment);
    }

    private void clickUpAction() {
        pushDragValues();
        removeDragTemp();
        updateNumColor();
        checkAutoX();
        setText();
        refreshBoard();

        if (board.isBoardComplete()) {
            completeGame();
        }
    }

    private void clickAction(int pos) {
        touchEndX = pos % board.getWidth();
        touchEndY = pos / board.getWidth();

        View firstTouchView = glm.findViewByPosition(pos);

        assert firstTouchView != null;
        viewSize = Math.min(firstTouchView.getMeasuredHeight(), firstTouchView.getMeasuredHeight());

        viewSize *= 0.8;

        dragManage();
        refreshBoard();
        showDrag();
    }

    private final BoardItemTouchListener boardTouchListener = new BoardItemTouchListener("touchable") {

        @Override
        public void onDownTouchableView(int pos) {

            setMacroMode(pos);
            clickAction(pos);
        }

        @Override
        public void onMoveTouchableView(int pos) {

            clickAction(pos);
        }


        @Override
        public void onClickUp(int pos, RecyclerView.ViewHolder holder) {
            clickUpAction();
        }

        @Override
        public void onLongClickUp(int pos, RecyclerView.ViewHolder holder) {
            clickUpAction();
        }

        @Override
        public void onDragMultiUp(int endPos, RecyclerView.ViewHolder holder) {
            clickUpAction();
        }


    };

    public void updateNumColor() {
        for (int i = 0; i < board.getWidth(); i++) {
            rvColumnAdapter.refreshView(i);
        }
        for (int i = 0; i < board.getHeight(); i++) {
            rvRowAdapter.refreshView(i);
        }
    }

    public void refreshBoard() {
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                ImageView view = Objects.requireNonNull(glm.findViewByPosition(x + y * board.getWidth())).findViewById(R.id.iv_item_board);

                if (view == null) {
                    continue;
                }

                Cell targetCell = board.getCell(y, x);

                if (targetCell.isHinted()) {
                    switch (targetCell.getCurrentValue()) {
                        case 0:
                        case 2:
                            view.setImageResource(R.drawable.background_x);
                            view.setBackgroundColor(Color.parseColor("#c0c0c0"));
                            break;
                        case 1:
                            view.setImageDrawable(null);
                            view.setBackgroundColor(Color.parseColor("#404040"));
                            break;
                    }
                } else {
                    switch (targetCell.getCurrentValue()) {
                        case 0:
                            view.setImageDrawable(null);
                            view.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            break;
                        case 1:
                            view.setImageDrawable(null);
                            view.setBackgroundColor(Color.parseColor("#000000"));
                            break;
                        case 2:
                            view.setImageResource(R.drawable.background_x);
                            view.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            break;
                    }
                }


            }
        }
    }

    private void showDrag() {
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                ImageView view = (ImageView) glm.findViewByPosition(x + y * board.getWidth());
                if (view == null) {
                    continue;
                }

                if (dragTemp[y][x]) {
                    //dragTemp 가 true 인 경우 드래그된 칸은 macroMode 에 맞게 그래픽을 갱신해 준다.
                    switch (macroMode) {
                        case 0:
                            //공백
                            view.setImageDrawable(null);
                            view.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            break;
                        case 1:
                            //체크
                            view.setImageDrawable(null);
                            view.setBackgroundColor(Color.parseColor("#000000"));
                            break;
                        case 2:
                            //X
                            view.setImageResource(R.drawable.background_x);
                            view.setBackgroundColor(Color.parseColor("#ffffff"));
                            break;
                    }
                }
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    public void makeGameBoard() {
        Log.d("GameController", "makeGameBoard start");

        //로직 게임판을 만듭니다.
        //터치가 가능한 보드판 제작
        glm = new GridLayoutManager(targetView.getContext(), board.getWidth());
        LinearLayoutManager columnLayoutManager = new LinearLayoutManager(targetView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager rowLayoutManager = new LinearLayoutManager(targetView.getContext());

        RvBoardAdapter rba = new RvBoardAdapter(board.getWidth(), board.getHeight());

        rv_board.addItemDecoration(new GameBoardBorder(targetView.getContext(), R.drawable.border_gameboard_normal, R.drawable.border_gameboard_accent, board.getWidth()));

        rv_board.setLayoutManager(glm);
        rv_board.addOnItemTouchListener(boardTouchListener);
        rv_board.setAdapter(rba);

        columnIndexDataManager = new ColumnIndexDataManager(board);
        rowIndexDataManager = new RowIndexDataManager(board);
        columnIndexDataManager.makeIdxDataSet();
        rowIndexDataManager.makeIdxDataSet();

        rvColumnAdapter = new RvColumnAdapter(columnIndexDataManager);
        rvRowAdapter = new RvRowAdapter(rowIndexDataManager);

        rv_column.setLayoutManager(columnLayoutManager);
        rv_row.setLayoutManager(rowLayoutManager);
        rv_column.setAdapter(rvColumnAdapter);
        rv_row.setAdapter(rvRowAdapter);

        rv_board.post(() -> {
            checkAutoX();
            refreshBoard();
        });
        rv_column.post(() -> {
            for (int i = 0; i < board.getWidth(); i++) {
                rvColumnAdapter.refreshView(i);
            }
        });

        rv_row.post(() -> {
            for (int i = 0; i < board.getHeight(); i++) {
                rvRowAdapter.refreshView(i);
            }
        });

        setText();
    }

    public int func_toggle() {
        if (touchMode == 0) {
            touchMode = 1;
        } else {
            touchMode = 0;
        }

        return touchMode;
    }

    public void func_hint() {
        touchMode = 2;
    }

    public void moveToNext() {
        if (board.moveToNext()) {
            refreshBoard();
            updateNumColor();
            setText();
        }
    }

    public void moveToPrev() {
        if (board.moveToPrev()) {
            refreshBoard();
            updateNumColor();
            setText();
        }
    }


    //saving methods
    public void onDestroy() {
        if (progress != Progress.PROGRESS_COMPLETE && progress != Progress.PROGRESS_RECOMPLETE) {
            saveGame();
        }
    }

    private void saveGame() {
        Log.d("GameController", "saveGame Called");
        makeSaveLevelDto();
        saveSaveLevelDto();
    }

    public void completeGame() {
        Log.d("GameController", "completeGame Called");
        setProgressComplete();
        makeSaveLevelDto();
        saveSaveLevelDto();
        moveToEndFragment();
    }

    private void setProgressComplete() {
        //한 번이라도 완료한 기록이 있으면 reComplete 로 저장, 처음 완료하는거면 complete 로 저장
        Log.d("GameController", "setProgressComplete called");

        Log.d("GameController", "before setProgressComplete progress: " + progress);
        switch (progress) {
            case Progress.PROGRESS_COMPLETE:
            case Progress.PROGRESS_REPLAYING:
            case Progress.PROGRESS_RECOMPLETE:
                Log.d("GameController", "setProgressComplete case upper");
                progress = Progress.PROGRESS_RECOMPLETE;
                break;
            case Progress.PROGRESS_FIRST:
            case Progress.PROGRESS_PLAYING:
                Log.d("GameController", "setProgressComplete case lower");
                progress = Progress.PROGRESS_COMPLETE;
                break;
        }

        Log.d("GameController", "after setProgressComplete progress: " + progress);
    }

    private void setProgressPlaying() {
        //한 번이라도 완료한 기록이 있으면 replaying 으로 저장, 처음이면 playing 으로 저장
        Log.d("GameController", "setProgressPlaying called");
        switch (levelDto.getProgress()) {
            case Progress.PROGRESS_COMPLETE:
            case Progress.PROGRESS_REPLAYING:
            case Progress.PROGRESS_RECOMPLETE:
                progress = Progress.PROGRESS_REPLAYING;
                break;
            case Progress.PROGRESS_FIRST:
            case Progress.PROGRESS_PLAYING:
                progress = Progress.PROGRESS_PLAYING;
                break;
        }
    }

    private void makeSaveLevelDto() {

        int levelId = levelDto.getLevelId();
        int puzzleId = levelDto.getPuzzleId();
        String name = levelDto.getName();
        int number = levelDto.getNumber();
        int width = levelDto.getWidth();
        int height = levelDto.getHeight();
        byte[] dataSet = levelDto.getDataSet();
        byte[] saveBlob = SaveData.of(board);
        byte[] colorBlob = levelDto.getColorBlob();
        boolean isCustom = levelDto.isCustom();

        saveLevelDto = new LevelDto(levelId, puzzleId, name, number, width, height, progress, dataSet, saveBlob, colorBlob, isCustom);

        Log.d("GameController", "makeSaveLevelDtd: " + saveLevelDto.toString());
    }

    private void saveSaveLevelDto() {

        Log.d("GameController", "saveSaveLevelDto called");
        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();

            Log.d("GameController", "saveSaveLevelDto: " + saveLevelDto.toString());
            mDbOpenHelper.saveLevelDto(saveLevelDto);

            mDbOpenHelper.close();

            SharedPreferences sharedPreferences = ctx.getSharedPreferences("PROPERTY", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putInt("hint", hintCount);
            editor.apply();

            SharedPreferences lastPlayPref = ctx.getSharedPreferences("LASTPLAY", MODE_PRIVATE);
            editor = lastPlayPref.edit();

            editor.putInt("id", saveLevelDto.getLevelId());
            editor.putBoolean("custom", saveLevelDto.isCustom());

            editor.apply();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
