package com.sinwindis.logicgallery.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
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
import com.sinwindis.logicgallery.data.HintManager;
import com.sinwindis.logicgallery.data.LevelDto;
import com.sinwindis.logicgallery.data.Progress;
import com.sinwindis.logicgallery.data.SaveData;
import com.sinwindis.logicgallery.data.sharedpref.LastPlayPreference;
import com.sinwindis.logicgallery.data.sharedpref.OptionPreference;
import com.sinwindis.logicgallery.end.EndFragment;
import com.sinwindis.logicgallery.listener.BoardItemTouchListener;
import com.sinwindis.logicgallery.mainactivity.MainActivity;

import java.sql.SQLException;
import java.util.Objects;

public class GameBoardController {

    private final int SIZE_BORDER = 8;
    private final int OFFSET_COUNT_X = 100;
    private final int OFFSET_COUNT_Y = 200;

    //property
    private boolean smartDrag;
    private boolean oneLineDrag;
    private boolean autoX;

    //drag values
    private DragManager dragManager;
    private TouchRangeManager touchRangeManager;

    private int progress;

    private float dragOffsetX;
    private float dragOffsetY;

    private final Context ctx;

    //Model
    public Board board;
    private HintManager hintManager;
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

    private TouchMode touchMode;
    private MacroMode macroMode;


    public GameBoardController(Context ctx, View view, LevelDto levelDto) {
        targetView = view;
        this.levelDto = levelDto;
        this.ctx = ctx;

        initialize();
    }

    public void setTouchMode(TouchMode touchMode) {
        this.touchMode = touchMode;
    }

    private void initialize() {
        Log.d("GameController", levelDto.toString());

        board = new Board(levelDto.getHeight(), levelDto.getWidth());
        board.setCorrectValues(levelDto.getDataSet());
        board.loadSaveData(levelDto.getSaveData());
        hintManager = new HintManager(ctx);

        Log.d("initialize", "board width: " + board.getWidth() + "board height: " + board.getHeight());

        rv_board = targetView.findViewById(R.id.rv_board);
        rv_row = targetView.findViewById(R.id.cl_row);
        rv_column = targetView.findViewById(R.id.cl_column);

        cl_count = targetView.findViewById(R.id.cl_count);
        ll_drag = targetView.findViewById(R.id.ll_drag);

        tv_count = targetView.findViewById(R.id.tv_count);
        tv_stack = targetView.findViewById(R.id.tv_stack);
        tv_hint = targetView.findViewById(R.id.tv_hint);

        macroMode = new MacroMode();

        dragManager = new DragManager();
        touchRangeManager = new TouchRangeManager();
        dragManager.setTouchRangeManager(touchRangeManager);

        Log.d("GameController", "initialize end");

        loadPref();
        setProgressPlaying();

        makeGameBoardView();
    }

    public void loadPref() {
        //option preferences
        OptionPreference optionPreference = new OptionPreference(ctx);
        smartDrag = optionPreference.isSmartDrag();
        oneLineDrag = optionPreference.isOneLineDrag();
        dragManager.setOneLineDrag(oneLineDrag);
        autoX = optionPreference.isAutoX();
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

    private void useHint(Cell cell) {
        if (cell.useHint()) {
            hintManager.setHintCount(hintManager.getHintCount() - 1);
            refreshHintView();
        }
    }

    private void setCell(Cell targetCell) {

        if (smartDrag) {
            switch (macroMode.getMacroMode()) {
                case MacroMode.CHECK:
                    if (targetCell.getCurrentValue() == Cell.BLANK) {
                        targetCell.setCurrentValue(Cell.CHECK);
                    }
                    break;
                case MacroMode.UNCHECK:
                    if (targetCell.getCurrentValue() == Cell.CHECK) {
                        targetCell.setCurrentValue(Cell.BLANK);
                    }
                    break;
                case MacroMode.X:
                    if (targetCell.getCurrentValue() == Cell.BLANK) {
                        targetCell.setCurrentValue(Cell.X);
                    }
                    break;
                case MacroMode.UNX:
                    if (targetCell.getCurrentValue() == Cell.X) {
                        targetCell.setCurrentValue(Cell.BLANK);
                    }
                    break;
                case MacroMode.LOCK:
                    targetCell.setFixed(true);
                    break;
                case MacroMode.UNLOCK:
                    targetCell.setFixed(false);
                    break;
                case MacroMode.HINT:
                    useHint(targetCell);
                    break;
            }
        } else {
            switch (macroMode.getMacroMode()) {
                case MacroMode.CHECK:
                    targetCell.setCurrentValue(Cell.CHECK);
                    break;
                case MacroMode.UNCHECK:
                case MacroMode.UNX:
                    targetCell.setCurrentValue(Cell.BLANK);
                    break;
                case MacroMode.X:
                    targetCell.setCurrentValue(Cell.X);
                    break;
                case MacroMode.LOCK:
                    targetCell.setFixed(true);
                    break;
                case MacroMode.UNLOCK:
                    targetCell.setFixed(false);
                    break;
                case MacroMode.HINT:
                    useHint(targetCell);
                    break;
            }
        }
    }

    private void setCellValues() {
        //드래그한 내용을 dataSet 에 저장해 줌

        Point endPoint = dragManager.getEndPoint();
        Point startPoint = dragManager.getStartPoint();

        for (int y = startPoint.y; y <= endPoint.y; y++) {
            for (int x = startPoint.x; x <= endPoint.x; x++) {
                setCell(board.getCell(y, x));
            }
        }
    }

    private void setMacroMode() {
        Point point = touchRangeManager.getStartPoint();
        Cell touchedCell = board.getCell(point);
        byte touchedCellValue = touchedCell.getCurrentValue();

        switch (touchMode.getTouchMode()) {
            case TouchMode.TOUCH_CHECK:
                if (touchedCellValue == Cell.CHECK) {
                    //체크 -> 공백
                    macroMode.setMacroMode(MacroMode.UNCHECK);
                } else {
                    //체크
                    macroMode.setMacroMode(MacroMode.CHECK);
                }
                break;
            case TouchMode.TOUCH_X:
                //X모드
                if (touchedCellValue == Cell.X) {
                    //X -> 공백
                    macroMode.setMacroMode(MacroMode.UNX);
                } else {
                    //X
                    macroMode.setMacroMode(MacroMode.X);
                }
                break;
            case TouchMode.TOUCH_LOCK:
                if (touchedCell.isFixed()) {
                    macroMode.setMacroMode(MacroMode.UNLOCK);
                } else {
                    macroMode.setMacroMode(MacroMode.LOCK);
                }
                break;
            case TouchMode.TOUCH_HINT:
                //hint 모드
                macroMode.setMacroMode(MacroMode.HINT);
                break;
        }
    }

    private void moveToEndFragment() {
        //클릭 시 해당 게임을 플레이하는 GameActivity 로 이동
        EndFragment endFragment = new EndFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("leveldto", saveLevelDto);

        endFragment.setArguments(bundle);

        ((MainActivity) ctx).fragmentMoveNoStack(endFragment);
    }

    //////////////////////////////////
    //click actions
    //////////////////////////////////
    private void clickAction(int pos) {

        touchRangeManager.setStartPoint(new Point(pos % board.getWidth(), pos / board.getWidth()));
        touchRangeManager.setEndPoint(new Point(pos % board.getWidth(), pos / board.getWidth()));
        setMacroMode();

        showDragBorderView();
    }

    private void moveAction(int pos) {
        touchRangeManager.setEndPoint(new Point(pos % board.getWidth(), pos / board.getWidth()));

        showDragBorderView();
        showDragCountView();
    }

    private void clickUpAction() {

        removeDragBorderView();
        removeDragCountView();

        //checkHintUse
        if (macroMode.getMacroMode() == MacroMode.HINT) {

            //show dialog

            if (!hintManager.useHint(dragManager.getDragCount())) {
                return;
            }
        }

        setCellValues();
        refreshIndexView();
        checkAutoX();
        board.push();

        Point startPoint = dragManager.getStartPoint();
        Point endPoint = dragManager.getEndPoint();
        refreshCellViews(startPoint.y, startPoint.x, endPoint.y, endPoint.x);
        refreshStackView();
        refreshHintView();

        if (board.isBoardComplete()) {
            completeGame();
        }
    }

    private void removeDragBorderView() {
        ll_drag.setVisibility(View.GONE);
    }

    private void removeDragCountView() {
        cl_count.setVisibility(View.GONE);
    }

    private final BoardItemTouchListener boardTouchListener = new BoardItemTouchListener("touchable") {

        @Override
        public void onDownTouchableView(int pos) {
            clickAction(pos);
            Log.d("BoardItemTouch", "onDown");
        }

        @Override
        public void onMoveTouchableView(int pos) {
            moveAction(pos);
            Log.d("BoardItemTouch", "onMove");
        }


        @Override
        public void onClickUp(int pos, RecyclerView.ViewHolder holder) {
            clickUpAction();
            Log.d("BoardItemTouch", "onUp");
        }

        @Override
        public void onLongClickUp(int pos, RecyclerView.ViewHolder holder) {
            clickUpAction();
            Log.d("BoardItemTouch", "onLongClickUp");
        }

        @Override
        public void onDragMultiUp(int endPos, RecyclerView.ViewHolder holder) {
            clickUpAction();
            Log.d("BoardItemTouch", "onDragMultiUp");
        }


    };


    @SuppressLint("ClickableViewAccessibility")
    public void makeGameBoardView() {
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
            refreshCellViews(0, 0, board.getHeight() - 1, board.getWidth() - 1);

            ConstraintLayout cl_board = targetView.findViewById(R.id.board);
            dragOffsetX = rv_board.getX() + cl_board.getX() - SIZE_BORDER;
            dragOffsetY = rv_board.getY() + cl_board.getY() - SIZE_BORDER;
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

        refreshStackView();
        refreshHintView();
    }

    public void moveToNext() {
        if (board.moveToNext()) {
            refreshCellViews(0, 0, board.getHeight() - 1, board.getWidth() - 1);
            refreshIndexView();
            refreshStackView();
        }
    }

    public void moveToPrev() {
        if (board.moveToPrev()) {
            refreshCellViews(0, 0, board.getHeight() - 1, board.getWidth() - 1);
            refreshIndexView();
            refreshStackView();
        }
    }


    //////////////////////////////
    //saving methods
    //////////////////////////////
    public void onDestroy() {
        if (progress != Progress.PROGRESS_COMPLETE && progress != Progress.PROGRESS_RECOMPLETE) {
            makeSaveLevelDto();
            saveSaveLevelDto();
        }
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

            hintManager.apply();

            LastPlayPreference lastPlayPreference = new LastPlayPreference(ctx);
            lastPlayPreference.setId(saveLevelDto.getLevelId());
            lastPlayPreference.setCustom(saveLevelDto.isCustom());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /////////////////////////////////
    //view refresher
    /////////////////////////////////
    @SuppressLint("SetTextI18n")
    private void refreshStackView() {
        tv_stack.setText(board.getCurrentStackNum() + "/" + board.getExploredStackNum());
    }

    private void refreshHintView() {
        tv_hint.setText(String.valueOf(hintManager.getHintCount()));
    }

    private void refreshIndexView() {
        for (int i = 0; i < board.getWidth(); i++) {
            rvColumnAdapter.refreshView(i);
        }
        for (int i = 0; i < board.getHeight(); i++) {
            rvRowAdapter.refreshView(i);
        }
    }

    private void refreshCellViews(int startY, int startX, int endY, int endX) {
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = startX; x <= endX; x++) {
                refreshCellView(y, x);
            }
        }

        for (int y = startY; y <= endY; y++) {
            for (int x = 0; x < startX; x++) {
                refreshCellView(y, x);
            }
            for (int x = endX + 1; x < board.getWidth(); x++) {
                refreshCellView(y, x);
            }
        }
    }

    private void refreshCellView(int y, int x) {
        ImageView view = Objects.requireNonNull(glm.findViewByPosition(x + y * board.getWidth())).findViewById(R.id.iv_item_board);

        if (view == null) {
            return;
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

    private void showDragBorderView() {
        Point startPoint = dragManager.getStartPoint();
        Point endPoint = dragManager.getEndPoint();

        View firstDragView = glm.findViewByPosition(startPoint.y * board.getWidth() + startPoint.x);
        View lastDragView = glm.findViewByPosition(endPoint.y * board.getWidth() + endPoint.x);

        assert firstDragView != null;
        assert lastDragView != null;

        int dragWidth = (int) (lastDragView.getX() - firstDragView.getX() + lastDragView.getWidth()) + SIZE_BORDER * 2;
        int dragHeight = (int) (lastDragView.getY() - firstDragView.getY() + lastDragView.getHeight()) + SIZE_BORDER * 2;

        ll_drag.setVisibility(View.VISIBLE);

        ll_drag.setX(dragOffsetX + firstDragView.getX());
        ll_drag.setY(dragOffsetY + firstDragView.getY());

        ll_drag.setLayoutParams(new ConstraintLayout.LayoutParams(dragWidth, dragHeight));
    }

    private void showDragCountView() {

        int dragCount = dragManager.getDragCount();

        if (dragCount > 1) {
            Point endPoint = touchRangeManager.getEndPoint();
            tv_count.setText(String.valueOf(dragCount));
            View lastDragView = glm.findViewByPosition(endPoint.y * board.getWidth() + endPoint.x);
            cl_count.setX(lastDragView.getX() + dragOffsetX - OFFSET_COUNT_X);
            cl_count.setY(lastDragView.getY() + dragOffsetY - OFFSET_COUNT_Y);
            cl_count.setVisibility(View.VISIBLE);
        }
    }


}
