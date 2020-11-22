package com.example.nemologic.game;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;

import java.util.ArrayList;
import java.util.List;

public class RvColumnAdapter extends RecyclerView.Adapter<RvColumnAdapter.ViewHolder> {

    ColumnIndexDataManager columnIndexDataManager;

    List<TextView> tvList = new ArrayList<>();
    float widthUnder = 0;
    int widthOffset = 0;
    int length;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
        }
    }

    RvColumnAdapter(ColumnIndexDataManager columnIndexDataManager) {
        //생성자
        this.columnIndexDataManager = columnIndexDataManager;
        columnIndexDataManager.makeIdxDataSet();
        this.length = columnIndexDataManager.idxNumSet.length;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @NonNull
    @Override
    public RvColumnAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_column, parent, false);


        widthUnder += parent.getMeasuredWidth()%this.length/((float)this.length);

        if(widthUnder >= 1)
        {
            widthOffset = 1;
            widthUnder--;
        }

        view.setLayoutParams(new RecyclerView.LayoutParams(parent.getMeasuredWidth()/this.length + widthOffset, ViewGroup.LayoutParams.MATCH_PARENT));

        widthOffset = 0;

        return new ViewHolder(view);
    }

    // onBindViewHolder() - position 에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(RvColumnAdapter.ViewHolder holder, int position) {
        final TextView tv = holder.itemView.findViewById(R.id.tv_item_column);
        tv.post(new Runnable() {
            @Override
            public void run() {
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, tv.getMeasuredWidth()/2);
            }
        });

        tvList.add(tv);
        refreshView(position);
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return this.length;
    }

    public void refreshView(int columnNum)
    {
        boolean[] completeIdx = columnIndexDataManager.getIdxMatch(columnNum);
        int[] idxNumSet = columnIndexDataManager.getIdxNumSet();
        int[][] dataSet = columnIndexDataManager.getIdxDataSet();

        tvList.get(columnNum).setText("");
        SpannableStringBuilder numStr;
        for(int i = 0; i < idxNumSet[columnNum]; i++)
        {
            if(i == 0)
            {
                numStr = new SpannableStringBuilder(String.valueOf(dataSet[columnNum][i]));
            }
            else
            {
                numStr = new SpannableStringBuilder('\n' + String.valueOf(dataSet[columnNum][i]));
            }

            if(completeIdx[i])
            {
                numStr.setSpan(new ForegroundColorSpan(Color.parseColor("#a0a0a0")), 0, numStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            else
            {
                numStr.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, numStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            tvList.get(columnNum).append(numStr);
        }
    }
}