package com.example.nemologic.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;
import com.example.nemologic.biglevel.BigLevelFragment;
import com.example.nemologic.data.BigPuzzleData;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.StringGetter;
import com.example.nemologic.mainactivity.MainActivity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

public class RvGalleryAdapter extends RecyclerView.Adapter<RvGalleryAdapter.ViewHolder> {

    private ArrayList<BigPuzzleData> bigPuzzleData;
    private Context ctx;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView) ;
            // 뷰 객체에 대한 참조. (hold strong reference)
        }
    }

    RvGalleryAdapter(Context ctx, ArrayList<BigPuzzleData> bigPuzzleData) {
        //생성자
        this.ctx = ctx;
        this.bigPuzzleData = bigPuzzleData;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @NonNull
    @Override
    public RvGalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.item_gallery, parent, false);
        view.setLayoutParams(new RecyclerView.LayoutParams(parent.getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT));

        return new ViewHolder(view);
    }

    // onBindViewHolder() - position 에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        //퍼즐 이름 표시
        TextView tv_puzzleName = holder.itemView.findViewById(R.id.tv_gallery_item_puzzle_name);
        tv_puzzleName.setText(this.bigPuzzleData.get(position).name);

        //퍼즐 아티스트 이름 표시
        TextView tv_artistName = holder.itemView.findViewById(R.id.tv_gallery_item_artist_name);
        String strArtistName = StringGetter.a_name.get(this.bigPuzzleData.get(position).a_id);
        tv_artistName.setText(strArtistName);

        //퍼즐 진행도 표시
        int puzzleWidth = bigPuzzleData.get(position).width;
        int puzzleHeight = bigPuzzleData.get(position).height;

        String strPuzzleProgress = bigPuzzleData.get(position).progress + "/" + puzzleHeight*puzzleWidth;

        TextView tv_puzzleSize = holder.itemView.findViewById(R.id.tv_gallery_item_progress);
        tv_puzzleSize.setText(strPuzzleProgress);

        //레벨 아이템 사이즈 표시
        int levelWidth = bigPuzzleData.get(position).l_width;
        int levelHeight = bigPuzzleData.get(position).l_height;

        String strLevelSize = levelWidth + "X" + levelHeight;

        TextView tv_levelSize = holder.itemView.findViewById(R.id.tv_gallery_item_level_size);
        tv_levelSize.setText(strLevelSize);


        //레벨 아이템 진행 상황 표시
        ImageView iv_thumbnail = holder.itemView.findViewById(R.id.iv_thumbnail);
        if(bigPuzzleData.get(position).progress == bigPuzzleData.get(position).width*bigPuzzleData.get(position).height)
        {
            //완료한 게임이면
            //컬러셋을 가져온다
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bigPuzzleData.get(position).bitmap, 400, 400, false);
            iv_thumbnail.setImageBitmap(scaledBitmap);
        }
       else
       {
           //완료되지 않은 게임이면
           //물음표 이미지를 띄워준다.
           iv_thumbnail.setImageResource(R.drawable.ic_unknown);
       }

        holder.itemView.findViewById(R.id.cl_touchbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //클릭 시 해당 퍼즐의 레벨들을 보여주는 BigLevelFragment 로 이동한다.
                Fragment dest = new BigLevelFragment(ctx);
                // Fragment 생성
                Bundle bundle = new Bundle();
                bundle.putInt("p_id", bigPuzzleData.get(position).id);
                bundle.putString("p_name", bigPuzzleData.get(position).name);
                dest.setArguments(bundle);
                ((MainActivity)ctx).fragmentMove(dest);
            }
        });

        if(bigPuzzleData.get(position).custom == 1)
        {
            Button btn_delete = holder.itemView.findViewById(R.id.btn_delete);
            btn_delete.setAlpha(1.0F);

            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
                    try {
                        mDbOpenHelper.open();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    mDbOpenHelper.deleteCustomBigPuzzle(bigPuzzleData.get(position).id);

                    mDbOpenHelper.close();

                    bigPuzzleData.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, bigPuzzleData.size());
                }
            });
        }
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return bigPuzzleData.size();
    }
}