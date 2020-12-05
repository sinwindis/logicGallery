package com.sinwindis.logicgallery.game;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
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

    RowIndexDataManager rowIndexDataManager;
    List<TextView> tvList = new ArrayList<>();
    float heightUnder = 0;
    int heightOffset = 0;
    int length;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조. (hold strong reference)
        }
    }

    RvRowAdapter(RowIndexDataManager rowIndexDataManager) {
        //생성자
        this.rowIndexDataManager = rowIndexDataManager;
        length = rowIndexDataManager.getIdxNumSet().length;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @NonNull
    @Override
    public RvRowAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_row, parent, false);

        heightUnder += parent.getMeasuredHeight() % length / ((float) length);

        if (heightUnder >= 1) {
            heightOffset = 1;
            heightUnder--;
        }

        view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parent.getMeasuredHeight() / length + heightOffset));

        heightOffset = 0;

        return new ViewHolder(view);
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(RvRowAdapter.ViewHolder holder, int position) {

        final TextView tv = holder.itemView.findViewById(R.id.tv_item_row);
        tv.post(new Runnable() {
            @Override
            public void run() {
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, tv.getMeasuredHeight() / 2);
            }
        });


        tvList.add(tv);

        refreshView(position);
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return length;
    }


    public void refreshView(int rowNum) {
        //숫자가 다 채워질 경우 색 바꾸기

        int[] idxNumSet = rowIndexDataManager.getIdxNumSet();
        int[][] dataSet = rowIndexDataManager.getIdxDataSet();
        boolean[] completeIdx = rowIndexDataManager.getIdxMatch(rowNum);

        tvList.get(rowNum).setText("");
        SpannableStringBuilder numStr;
        for (int i = 0; i < idxNumSet[rowNum]; i++) {
            numStr = new SpannableStringBuilder(' ' + String.valueOf(dataSet[rowNum][i]));
            if (completeIdx[i]) {
                numStr.setSpan(new ForegroundColorSpan(Color.parseColor("#a0a0a0")), 0, numStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                numStr.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, numStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            tvList.get(rowNum).append(numStr);
        }
    }
}