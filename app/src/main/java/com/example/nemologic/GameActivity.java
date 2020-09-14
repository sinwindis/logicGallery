package com.example.nemologic;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GameActivity extends AppCompatActivity {


    private RecyclerView rv_board;
    private RecyclerView rv_row;
    private RecyclerView rv_column;
    private TextView tv_stack;
    private TextView tv_count;

    private LinearLayout ll_count;

    private Button btn_toggle;

    RvRowAdapter rvRowAdapter;
    RvColumnAdapter rvColumnAdapter;

    private GridLayoutManager glm;
    private GameLevelData gld;

    int[][] rowDataSet;
    int[][] columnDataSet;

    int[][] dragTemp;

    int touchStartX;
    int touchStartY;
    int touchEndX;
    int touchEndY;


    //0: 체크, 1: X
    int touchMode = 0;
    //0: 공백 -> 체크, 1: 공백 -> X, 2: 체크 -> 공백, 3: 체크 -> X, 4: X -> 공백, 5: X -> 체크
    int macroMode = 0;

    private void checkGameEnd()
    {
        for(int y = 0; y < gld.height; y++)
        {
            for(int x = 0; x < gld.width; x++)
            {
                if(gld.dataSet[y][x] == 1 && gld.checkedSet[y][x] != 1)
                {
                    return;
                }
                else if(gld.dataSet[y][x] != 1 && gld.checkedSet[y][x] == 1)
                {
                    return;
                }
            }
        }

        Toast.makeText(this, "Game End", Toast.LENGTH_LONG).show();
    }

    private void refreshBoard()
    {
        for(int y = 0; y < gld.height; y++)
        {
            for(int x = 0; x < gld.width; x++)
            {
                ImageView view =  (ImageView) glm.findViewByPosition(x + y * gld.width);
                if(view == null)
                    return;

                if(dragTemp[y][x] == 0)
                {
                    //dragTemp가 0인 경우 checkedSet에 있는 값대로 화면에 표현해 준다.
                    switch(gld.checkedSet[y][x])
                    {
                        case 0:
                            view.setImageResource(R.drawable.border_05dp_white);
                            break;
                        case 1:
                            view.setImageResource(R.drawable.border_05dp_black);
                            break;
                        case 2:
                            view.setImageResource(R.drawable.border_05dp_x);
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
                            view.setImageResource(R.drawable.border_4dp_black);
                            break;
                        case 1:
                        case 3:
                            //X
                            view.setImageResource(R.drawable.border_4dp_x);
                            break;
                        case 2:
                        case 4:
                            //공백
                            view.setImageResource(R.drawable.border_4dp_white);
                            break;
                    }
                }
                else if(dragTemp[y][x] == 2)
                {
                    switch(gld.checkedSet[y][x])
                    {
                        case 0:
                            view.setImageResource(R.drawable.border_05dp_white_sky);
                        break;
                        case 1:
                            view.setImageResource(R.drawable.border_05dp_black_sky);
                            break;
                        case 2:
                            view.setImageResource(R.drawable.border_05dp_x_sky);
                            break;
                    }
                }


            }
        }
    }

    private void removeDragTemp()
    {
        for(int y = 0; y < gld.height; y++)
        {
            for(int x = 0; x < gld.width; x++)
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

        for(int y = 0; y < gld.height; y++)
        {
            dragTemp[y][lastX] = 2;
        }
        for(int x = 0; x < gld.width; x++)
        {
            dragTemp[lastY][x] = 2;
        }

        for(int y = dragStartY; y <= dragEndY; y++)
        {
            for(int x = dragStartX; x <= dragEndX; x++)
            {
                if(gld.checkedSet[y][x] == gld.checkedSet[touchStartY][touchStartX])
                {
                    //본인이 처음 선택한 것이랑 현재 드래그된 칸이 똑같은 유형이라면
                    //dragTemp에 해당 칸을 저장해 둔다.
                    dragTemp[y][x] = 1;
                }
            }
        }
        tv_count.setText(String.valueOf(dragCount));
        view = (ImageView) glm.findViewByPosition(touchStartY * gld.width + touchStartX);
        ll_count.setX(view.getX() + view.getWidth());
        ll_count.setY(view.getY());

        ll_count.setLayoutParams(new LinearLayout.LayoutParams(100, 100));

    }

    private void updateNumColor()
    {
        for(int i = 0; i < gld.width; i++)
        {
            rvColumnAdapter.updateNumColor(i, gld.checkedSet);
        }
        for(int i = 0; i < gld.height; i++)
        {
            rvRowAdapter.updateNumColor(i, gld.checkedSet);
        }


    }

    private void setChecked()
    {
        //dragTemp의 데이터를 checkedSet에 저장해 줌

        for(int y = 0; y < gld.height; y++)
        {
            for(int x = 0; x < gld.width; x++)
            {
                if(dragTemp[y][x] != 1)
                    continue;
                switch(macroMode)
                {
                    case 0:
                        //공백 -> 체크
                        if(gld.checkedSet[y][x] == 0)
                        {
                            gld.checkedSet[y][x] = 1;
                        }
                        break;
                    case 1:
                        //공백 -> X
                        if(gld.checkedSet[y][x] == 0)
                        {
                            gld.checkedSet[y][x] = 2;
                        }
                        break;
                    case 2:
                        //체크 -> 공백
                        if(gld.checkedSet[y][x] == 1)
                        {
                            gld.checkedSet[y][x] = 0;
                        }
                        break;
                    case 3:
                        //체크 -> X
                        if(gld.checkedSet[y][x] == 1)
                        {
                            gld.checkedSet[y][x] = 2;
                        }
                        break;
                    case 4:
                        //X -> 공백
                        if(gld.checkedSet[y][x] == 2)
                        {
                            gld.checkedSet[y][x] = 0;
                        }
                    case 5:
                        //X -> 체크
                        if(gld.checkedSet[y][x] == 2)
                        {
                            gld.checkedSet[y][x] = 1;
                        }

                }
            }
        }
    }

    private void showStackNum()
    {
        String str_stack = gld.getStackNum() + "/" + gld.getStackMaxNum();

        tv_stack.setText(str_stack);
    }


    private void setMacroMode(int pos)
    {
        int x = pos % gld.width;
        int y = pos / gld.width;

        touchStartX = x;
        touchStartY = y;
        touchEndX = x;
        touchEndY = y;

        if(touchMode == 0)
        {
            //체크모드
            if(gld.checkedSet[y][x] == 0)
            {
                //공백 -> 체크
                macroMode = 0;
            }
            else if(gld.checkedSet[y][x] == 2)
            {
                //X -> 체크
                macroMode = 5;
            }
            else if(gld.checkedSet[y][x] == 1)
            {
                //체크 -> 공백
                macroMode = 2;
            }
        }
        else
        {
            //X모드
            if(gld.checkedSet[y][x] == 0)
            {
                //공백 -> X
                macroMode = 1;
            }
            else if(gld.checkedSet[y][x] == 2)
            {
                //X -> 공백
                macroMode = 4;
            }
            else if(gld.checkedSet[y][x] == 1)
            {
                //체크 -> X
                macroMode = 3;
            }
        }
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
            touchEndX = pos % gld.width;
            touchEndY = pos / gld.width;

            dragManage();
            refreshBoard();
            updateNumColor();
        }



        /////////////////////////////

        @Override
        public void onClickUp(int pos, RecyclerView.ViewHolder holder)
        {
            setChecked();
            removeDragTemp();
            gld.pushCheckStack();
            refreshBoard();
            updateNumColor();
            showStackNum();

            checkGameEnd();
        }

        @Override
        public void onLongClickUp(int pos, RecyclerView.ViewHolder holder)
        {
            setChecked();
            removeDragTemp();
            gld.pushCheckStack();
            refreshBoard();
            updateNumColor();
            showStackNum();

            checkGameEnd();
        }

        @Override
        public void onDragMultiUp(int endPos, RecyclerView.ViewHolder holder)
        {
            setChecked();
            removeDragTemp();
            gld.pushCheckStack();
            refreshBoard();
            updateNumColor();
            showStackNum();

            checkGameEnd();
        }


    };

    private void makeRowDataSet()
    {
        rowDataSet = new int[gld.height][gld.width];
        int sumTemp;
        int idx;

        for(int y = 0; y < gld.height; y++)
        {
            sumTemp = 0;
            idx = 0;
            rowDataSet[y][0] = 0;
            for(int x = 0; x < gld.width; x++)
            {
                if(gld.dataSet[y][x] == 1)
                {
                    sumTemp++;
                }
                else if(sumTemp != 0)
                {
                    rowDataSet[y][idx] = sumTemp;
                    idx++;
                    sumTemp = 0;
                }
            }

            if(sumTemp != 0)
            {
                rowDataSet[y][idx] = sumTemp;
            }
        }
    }

    private void makeColumnDataSet()
    {
        columnDataSet = new int[gld.width][gld.height];
        int sumTemp;
        int idx;

        for(int x = 0; x < gld.width; x++)
        {
            sumTemp = 0;
            idx = 0;
            columnDataSet[x][0] = 0;
            for(int y = 0; y < gld.height; y++)
            {
                if(gld.dataSet[y][x] == 1)
                {
                    sumTemp++;
                }
                else if(sumTemp != 0)
                {
                    columnDataSet[x][idx] = sumTemp;
                    idx++;
                    sumTemp = 0;
                }
            }

            if(sumTemp != 0)
            {
                columnDataSet[x][idx] = sumTemp;
            }
        }
    }

    private void makeIdxView()
    {
        makeRowDataSet();

        rvRowAdapter = new RvRowAdapter(rowDataSet, this);
        rv_row.setLayoutManager(new LinearLayoutManager(this));
        rv_row.setAdapter(rvRowAdapter);

        makeColumnDataSet();
        rvColumnAdapter = new RvColumnAdapter(columnDataSet, this);
        rv_column.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_column.setAdapter(rvColumnAdapter);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void makeGameBoard()
    {
        //로직 게임판을 만듭니다.

        glm = new GridLayoutManager(this, gld.width);
        GameScreenAdapter gsa = new GameScreenAdapter(gld.height, gld.width);

        rv_board.setLayoutManager(glm);
        rv_board.setAdapter(gsa);

        rv_board.addOnItemTouchListener(boardTouchListener);
        makeIdxView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        tv_stack = findViewById(R.id.tv_stack);
        tv_count = findViewById(R.id.tv_count);
        ll_count = findViewById(R.id.ll_count);

        btn_toggle = findViewById(R.id.img_toggle);

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

        Button btn_next = findViewById(R.id.img_next);

        btn_next.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                gld.nextCheckStack();

                refreshBoard();
                updateNumColor();
                showStackNum();
            }
        });

        Button btn_prev = findViewById(R.id.img_prev);

        btn_prev.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                gld.prevCheckStack();

                refreshBoard();
                updateNumColor();
                showStackNum();
            }
        });


        rv_board = findViewById(R.id.rv_board);
        rv_row = findViewById(R.id.rv_row);
        rv_column = findViewById(R.id.rv_column);

        int[][] dummyData = {{0, 1, 0, 1, 1, 0, 1, 0, 1, 1},
                             {0, 1, 0, 1, 1, 1, 1, 1, 0, 0},
                             {1, 1, 0, 1, 0, 1, 1, 1, 1, 1},
                             {0, 1, 1, 0, 0, 0, 0, 1, 0, 1},
                             {1, 1, 0, 1, 0, 1, 1, 1, 1, 1},
                             {1, 1, 0, 1, 0, 1, 1, 1, 1, 1},
                             {1, 1, 0, 1, 0, 1, 1, 1, 1, 1}};
        gld = new GameLevelData("dummy", dummyData);

        dragTemp = new int[gld.height][gld.width];
        makeGameBoard();

        showStackNum();
    }
}