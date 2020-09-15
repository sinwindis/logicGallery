package com.example.nemologic.categoryactivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;
import com.example.nemologic.levelactivity.LevelActivity;

public class RvCategoryAdapter extends RecyclerView.Adapter<RvCategoryAdapter.ViewHolder> {

    String[] names;
    Context ctx;
    final Intent intent;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView) ;
            // 뷰 객체에 대한 참조. (hold strong reference)
        }
    }

    RvCategoryAdapter(Context ctx, String[] names) {
        //생성자
        this.names = names.clone();
        this.ctx = ctx;
        this.intent = new Intent(ctx, LevelActivity.class);
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @NonNull
    @Override
    public RvCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.item_category, parent, false) ;
        view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return new ViewHolder(view);
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(RvCategoryAdapter.ViewHolder holder, int position) {
        TextView tv = holder.itemView.findViewById(R.id.tv_item_category_name);
        tv.setText(this.names[position]);
        final int categoryPos = position;

        holder.itemView.findViewById(R.id.cl_item_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //클릭 시 해당 카테고리에 해당하는 게임 레벨들을 나열하는 LevelActivity로 이동
                intent.putExtra("category", names[categoryPos]);
                ctx.startActivity(intent);
            }
        });
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return names.length;
    }

}