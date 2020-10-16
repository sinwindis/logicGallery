package com.example.nemologic.bigpuzzle;

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
import com.example.nemologic.data.LevelThumbnailData;
import com.example.nemologic.data.StringParser;
import com.example.nemologic.game.GameFragment;
import com.example.nemologic.level.RvLevelBoardAdapter;
import com.example.nemologic.mainactivity.MainActivity;

public class RvBigPuzzleAdapter extends RecyclerView.Adapter<RvBigPuzzleAdapter.ViewHolder> {

    LevelThumbnailData[] levels;
    Context ctx;
    int rowItemNum;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView) ;
            // 뷰 객체에 대한 참조. (hold strong reference)
        }
    }

    RvBigPuzzleAdapter(Context ctx, LevelThumbnailData[] levels, int rowItemNum) {
        //생성자
        this.levels = levels;
        this.ctx = ctx;
        this.rowItemNum = rowItemNum;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @NonNull
    @Override
    public RvBigPuzzleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.item_level, parent, false) ;
        view.setLayoutParams(new RecyclerView.LayoutParams(parent.getMeasuredWidth()/rowItemNum, ViewGroup.LayoutParams.WRAP_CONTENT));

        return new ViewHolder(view);
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(RvBigPuzzleAdapter.ViewHolder holder, final int position) {

        //레벨 아이템 이름 표시
        ((TextView) holder.itemView.findViewById(R.id.tv_item_level_name)).setText(this.levels[position].getName());

        //레벨 아이템 사이즈 표시
        String temp = levels[position].getWidth() + "X" + levels[position].getHeight();

        ((TextView)holder.itemView.findViewById(R.id.tv_item_level_size)).setText(temp);

        //빅퍼즐 진행 상태 표시
        RecyclerView rv_board = holder.itemView.findViewById(R.id.rv_level_board);
        rv_board.setLayoutManager(new GridLayoutManager(ctx, levels[position].getWidth()));
        if(levels[position].getProgress() == 1)
        {
            //완료된 퍼즐이면
            //세이브데이터를 가져온다
            rv_board.setAdapter(new RvLevelBoardAdapter(StringParser.getParsedSaveData(levels[position].getSaveData(), levels[position].getWidth(), levels[position].getHeight())));
        }
        else if(levels[position].getProgress() == 2)
        {
            //완료한 게임이면
            //컬러셋을 가져온다
            rv_board.setAdapter(new RvLevelBoardAdapter(StringParser.getParsedColorSet(levels[position].getColorSet(), levels[position].getWidth(), levels[position].getHeight())));
        }

        final View itemView = holder.itemView;

        holder.itemView.findViewById(R.id.cl_touchbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //클릭 시 해당 게임을 플레이하는 GameActivity 로 이동
                Fragment dest = new GameFragment(ctx);
                // Fragment 생성
                Bundle bundle = new Bundle();
                bundle.putInt("id", levels[position].getId());
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