package com.sinwindis.logicgallery.tutorial;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinwindis.logicgallery.R;

public class RvTutorialBoardAdapter extends RecyclerView.Adapter<RvTutorialBoardAdapter.ViewHolder> {

    int height;
    int width;
    private float heightUnder = 0;
    private int heightOffset = 0;
    private int widthCount = 0;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조. (hold strong reference)
        }
    }

    RvTutorialBoardAdapter(int width, int height) {
        //생성자
        this.height = height;
        this.width = width;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @NonNull
    @Override
    public RvTutorialBoardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_board, parent, false);

        if (widthCount % this.width == 0) {
            //매 줄의 첫 칸일 때

            //height 는 여기서 한 번만 계산
            heightUnder += parent.getMeasuredHeight() % this.height / ((float) this.height);

            if (heightUnder >= 1) {
                heightOffset = 1;
                heightUnder--;
            }
        }

        view.setLayoutParams(new RecyclerView.LayoutParams(parent.getMeasuredWidth() / this.width + 2, parent.getMeasuredHeight() / this.height + heightOffset));

        heightOffset = 0;
        widthCount++;

        return new ViewHolder(view);
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(@NonNull RvTutorialBoardAdapter.ViewHolder holder, int position) {

        ImageView iv = (ImageView) holder.itemView;

        iv.setBackgroundColor(Integer.parseInt("FFFFFF", 16));
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return height * width;
    }

}