package com.sinwindis.logicgallery.tutorial;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinwindis.logicgallery.R;

import java.util.ArrayList;
import java.util.List;

public class RvRowAdapter extends RecyclerView.Adapter<RvRowAdapter.ViewHolder> {

    List<TextView> tvList = new ArrayList<>();
    String[] dataSet;

    float heightUnder = 0;
    int heightOffset = 0;
    int length;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView) ;
            // 뷰 객체에 대한 참조. (hold strong reference)
        }
    }

    RvRowAdapter(String[] dataSet) {
        //생성자
        this.dataSet = dataSet;
        length = dataSet.length;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @NonNull
    @Override
    public RvRowAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.item_row, parent, false) ;

        heightUnder += parent.getMeasuredHeight()%length/((float)length);

        if(heightUnder >= 1)
        {
            heightOffset = 1;
            heightUnder--;
        }

        view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parent.getMeasuredHeight()/length + heightOffset));

        return new ViewHolder(view);
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(RvRowAdapter.ViewHolder holder, int position) {
        tvList.add((TextView)  holder.itemView.findViewById(R.id.tv_item_row));

        tvList.get(position).setText(dataSet[position]);
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return length;
    }


    public void refreshView(String[] dataSet)
    {
        this.dataSet = dataSet;
        for(int i = 0; i < this.length; i++)
        {
            tvList.get(i).setText(dataSet[i]);
        }
    }
}