package com.example.nemologic.level;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;
import com.example.nemologic.mainactivity.MainActivity;
import com.example.nemologic.data.LevelData;
import com.example.nemologic.game.GameFragment;

public class RvLevelAdapter extends RecyclerView.Adapter<RvLevelAdapter.ViewHolder> {

    LevelData[] levels;
    Context ctx;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView) ;
            // 뷰 객체에 대한 참조. (hold strong reference)
        }
    }

    RvLevelAdapter(Context ctx, LevelData[] levels) {
        //생성자
        this.levels = levels;
        this.ctx = ctx;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @NonNull
    @Override
    public RvLevelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.item_level, parent, false) ;
        view.setLayoutParams(new RecyclerView.LayoutParams(parent.getMeasuredWidth()/2 - 15, (int) ((parent.getMeasuredWidth()/2 - 15)*1.3)));

        return new ViewHolder(view);
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(RvLevelAdapter.ViewHolder holder, final int position) {
        TextView tv = holder.itemView.findViewById(R.id.tv_item_level_name);
        tv.setText(this.levels[position].getName());
        tv = holder.itemView.findViewById(R.id.tv_item_level_size);
        String temp = levels[position].getWidth() + "X" + levels[position].getHeight();
        tv.setText(temp);

        RecyclerView rv_board = holder.itemView.findViewById(R.id.rv_level_board);
        rv_board.setLayoutManager(new GridLayoutManager(ctx, levels[position].getWidth()));
        rv_board.setAdapter(new RvLevelBoardAdapter(levels[position].getParsedSaveData()));

        holder.itemView.findViewById(R.id.cl_touchbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //클릭 시 해당 게임을 플레이하는 GameActivity로 이동
                Fragment dest = new GameFragment();
                // Fragment 생성
                Bundle bundle = new Bundle();
                bundle.putString("category", levels[position].getCategory());
                bundle.putString("name", levels[position].getName());
                dest.setArguments(bundle);
                ((MainActivity)ctx).fragmentMove(dest);
            }
        });
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return levels.length;
    }

}