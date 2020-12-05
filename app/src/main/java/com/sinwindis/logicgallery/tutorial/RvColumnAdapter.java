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

public class RvColumnAdapter extends RecyclerView.Adapter<RvColumnAdapter.ViewHolder> {

    List<TextView> tvList = new ArrayList<>();
    String[] dataSet;

    float widthUnder = 0;
    int widthOffset = 0;
    int length;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조. (hold strong reference)
        }
    }

    RvColumnAdapter(String[] dataSet) {
        //생성자
        this.dataSet = dataSet;
        this.length = dataSet.length;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @NonNull
    @Override
    public RvColumnAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_column, parent, false);


        widthUnder += parent.getMeasuredWidth() % this.length / ((float) this.length);

        if (widthUnder >= 1) {
            widthOffset = 1;
            widthUnder--;
        }

        view.setLayoutParams(new RecyclerView.LayoutParams(parent.getMeasuredWidth() / this.length + widthOffset, ViewGroup.LayoutParams.MATCH_PARENT));

        return new ViewHolder(view);
    }

    // onBindViewHolder() - position 에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(RvColumnAdapter.ViewHolder holder, int position) {
        tvList.add((TextView) holder.itemView.findViewById(R.id.tv_item_column));
        tvList.get(position).setText(dataSet[position]);
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return this.length;
    }

    public void refreshView(String[] dataSet) {
        this.dataSet = dataSet;
        for (int i = 0; i < this.length; i++) {
            tvList.get(i).setText(dataSet[i]);
        }
    }
}