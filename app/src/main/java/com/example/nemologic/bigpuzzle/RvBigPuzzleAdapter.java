package com.example.nemologic.bigpuzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;
import com.example.nemologic.biglevel.BigLevelFragment;
import com.example.nemologic.data.BigPuzzleData;
import com.example.nemologic.data.LevelThumbnailData;
import com.example.nemologic.data.SqlManager;
import com.example.nemologic.game.GameFragment;
import com.example.nemologic.mainactivity.MainActivity;

public class RvBigPuzzleAdapter extends RecyclerView.Adapter<RvBigPuzzleAdapter.ViewHolder> {

    BigPuzzleData[] bpds;
    Context ctx;
    int rowItemNum;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView) ;
            // 뷰 객체에 대한 참조. (hold strong reference)
        }
    }

    RvBigPuzzleAdapter(Context ctx, BigPuzzleData[] bigPuzzleData, int rowItemNum) {
        //생성자
        this.ctx = ctx;
        this.bpds = bigPuzzleData;
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
    public void onBindViewHolder(ViewHolder holder, final int position) {

        //레벨 아이템 이름 표시
        ((TextView) holder.itemView.findViewById(R.id.tv_item_level_name)).setText(this.bpds[position].name);

        //레벨 아이템 사이즈 표시
        String temp = bpds[position].width + "X" + bpds[position].height;

        ((TextView)holder.itemView.findViewById(R.id.tv_item_level_size)).setText(temp);

        //레벨 아이템 진행 상황 표시
        ImageView iv_thumbnail = holder.itemView.findViewById(R.id.iv_thumbnail);
       if(bpds[position].progress== 2)
        {
            //완료한 게임이면
            //컬러셋을 가져온다
            iv_thumbnail.setImageBitmap(bpds[position].bitmap);
        }
       else
       {
           //완료되지 않은 게임이면
           //물음표 이미지를 띄워준다.
       }

        holder.itemView.findViewById(R.id.cl_touchbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //클릭 시 해당 퍼즐의 레벨들을 보여주는 BigLevelFragment 로 이동한다.
                Fragment dest = new BigLevelFragment(ctx);
                // Fragment 생성
                Bundle bundle = new Bundle();
                bundle.putInt("p_id", bpds[position].id);
                bundle.putString("p_name", bpds[position].name);
                dest.setArguments(bundle);
                ((MainActivity)ctx).fragmentMove(dest);
            }
        });
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return bpds.length;
    }

}