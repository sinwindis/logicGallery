package com.example.nemologic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class GameScreenAdapter extends RecyclerView.Adapter<GameScreenAdapter.ViewHolder> {

    int columnSize = 0;
    int rowSize = 0;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
        }
    }

    GameScreenAdapter(int columnSize, int rowSize) {
        //생성자
        this.columnSize = columnSize;
        this.rowSize = rowSize;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public GameScreenAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.item_board, parent, false) ;
        view.setLayoutParams(new RecyclerView.LayoutParams(parent.getMeasuredWidth()/this.rowSize, parent.getMeasuredHeight()/this.columnSize));

        GameScreenAdapter.ViewHolder vh = new GameScreenAdapter.ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(GameScreenAdapter.ViewHolder holder, int position) {
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return columnSize*rowSize;
    }
}