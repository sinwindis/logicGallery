package com.example.nemologic.game;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.LevelPlayManager;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class GameBoard {

    SharedPreferences optionPref;
    boolean smartDrag;
    boolean oneLineDrag;
    boolean autoX;

    GameFragment parent;

    private View targetView;

    private RecyclerView rv_board;

    private GridLayoutManager glm;
    private RvBoardAdapter rba;
    public LevelPlayManager lpm;

    ColumnIndexViewMaker civm;
    RowIndexViewMaker rivm;

    private TextView tv_stack;
    private TextView tv_count;

    private ConstraintLayout cl_count;
    private LinearLayout ll_drag;

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
    //0: 공백 1: 체크 2: X
    int macroMode = 0;
    
    public GameBoard(GameFragment ctx, View view, LevelPlayManager lpm)
    {
        targetView = view;
        this.lpm = lpm;
        this.parent = ctx;

        initialize();
    }

    public void loadOption()
    {
        optionPref = targetView.getContext().getSharedPreferences("OPTION", MODE_PRIVATE);
        smartDrag = optionPref.getBoolean("smartDrag", true);
        oneLineDrag = optionPref.getBoolean("oneLineDrag", true);
        autoX = optionPref.getBoolean("autoX", false);
    }

    private void initialize()
    {
        dragTemp = new int[lpm.height][lpm.width];
        rv_board = targetView.findViewById(R.id.rv_board);

        cl_count = targetView.findViewById(R.id.cl_count);
        ll_drag = targetView.findViewById(R.id.ll_drag);
        btn_toggle = targetView.findViewById(R.id.img_toggle);
        btn_prev = targetView.findViewById(R.id.img_prevstack);
        btn_next = targetView.findViewById(R.id.img_nextstack);

        tv_count = targetView.findViewById(R.id.tv_count);
        tv_stack = targetView.findViewById(R.id.tv_stack);

        loadOption();
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

        cl_count.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        ll_drag.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
    }

    private void dragManage()
    {
        ImageView view;

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
                if(lpm.checkedSet[y][x] == lpm.checkedSet[touchStartY][touchStartX] && smartDrag)
                {
                    //본인이 처음 선택한 것이랑 현재 드래그된 칸이 똑같은 유형이라면
                    //dragTemp 에 해당 칸을 저장해 둔다.
                    dragTemp[y][x] = 1;
                }
                else if(!smartDrag)
                {
                    //스마트드래그가 꺼져 있다면 드래그한 모든 칸을 dragTemp 에 저장한다.
                    dragTemp[y][x] = 1;
                }
            }
        }
        if(dragCount > 1)
        {
            tv_count.setText(String.valueOf(dragCount));
            View firstTouchView = glm.findViewByPosition(touchStartY * lpm.width + touchStartX);
            assert firstTouchView != null;

            cl_count.setX(firstTouchView.getX());
            cl_count.setY(firstTouchView.getY());

            cl_count.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
        }


        View firstDragView = glm.findViewByPosition(dragStartY * lpm.width + dragStartX);
        View lastDragView = glm.findViewByPosition(dragEndY * lpm.width + dragEndX);

        assert firstDragView != null;
        assert lastDragView != null;

        int dragWidth = (int) (lastDragView.getX() - firstDragView.getX() + lastDragView.getWidth()) + 20;
        int dragHeight = (int) (lastDragView.getY() - firstDragView.getY() + lastDragView.getHeight()) + 20;

        ll_drag.setX(firstDragView.getX() - 10);
        ll_drag.setY(firstDragView.getY() - 10);

        ll_drag.setLayoutParams(new LinearLayout.LayoutParams(dragWidth, dragHeight));
    }
    
    private void checkAutoX()
    {
        if(!autoX)
        {
            //autoX 옵션이 꺼져있으면 바로 종료
            return;
        }
        
        //row 의 경우
        for(int row = 0; row < lpm.height; row++)
        {
            if(rivm.rvRowAdapter.endRow[row])
            {
                //해당 row가 완성됐다면
                for(int x = 0; x < lpm.width; x++)
                {
                    if(lpm.checkedSet[row][x] == 0)
                    {
                        //해당 칸이 공백이면 x로 채워준다.
                        lpm.checkedSet[row][x] = 2;
                    }
                }
            }
        }

        //column 의 경우
        for(int column = 0; column < lpm.width; column++)
        {
            if(civm.rvColumnAdapter.endColumn[column])
            {
                //해당 column가 완성됐다면
                for(int x = 0; x < lpm.height; x++)
                {
                    if(lpm.checkedSet[x][column] == 0)
                    {
                        //해당 칸이 공백이면 x로 채워준다.
                        lpm.checkedSet[x][column] = 2;
                    }
                }
            }
        }
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
                        //공백
                        lpm.checkedSet[y][x] = 0;
                        break;
                    case 1:
                        //체크
                        lpm.checkedSet[y][x] = 1;
                        break;
                    case 2:
                        //X
                        lpm.checkedSet[y][x] = 2;
                        break;
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
            if(lpm.checkedSet[y][x] == 1)
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
        else
        {
            //X모드
            if(lpm.checkedSet[y][x] == 2)
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
    }

    private void showStackNum()
    {
        String str_stack = lpm.getStackNum() + "/" + lpm.getStackMaxNum();

        tv_stack.setText(str_stack);
    }

    private void setGameEnd()
    {
        lpm.progress = 2;
        lpm.savePlayData(parent.getContext());
        //parent.getFragmentManager().popBackStackImmediate();
    }

    private void clickUpAction()
    {
        setDragChecked();
        removeDragTemp();
        lpm.pushCheckStack();
        updateNumColor();
        checkAutoX();
        showStackNum();
        refreshBoard();

        if(lpm.isGameEnd())
        {
            setGameEnd();
        }
    }

    private void clickAction(int pos)
    {
        touchEndX = pos % lpm.width;
        touchEndY = pos / lpm.width;

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
            civm.rvColumnAdapter.updateNumColor(i, lpm.checkedSet);
        }
        for(int i = 0; i < lpm.height; i++)
        {
            rivm.rvRowAdapter.updateNumColor(i, lpm.checkedSet);
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

                switch(lpm.checkedSet[y][x])
                {
                    case 0:
                        view.setImageResource(R.drawable.background_transparent);
                        view.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        break;
                    case 1:
                        view.setImageResource(R.drawable.background_transparent);
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


                if(dragTemp[y][x] == 1)
                {
                    //dragTemp가 1인 경우 드래그된 칸은 macroMode에 맞게 그래픽을 갱신해 준다.
                    switch(macroMode)
                    {
                        case 0:
                            //공백
                            view.setImageResource(R.drawable.background_transparent);
                            view.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            break;
                        case 1:
                            //체크
                            view.setImageResource(R.drawable.background_transparent);
                            view.setBackgroundColor(Color.parseColor("#000000"));
                            break;
                        case 2:
                            //X
                            view.setImageResource(R.drawable.background_x);
                            view.setBackgroundColor(Color.parseColor("#ffffff"));
                            break;
                    }
                }
                else if(dragTemp[y][x] == 2)
                {
                    switch(lpm.checkedSet[y][x])
                    {
                        case 0:
                            view.setImageResource(R.drawable.background_transparent);
                            view.setBackgroundColor(Color.parseColor("#aaeeff"));
                            break;
                        case 1:
                            view.setImageResource(R.drawable.background_transparent);
                            view.setBackgroundColor(Color.parseColor("#113355"));
                            break;
                        case 2:
                            view.setImageResource(R.drawable.background_x);
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
        glm = new GridLayoutManager(targetView.getContext(), lpm.width);

        rba = new RvBoardAdapter(lpm.checkedSet);

        rv_board.addItemDecoration(new GameBoardBorder(targetView.getContext(), R.drawable.border_gameboard_normal, R.drawable.border_gameboard_accent, lpm.width));

        rv_board.setLayoutManager(glm);
        rv_board.addOnItemTouchListener(boardTouchListener);
        rv_board.setAdapter(rba);

        civm = new ColumnIndexViewMaker(lpm.dataSet);
        rivm = new RowIndexViewMaker(lpm.dataSet);

        civm.setView(targetView);
        rivm.setView(targetView);

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
    }
}
