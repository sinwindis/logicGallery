package com.example.nemologic.biglevel;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;
import com.example.nemologic.data.BigLevelData;
import com.example.nemologic.game.GameFragment;
import com.example.nemologic.mainactivity.MainActivity;

public class RvBigLevelAdapter extends RecyclerView.Adapter<RvBigLevelAdapter.ViewHolder> {

    BigLevelData[] data;
    Context ctx;
    int puzzleSize;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView) ;
            // 뷰 객체에 대한 참조. (hold strong reference)
        }
    }

    RvBigLevelAdapter(Context ctx, BigLevelData[] bigLevelThumbnailData, int puzzleWidth, int puzzleHeight, int parentWidth, int parentHeight) {
        //생성자
        this.ctx = ctx;
        this.data = bigLevelThumbnailData;

        int widthTemp = parentWidth / puzzleWidth;
        int heightTemp = parentHeight / puzzleHeight;

        puzzleSize = Math.min(widthTemp, heightTemp);

        Log.d("RvBigLevelAdapter", "puzzleSize: " + puzzleSize);
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @NonNull
    @Override
    public RvBigLevelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_biglevel, parent, false);

        view.setLayoutParams(new RecyclerView.LayoutParams(puzzleSize, puzzleSize));

        return new ViewHolder(view);
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        ImageView iv = holder.itemView.findViewById(R.id.iv_item_level);
        Bitmap bitmap;

        switch (data[position].getProgress())
        {
            case 0:
                //한번도 안했으면
                //물음표 이미지 띄우기
                iv.setImageResource(R.drawable.ic_unknown);
                break;
            case 1:
                //저장된 게임일 경우
                bitmap = Bitmap.createScaledBitmap(data[position].getSaveBitmap(), puzzleSize, puzzleSize, false);
                iv.setImageBitmap(bitmap);
                break;
            case 2:
            case 3:
                //완료한 게임일 경우
                bitmap = Bitmap.createScaledBitmap(data[position].getColorBitmap(), puzzleSize, puzzleSize, false);
                iv.setImageBitmap(bitmap);
                break;
        }

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //클릭 시 해당 퍼즐의 게임을 실행한다
                Fragment dest = new GameFragment(ctx);
                // Fragment 생성
                Bundle bundle = new Bundle();
                //type 가 1이면 빅레벨 테이블에서 가져와야 함
                bundle.putBoolean("custom", data[position].getCustom());
                bundle.putInt("id", data[position].getId());
                dest.setArguments(bundle);
                ((MainActivity)ctx).fragmentMove(dest);
            }
        });
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return data.length;
    }

}