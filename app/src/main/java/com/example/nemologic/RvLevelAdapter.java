package com.example.nemologic;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RvLevelAdapter extends RecyclerView.Adapter<RvLevelAdapter.ViewHolder> {


    GameLevelData[] levels;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView) ;
            // 뷰 객체에 대한 참조. (hold strong reference)
        }
    }

    RvLevelAdapter(GameLevelData[] levels) {
        //생성자
        this.levels = levels;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public RvLevelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.item_level, parent, false) ;
        view.setLayoutParams(new RecyclerView.LayoutParams(150, 150));

        RvLevelAdapter.ViewHolder vh = new RvLevelAdapter.ViewHolder(view) ;

        return vh;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(RvLevelAdapter.ViewHolder holder, int position) {
        TextView tv = holder.itemView.findViewById(R.id.tv_item_level_name);
        tv.setText(this.levels[position].name);
        tv = holder.itemView.findViewById(R.id.tv_item_level_size);
        String temp = levels[position].width + "X" + levels[position].height;
        tv.setText(temp);

    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return levels.length;
    }

}