package com.example.nemologic.gameactivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;
import com.example.nemologic.data.LevelPlayManager;

public class GameBoard {

    private RecyclerView rv_board;

    private GridLayoutManager glm;
    private LevelPlayManager lpm;

    ColumnIndexViewMaker civm;
    RowIndexViewMaker rivm;

    private TextView tv_stack;
    private TextView tv_count;

    private LinearLayout ll_count;

    private Button btn_toggle;
    private Button btn_prev;
    private Button btn_next;

    int touchStartX;
    int touchStartY;
    int touchEndX;
    int touchEndY;

    int[][] dragTemp;

    //0: 체크, 1: X
    int touchMode = 0;
    //0: 공백 -> 체크, 1: 공백 -> X, 2: 체크 -> 공백, 3: 체크 -> X, 4: X -> 공백, 5: X -> 체크
    int macroMode = 0;
    
    Context ctx;
    
    public GameBoard(Context ctx, LevelPlayManager lpm)
    {
        this.ctx = ctx;
        this.lpm = lpm;

        initialize();
    }

    private void initialize()
    {
        dragTemp = new int[lpm.height][lpm.width];
        ll_count = ((Activity)ctx).findViewById(R.id.ll_count);
        btn_toggle = ((Activity)ctx).findViewById(R.id.img_toggle);
        btn_prev = ((Activity)ctx).findViewById(R.id.img_prev);
        btn_next = ((Activity)ctx).findViewById(R.id.img_next);
        tv_count = ((Activity)ctx).findViewById(R.id.tv_count);
        tv_stack = ((Activity)ctx).findViewById(R.id.tv_stack);
    }


    //DRAG////////////////////////////////
    private void removeDragTemp()
    {
        for(int y = 0; y < lpm.height; y++)
        {
            for(int x = 0; x < lpm.width; x++)
            {
                dragTemp[y][x] = 0;
            }
        }

        ll_count.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
    }

    private void dragManage()
    {
        ImageView view;

        int dragStartX;
        int dragStartY;
        int dragEndX;
        int dragEndY;

        int lastX;
        int lastY;

        int dragCount;

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

        removeDragTemp();

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
            dragCount = dragEndY - dragStartY + 1;
            dragEndX = touchStartX;
            dragStartX = touchStartX;

            lastX = touchStartX;
            lastY = touchEndY;
        }

        for(int y = 0; y < lpm.height; y++)
        {
            dragTemp[y][lastX] = 2;
        }
        for(int x = 0; x < lpm.width; x++)
        {
            dragTemp[lastY][x] = 2;
        }

        for(int y = dragStartY; y <= dragEndY; y++)
        {
            for(int x = dragStartX; x <= dragEndX; x++)
            {
                if(lpm.checkedSet[y][x] == lpm.checkedSet[touchStartY][touchStartX])
                {
                    //본인이 처음 선택한 것이랑 현재 드래그된 칸이 똑같은 유형이라면
                    //dragTemp에 해당 칸을 저장해 둔다.
                    dragTemp[y][x] = 1;
                }
            }
        }
        tv_count.setText(String.valueOf(dragCount));
        view = (ImageView) glm.findViewByPosition(touchStartY * lpm.width + touchStartX);
        assert view != null;
        ll_count.setX(view.getX() + view.getWidth());
        ll_count.setY(view.getY());

        ll_count.setLayoutParams(new LinearLayout.LayoutParams(100, 100));

    }

    private void setDragChecked()
    {
        //dragTemp의 데이터를 checkedSet에 저장해 줌

        for(int y = 0; y < lpm.height; y++)
        {
            for(int x = 0; x < lpm.width; x++)
            {
                if(dragTemp[y][x] != 1)
                    continue;
                switch(macroMode)
                {
                    case 0:
                        //공백 -> 체크
                        if(lpm.checkedSet[y][x] == 0)
                        {
                            lpm.checkedSet[y][x] = 1;
                        }
                        break;
                    case 1:
                        //공백 -> X
                        if(lpm.checkedSet[y][x] == 0)
                        {
                            lpm.checkedSet[y][x] = 2;
                        }
                        break;
                    case 2:
                        //체크 -> 공백
                        if(lpm.checkedSet[y][x] == 1)
                        {
                            lpm.checkedSet[y][x] = 0;
                        }
                        break;
                    case 3:
                        //체크 -> X
                        if(lpm.checkedSet[y][x] == 1)
                        {
                            lpm.checkedSet[y][x] = 2;
                        }
                        break;
                    case 4:
                        //X -> 공백
                        if(lpm.checkedSet[y][x] == 2)
                        {
                            lpm.checkedSet[y][x] = 0;
                        }
                    case 5:
                        //X -> 체크
                        if(lpm.checkedSet[y][x] == 2)
                        {
                            lpm.checkedSet[y][x] = 1;
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
        touchEndX = x;
        touchEndY = y;

        if(touchMode == 0)
        {
            //체크모드
            if(lpm.checkedSet[y][x] == 0)
            {
                //공백 -> 체크
                macroMode = 0;
            }
            else if(lpm.checkedSet[y][x] == 2)
            {
                //X -> 체크
                macroMode = 5;
            }
            else if(lpm.checkedSet[y][x] == 1)
            {
                //체크 -> 공백
                macroMode = 2;
            }
        }
        else
        {
            //X모드
            if(lpm.checkedSet[y][x] == 0)
            {
                //공백 -> X
                macroMode = 1;
            }
            else if(lpm.checkedSet[y][x] == 2)
            {
                //X -> 공백
                macroMode = 4;
            }
            else if(lpm.checkedSet[y][x] == 1)
            {
                //체크 -> X
                macroMode = 3;
            }
        }
    }

    private void showStackNum()
    {
        String str_stack = lpm.getStackNum() + "/" + lpm.getStackMaxNum();

        tv_stack.setText(str_stack);
    }

    private BoardItemTouchListener boardTouchListener = new BoardItemTouchListener("touchable") {

        @Override
        public void onDownTouchableView(int pos) {
            // Write log here.
            // This is an abstract method, you must implement.

            setMacroMode(pos);
            dragManage();
            refreshBoard();
            updateNumColor();
        }

        @Override
        public void onMoveTouchableView(int pos) {
            // Write log here.
            // This is an abstract method, you must implement.
            touchEndX = pos % lpm.width;
            touchEndY = pos / lpm.width;

            dragManage();
            refreshBoard();
            updateNumColor();
        }



        /////////////////////////////

        @Override
        public void onClickUp(int pos, RecyclerView.ViewHolder holder)
        {
            setDragChecked();
            removeDragTemp();
            lpm.pushCheckStack();
            refreshBoard();
            updateNumColor();


            lpm.isGameEnd();
        }

        @Override
        public void onLongClickUp(int pos, RecyclerView.ViewHolder holder)
        {
            setDragChecked();
            removeDragTemp();
            lpm.pushCheckStack();
            refreshBoard();
            updateNumColor();
            showStackNum();

            lpm.isGameEnd();
        }

        @Override
        public void onDragMultiUp(int endPos, RecyclerView.ViewHolder holder)
        {
            setDragChecked();
            removeDragTemp();
            lpm.pushCheckStack();
            refreshBoard();
            updateNumColor();
            showStackNum();

            lpm.isGameEnd();
        }


    };

    private void updateNumColor()
    {
        for(int i = 0; i < lpm.width; i++)
        {
            civm.rvColumnAdapter.updateNumColor(i, lpm.checkedSet);
        }
        for(int i = 0; i < lpm.height; i++)
        {
            rivm.rvRowAdapter.updateNumColor(i, lpm.checkedSet);
        }
    }



    private void refreshBoard()
    {
        for(int y = 0; y < lpm.height; y++)
        {
            for(int x = 0; x < lpm.width; x++)
            {
                ImageView view =  (ImageView) glm.findViewByPosition(x + y * lpm.width);
                if(view == null)
                    return;

                if(dragTemp[y][x] == 0)
                {
                    //dragTemp가 0인 경우 checkedSet에 있는 값대로 화면에 표현해 준다.
                    switch(lpm.checkedSet[y][x])
                    {
                        case 0:
                            view.setImageResource(R.drawable.border_1px_transparent);
                            view.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            break;
                        case 1:
                            view.setImageResource(R.drawable.border_1px_transparent);
                            view.setBackgroundColor(Color.parseColor("#000000"));
                            break;
                        case 2:
                            view.setImageResource(R.drawable.border_1px_x);
                            view.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            break;
                    }
                }
                else if(dragTemp[y][x] == 1)
                {
                    //dragTemp가 1인 경우 드래그된 칸은 macroMode에 맞게 그래픽을 갱신해 준다.
                    switch(macroMode)
                    {
                        case 0:
                        case 5:
                            //체크
                            view.setImageResource(R.drawable.border_2dp_transparent);
                            view.setBackgroundColor(Color.parseColor("#000000"));
                            break;
                        case 1:
                        case 3:
                            //X
                            view.setImageResource(R.drawable.border_2dp_x);
                            view.setBackgroundColor(Color.parseColor("#ffffff"));
                            break;
                        case 2:
                        case 4:
                            //공백
                            view.setImageResource(R.drawable.border_2dp_transparent);
                            view.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            break;
                    }
                }
                else if(dragTemp[y][x] == 2)
                {
                    switch(lpm.checkedSet[y][x])
                    {
                        case 0:
                            view.setImageResource(R.drawable.border_1px_transparent);
                            view.setBackgroundColor(Color.parseColor("#aaeeff"));
                            break;
                        case 1:
                            view.setImageResource(R.drawable.border_1px_transparent);
                            view.setBackgroundColor(Color.parseColor("#113355"));
                            break;
                        case 2:
                            view.setImageResource(R.drawable.border_1px_x);
                            view.setBackgroundColor(Color.parseColor("#aaeeff"));
                            break;
                    }
                }

            }
        }
    }


    public void makeGameBoard()
    {
        //로직 게임판을 만듭니다.

        glm = new GridLayoutManager(ctx, lpm.width);

        RvBoardAdapter rba = new RvBoardAdapter(lpm.height, lpm.width);

        rv_board = ((Activity)ctx).findViewById(R.id.rv_board);

        rv_board.setLayoutManager(glm);
        rv_board.setAdapter(rba);

        rv_board.addOnItemTouchListener(boardTouchListener);

        civm = new ColumnIndexViewMaker(lpm.dataSet);
        rivm = new RowIndexViewMaker(lpm.dataSet);

        civm.setView(ctx);
        rivm.setView(ctx);

        btn_toggle.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(touchMode == 0)
                {
                    touchMode = 1;
                    btn_toggle.setText("X");
                }
                else
                {
                    touchMode = 0;
                    btn_toggle.setText("O");
                }

            }
        });

        btn_next.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                lpm.nextCheckStack();

                refreshBoard();
                updateNumColor();
                showStackNum();
            }
        });

        btn_prev.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                lpm.prevCheckStack();

                refreshBoard();
                updateNumColor();
                showStackNum();
            }
        });

        refreshBoard();
    }
}
