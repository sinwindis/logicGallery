package com.example.nemologic.mainactivity;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;

public class RvContinueLevelAdapter extends RecyclerView.Adapter<RvContinueLevelAdapter.ViewHolder> {

    int height;
    int width;
    int[][] checkedSet;
    float offset = 0;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
        }
    }

    public RvContinueLevelAdapter(int[][] checkedSet) {
        //생성자
        this.checkedSet = checkedSet.clone();
        this.height = checkedSet.length;
        this.width = checkedSet[0].length;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @NonNull
    @Override
    public RvContinueLevelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.item_board, parent, false);
        view.setLayoutParams(new RecyclerView.LayoutParams(parent.getMeasuredWidth()/this.width + 1, parent.getMeasuredHeight()/this.height));

        return new ViewHolder(view);
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(@NonNull RvContinueLevelAdapter.ViewHolder holder, int position) {

        ImageView iv = (ImageView) holder.itemView;

        int color = checkedSet[position/width][position% width];
        String colorStr = Integer.toHexString(color);
        while(colorStr.length() < 6)
        {
            colorStr = "0" + colorStr;
        }


        iv.setBackgroundColor(Color.parseColor("#80" + colorStr));
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return height * width;
    }
}