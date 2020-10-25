package com.example.nemologic.game;

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

import com.example.nemologic.R;
import com.example.nemologic.data.LevelPlayManager;
import com.example.nemologic.end.EndFragment;
import com.example.nemologic.listener.BoardItemTouchListener;
import com.example.nemologic.mainactivity.MainActivity;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class GameBoard {


    boolean smartDrag;
    boolean oneLineDrag;
    boolean autoX;

    Context ctx;

    private View targetView;

    private RecyclerView rv_board;
    private RecyclerView rv_column;
    private RecyclerView rv_row;

    private GridLayoutManager glm;
    private LinearLayoutManager columnLayoutManager;
    private LinearLayoutManager rowLayoutManager;
    public LevelPlayManager lpm;

    private ColumnIndexDataManager columnIndexDataManager;
    private RowIndexDataManager rowIndexDataManager;

    private RvColumnAdapter rvColumnAdapter;
    private RvRowAdapter rvRowAdapter;

    private TextView tv_stack;
    private TextView tv_hint;
    private TextView tv_count;

    private ConstraintLayout cl_count;
    private LinearLayout ll_drag;

    int touchStartX;
    int touchStartY;
    int touchEndX;
    int touchEndY;

    int hintCount;

    float viewSize;

    boolean[][] dragTemp;
    boolean[][] dragIndexTemp;
    boolean[][] hint;

    //0: 체크, 1: X, 2: 힌트
    int touchMode = 0;
    //0: 공백 1: 체크 2: X 3: 힌트(정답)
    int macroMode = 0;
    
    public GameBoard(Context ctx, View view, LevelPlayManager lpm)
    {
        targetView = view;
        this.lpm = lpm;
        this.ctx = ctx;

        initialize();
    }

    public void saveGame()
    {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("PROPERTY", MODE_PRIVATE);
        lpm.savePlayData(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("hint", hintCount);
        editor.apply();
    }

    public void loadPref()
    {
        //option preferences
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("OPTION", MODE_PRIVATE);
        smartDrag = sharedPreferences.getBoolean("smartDrag", true);
        oneLineDrag = sharedPreferences.getBoolean("oneLineDrag", true);
        autoX = sharedPreferences.getBoolean("autoX", false);

        //hint properties
        sharedPreferences = ctx.getSharedPreferences("PROPERTY", MODE_PRIVATE);
        hintCount = sharedPreferences.getInt("hint", 3);
    }

    private void initialize()
    {
        dragTemp = new boolean[lpm.height][lpm.width];
        dragIndexTemp = new boolean[lpm.height][lpm.width];
        hint = new boolean[lpm.height][lpm.width];
        rv_board = targetView.findViewById(R.id.rv_board);
        rv_row = targetView.findViewById(R.id.rv_row);
        rv_column = targetView.findViewById(R.id.rv_column);

        cl_count = targetView.findViewById(R.id.cl_count);
        ll_drag = targetView.findViewById(R.id.ll_drag);

        tv_count = targetView.findViewById(R.id.tv_count);
        tv_stack = targetView.findViewById(R.id.tv_stack);
        tv_hint = targetView.findViewById(R.id.tv_hint);

        loadPref();
    }


    //DRAG////////////////////////////////
    private void removeDragTemp()
    {
        for(int y = 0; y < lpm.height; y++)
        {
            for(int x = 0; x < lpm.width; x++)
            {
                dragTemp[y][x] = false;
                dragIndexTemp[y][x] = false;
            }
        }

        cl_count.setLayoutParams(new ConstraintLayout.LayoutParams(0, 0));
        ll_drag.setLayoutParams(new ConstraintLayout.LayoutParams(0, 0));
    }

    private void dragManage()
    {
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
        if(touchStartX > touchEndX)
        {
            dragStartX = touchEndX;
            dragEndX = touchStartX;
        }
        else
        {
            dragStartX = touchStartX;
            dragEndX = touchEndX;
        }

        if(touchStartY > touchEndY)
        {
            dragStartY = touchEndY;
            dragEndY = touchStartY;
        }
        else
        {
            dragStartY = touchStartY;
            dragEndY = touchEndY;
        }


        if(oneLineDrag)
        {
            //한 줄만 드래그하기 옵션이 켜져 있을 경우
            if(dragEndX - dragStartX > dragEndY - dragStartY)
            {
                //가로 방향 드래그가 더 길 경우
                //가로 방향 한 줄만 드래그시킨다.
                dragCount = dragEndX - dragStartX + 1;
                dragEndY = touchStartY;
                dragStartY = touchStartY;

                //마지막으로 선택된 칸의 위치를 lastX와 lastY에 저장해 둔다.
                lastX = touchEndX;
                lastY = touchStartY;
            }
            else
            {
                //세로 방향 드래그가 더 길 경우
                dragCount = dragEndY - dragStartY + 1;
                dragEndX = touchStartX;
                dragStartX = touchStartX;

                lastX = touchStartX;
                lastY = touchEndY;
            }
        }
        else
        {
            //마지막으로 터치한 지점의 좌표값과 드래그한 넓이를 대입해준다.
            lastX = touchEndX;
            lastY = touchEndY;
            dragCount = (dragEndX - dragStartX + 1) * (dragEndY - dragStartY + 1);
        }


        //마지막 드래그한 칸의 가로줄과 세로줄에 하이라이트를 해 준다.
        for(int y = 0; y < lpm.height; y++)
        {
            dragIndexTemp[y][lastX] = true;
        }
        for(int x = 0; x < lpm.width; x++)
        {
            dragIndexTemp[lastY][x] = true;
        }


        for(int y = dragStartY; y <= dragEndY; y++)
        {
            for(int x = dragStartX; x <= dragEndX; x++)
            {
                if(!smartDrag)
                {
                    //스마트드래그가 꺼져 있다면 드래그한 모든 칸을 dragTemp 에 저장한다.
                    dragTemp[y][x] = true;
                }
                else if(lpm.checkedSet[y*lpm.width + x] == lpm.checkedSet[touchStartY * lpm.width + touchStartX])
                {
                    //본인이 처음 선택한 것이랑 현재 드래그된 칸이 똑같은 유형이라면
                    //dragTemp 에 해당 칸을 저장해 둔다.
                    dragTemp[y][x] = true;
                }
            }
        }
        if(dragCount > 1)
        {
            tv_count.setText(String.valueOf(dragCount));
            View firstTouchView = glm.findViewByPosition(touchStartY * lpm.width + touchStartX);

            assert firstTouchView != null;

            cl_count.setX((float) (firstTouchView.getX() + viewSize*0.1));
            cl_count.setY((float) (firstTouchView.getY() + viewSize*0.1));

            cl_count.setLayoutParams(new ConstraintLayout.LayoutParams((int)viewSize, (int)viewSize));
        }


        View firstDragView = glm.findViewByPosition(dragStartY * lpm.width + dragStartX);
        View lastDragView = glm.findViewByPosition(dragEndY * lpm.width + dragEndX);

        assert firstDragView != null;
        assert lastDragView != null;

        int dragWidth = (int) (lastDragView.getX() - firstDragView.getX() + lastDragView.getWidth()) + 16;
        int dragHeight = (int) (lastDragView.getY() - firstDragView.getY() + lastDragView.getHeight()) + 16;

        ll_drag.setX(firstDragView.getX() - 8);
        ll_drag.setY(firstDragView.getY() - 8);

        ll_drag.setLayoutParams(new ConstraintLayout.LayoutParams(dragWidth, dragHeight));
    }
    
    public void checkAutoX()
    {
        if(!autoX)
        {
            //autoX 옵션이 꺼져있으면 바로 종료
            return;
        }
        
        //row 의 경우
        for(int row = 0; row < lpm.height; row++)
        {
            if(rowIndexDataManager.isIdxComplete[row])
            {
                //해당 row 가 완성됐다면
                for(int i = 0; i < lpm.width; i++)
                {
                    if(lpm.checkedSet[row * lpm.width + i] == 0)
                    {
                        //해당 칸이 공백이면 x로 채워준다.
                        lpm.checkedSet[row * lpm.width + i] = 2;
                    }
                }
            }
        }

        //column 의 경우
        for(int column = 0; column < lpm.width; column++)
        {
            if(columnIndexDataManager.isIdxComplete[column])
            {
                //해당 column 가 완성됐다면
                for(int i = 0; i < lpm.height; i++)
                {
                    if(lpm.checkedSet[i * lpm.width + column] == 0)
                    {
                        //해당 칸이 공백이면 x로 채워준다.
                        lpm.checkedSet[i * lpm.width + column] = 2;
                    }
                }
            }
        }
    }

    private void setData()
    {
        //드래그한 내용을 dataSet 에 저장해 줌

        for(int y = 0; y < lpm.height; y++)
        {
            for(int x = 0; x < lpm.width; x++)
            {
                if(dragTemp[y][x] && !hint[y][x])
                {
                    switch(macroMode)
                    {
                        case 0:
                            //공백
                            lpm.checkedSet[y * lpm.width + x] = 0;
                            break;
                        case 1:
                            //체크
                            lpm.checkedSet[y * lpm.width + x] = 1;
                            break;
                        case 2:
                            //X
                            lpm.checkedSet[y * lpm.width + x] = 2;
                            break;
                        case 3:
                            //힌트
                            if(hintCount > 0)
                            {
                                hint[y][x] = true;
                                lpm.checkedSet[y * lpm.width + x] = lpm.dataSet[y * lpm.width + x];
                                hintCount--;
                            }

                            break;

                    }
                }
            }
        }
    }

    private void setMacroMode(int pos)
    {
        int x = pos % lpm.width;
        int y = pos / lpm.width;

        touchStartX = x;
        touchStartY = y;

        if(touchMode == 0)
        {
            //체크모드
            if(lpm.checkedSet[y * lpm.width + x] == 1)
            {
                //체크 -> 공백
                macroMode = 0;
            }
            else
            {
                //체크
                macroMode = 1;
            }
        }
        else if(touchMode == 1)
        {
            //X모드
            if(lpm.checkedSet[y * lpm.width + x] == 2)
            {
                //X -> 공백
                macroMode = 0;
            }
            else
            {
                //X
                macroMode = 2;
            }
        }
        else if(touchMode == 2)
        {
            //hint 모드
            macroMode = 3;
        }
    }

    private void setText()
    {
        String str_stack = lpm.getStackNum() + "/" + lpm.getStackMaxNum();

        tv_stack.setText(str_stack);
        tv_hint.setText(String.valueOf(hintCount));
    }

    private void moveToEndFragment()
    {
        //클릭 시 해당 게임을 플레이하는 GameActivity 로 이동
        EndFragment endFragment = new EndFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", lpm.id);
        bundle.putInt("type", lpm.type);


        endFragment.setArguments(bundle);

        ((MainActivity) ctx).fragmentMoveNoStack(endFragment);
    }

    private void setGameEnd()
    {
        lpm.progress = 2;
        saveGame();
    }


    private void clickUpAction()
    {
        setData();
        removeDragTemp();
        updateNumColor();
        checkAutoX();
        lpm.pushCheckStack();
        setText();
        refreshBoard();

        if(lpm.isGameEnd())
        {
            setGameEnd();
            moveToEndFragment();
        }
    }

    private void clickAction(int pos)
    {
        touchEndX = pos % lpm.width;
        touchEndY = pos / lpm.width;

        View firstTouchView = glm.findViewByPosition(pos);

        assert firstTouchView != null;
        viewSize = Math.min(firstTouchView.getMeasuredHeight(), firstTouchView.getMeasuredHeight());

        viewSize *= 0.8;

        dragManage();
        refreshBoard();
        showDrag();
    }

    private BoardItemTouchListener boardTouchListener = new BoardItemTouchListener("touchable") {

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
        public void onClickUp(int pos, RecyclerView.ViewHolder holder)
        {
            clickUpAction();
        }

        @Override
        public void onLongClickUp(int pos, RecyclerView.ViewHolder holder)
        {
            clickUpAction();
        }

        @Override
        public void onDragMultiUp(int endPos, RecyclerView.ViewHolder holder)
        {
            clickUpAction();
        }


    };

    public void updateNumColor()
    {
        for(int i = 0; i < lpm.width; i++)
        {
            rvColumnAdapter.refreshView(i);
        }
        for(int i = 0; i < lpm.height; i++)
        {
            rvRowAdapter.refreshView(i);
        }
    }

    public void refreshBoard()
    {
        for(int y = 0; y < lpm.height; y++)
        {
            for(int x = 0; x < lpm.width; x++)
            {
                ImageView view = Objects.requireNonNull(glm.findViewByPosition(x + y * lpm.width)).findViewById(R.id.iv_item_board);

                if(view == null)
                {
                    continue;
                }

                if(hint[y][x])
                {
                    switch(lpm.checkedSet[y * lpm.width + x])
                    {
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
                }
                else
                {
                    switch(lpm.checkedSet[y * lpm.width + x])
                    {
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

    private void showDrag()
    {
        for(int y = 0; y < lpm.height; y++)
        {
            for(int x = 0; x < lpm.width; x++)
            {
                ImageView view =  (ImageView) glm.findViewByPosition(x + y * lpm.width);
                if(view == null)
                {
                    continue;
                }

                if(dragTemp[y][x])
                {
                    //dragTemp 가 true 인 경우 드래그된 칸은 macroMode 에 맞게 그래픽을 갱신해 준다.
                    //만약 dragIndexTemp 또한 true 일 경우 드래그인덱스 그래픽을 표시, 이외에는 일반 그래픽 표시
                    if(dragIndexTemp[y][x])
                    {
                        switch(macroMode)
                        {
                            case 0:
                                view.setImageDrawable(null);
                                view.setBackgroundColor(Color.parseColor("#82C3FF"));
                                break;
                            case 1:
                                view.setImageDrawable(null);
                                view.setBackgroundColor(Color.parseColor("#406F9A"));
                                break;
                            case 2:
                                view.setImageResource(R.drawable.background_x);
                                view.setBackgroundColor(Color.parseColor("#82C3FF"));
                                break;
                        }
                    }
                    else
                    {
                        switch(macroMode)
                        {
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
                else if(dragIndexTemp[y][x])
                {
                    switch(lpm.checkedSet[y * lpm.width + x])
                    {
                        case 0:
                            view.setImageDrawable(null);
                            view.setBackgroundColor(Color.parseColor("#82C3FF"));
                            break;
                        case 1:
                            view.setImageDrawable(null);
                            view.setBackgroundColor(Color.parseColor("#406F9A"));
                            break;
                        case 2:
                            view.setImageResource(R.drawable.background_x);
                            view.setBackgroundColor(Color.parseColor("#82C3FF"));
                            break;
                    }
                }
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    public void makeGameBoard()
    {
        //로직 게임판을 만듭니다.
        //터치가 가능한 보드판 제작
        glm = new GridLayoutManager(targetView.getContext(), lpm.width);
        columnLayoutManager = new LinearLayoutManager(targetView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        rowLayoutManager = new LinearLayoutManager(targetView.getContext());

        RvBoardAdapter rba = new RvBoardAdapter(lpm.width, lpm.height);

        rv_board.addItemDecoration(new GameBoardBorder(targetView.getContext(), R.drawable.border_gameboard_normal, R.drawable.border_gameboard_accent, lpm.width));

        rv_board.setLayoutManager(glm);
        rv_board.addOnItemTouchListener(boardTouchListener);
        rv_board.setAdapter(rba);

        columnIndexDataManager = new ColumnIndexDataManager(lpm);
        rowIndexDataManager = new RowIndexDataManager(lpm);
        columnIndexDataManager.makeIdxDataSet();
        rowIndexDataManager.makeIdxDataSet();

        rvColumnAdapter = new RvColumnAdapter(columnIndexDataManager);
        rvRowAdapter = new RvRowAdapter(rowIndexDataManager);

        rv_column.setLayoutManager(columnLayoutManager);
        rv_row.setLayoutManager(rowLayoutManager);
        rv_column.setAdapter(rvColumnAdapter);
        rv_row.setAdapter(rvRowAdapter);

        rv_board.post(new Runnable() {
            @Override
            public void run() {
                checkAutoX();
                refreshBoard();
            }
        });
        rv_column.post(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < lpm.width; i++)
                {
                    rvColumnAdapter.refreshView(i);
                }
            }
        });

        rv_row.post(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < lpm.height; i++)
                {
                    rvRowAdapter.refreshView(i);
                }
            }
        });

        setText();
    }

    public int func_toggle()
    {
        if(touchMode == 0)
        {
            touchMode = 1;
        }
        else
        {
            touchMode = 0;
        }

        return touchMode;
    }

    public void func_hint()
    {
        touchMode = 2;
    }

    public void func_nextStack()
    {
        lpm.nextCheckStack();

        refreshBoard();
        updateNumColor();
        setText();
    }

    public void func_prevStack()
    {
        lpm.prevCheckStack();

        refreshBoard();
        updateNumColor();
        setText();
    }
}
