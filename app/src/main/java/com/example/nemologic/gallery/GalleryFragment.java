package com.example.nemologic.gallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.nemologic.R;
import com.example.nemologic.animation.ButtonAnimation;
import com.example.nemologic.biglevel.BigLevelFragment;
import com.example.nemologic.data.BigPuzzleData;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.SqlManager;
import com.example.nemologic.data.StringGetter;
import com.example.nemologic.mainactivity.MainActivity;

import java.sql.SQLException;

public class GalleryFragment extends Fragment {

    private Context ctx;

    private TextView tv_level_num;

    private BigPuzzleData bigPuzzleDataTemp;

    private DbOpenHelper mDbOpenHelper;

    private int puzzlePosition = 0;
    private int puzzleNum = 0;

    //액자 부분 뷰
    private TextView tv_puzzleName;
    private TextView tv_artistName;
    private TextView tv_puzzleSize;
    private ImageView iv_thumbnail;
    private TextView tv_levelSize;
    private Button btn_delete;

    private Cursor bigPuzzleCursor;

    public GalleryFragment(Context ctx) {
        // Required empty public constructor
        this.ctx = ctx;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_gallery, container, false);

        final ConstraintLayout cl_frame = fragmentView.findViewById(R.id.cl_frame);

        tv_puzzleName = fragmentView.findViewById(R.id.tv_gallery_item_puzzle_name);
        tv_artistName = fragmentView.findViewById(R.id.tv_gallery_item_artist_name);
        tv_puzzleSize = fragmentView.findViewById(R.id.tv_gallery_item_progress);
        iv_thumbnail = fragmentView.findViewById(R.id.iv_thumbnail);
        tv_levelSize = fragmentView.findViewById(R.id.tv_gallery_item_level_size);
        btn_delete = fragmentView.findViewById(R.id.btn_delete);
        ConstraintLayout cl_touchBox = fragmentView.findViewById(R.id.cl_touchbox);

        tv_level_num = fragmentView.findViewById(R.id.tv_level_num);

        loadCursor();

        //클릭 애니메이션 설정
        @SuppressLint("UseCompatLoadingForDrawables")
        final Drawable press = ctx.getResources().getDrawable(R.drawable.background_gallery_image_press);
        @SuppressLint("UseCompatLoadingForDrawables")
        final Drawable up = ctx.getResources().getDrawable(R.drawable.background_gallery_image);
        cl_touchBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    if(view != null)
                    {
                        cl_frame.setBackground(press);

                    }


                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_POINTER_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL){

                    if(view != null)
                    {
                        cl_frame.setBackground(up);
                    }
                }
                return false;
            }
        });

        cl_touchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //클릭 시 해당 퍼즐의 레벨들을 보여주는 BigLevelFragment 로 이동한다.
                Fragment dest = new BigLevelFragment(ctx);
                // Fragment 생성
                Bundle bundle = new Bundle();
                bundle.putInt("p_id", bigPuzzleDataTemp.id);
                bundle.putString("p_name", bigPuzzleDataTemp.name);
                dest.setArguments(bundle);
                ((MainActivity)ctx).fragmentMove(dest);
            }
        });

        //프래그먼트 버튼 이벤트 셋
        ImageView img_back = fragmentView.findViewById(R.id.img_back);

        ButtonAnimation.setOvalButtonAnimationNormal(img_back);

        img_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                assert getFragmentManager() != null;
                getFragmentManager().popBackStackImmediate();
            }
        });

        ImageView img_prev = fragmentView.findViewById(R.id.img_prev);

        ButtonAnimation.setRoundButtonAnimationNormal(img_prev);

        img_prev.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //이전 레벨
                if(puzzlePosition == 0)
                    puzzlePosition = puzzleNum - 1;
                else
                    puzzlePosition--;

                loadPuzzle(puzzlePosition);
                showPuzzleData();
            }
        });

        ImageView img_next = fragmentView.findViewById(R.id.img_next);

        ButtonAnimation.setRoundButtonAnimationNormal(img_next);

        img_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //다음 레벨
                if(puzzlePosition == puzzleNum - 1)
                    puzzlePosition = 0;
                else
                    puzzlePosition++;

                loadPuzzle(puzzlePosition);
                showPuzzleData();
            }
        });

        //레벨 지우기 버튼
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
                try {
                    mDbOpenHelper.open();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                mDbOpenHelper.deleteCustomBigPuzzle(bigPuzzleDataTemp.id);

                mDbOpenHelper.close();

                //지우고 나면 화면에 뭐 보여줄지 코딩
            }
        });

        return fragmentView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        loadPuzzle(puzzlePosition);
        showPuzzleData();
        //퍼즐 클리어하고 돌아왔을 때 화면 갱신해줘야 함
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        mDbOpenHelper.close();
    }

    private void loadCursor()
    {
        mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        bigPuzzleCursor = mDbOpenHelper.getBigPuzzleCursor();
        puzzleNum = bigPuzzleCursor.getCount();
    }

    private void loadPuzzle(int position)
    {
        Log.d("loadPuzzle", "load start");
        bigPuzzleCursor.moveToPosition(position);

        int id = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.ID));
        int a_id = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.A_ID));
        String name = StringGetter.p_name.get(id);
        int width = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.WIDTH));
        int height = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.HEIGHT));
        int progress = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.PROGRESS));
        int l_width = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.L_WIDTH));
        int l_height = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.L_HEIGHT));

        byte[] colorSet = bigPuzzleCursor.getBlob(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.COLORSET));
        int custom = 0;

        Bitmap bitmap = BitmapFactory.decodeByteArray( colorSet, 0, colorSet.length);

        bigPuzzleDataTemp = new BigPuzzleData(id, a_id, name, bitmap, width, height, l_width, l_height, progress, custom);

        Log.d("loadPuzzle", "load end");
    }

    @SuppressLint("SetTextI18n")
    private void showPuzzleData() {


        Log.d("showPuzzleData", "refreshing view");
        //퍼즐 이름 표시

        tv_puzzleName.setText(bigPuzzleDataTemp.name);

        //퍼즐 아티스트 이름 표시

        String strArtistName = StringGetter.a_name.get(bigPuzzleDataTemp.a_id);
        tv_artistName.setText(strArtistName);

        //퍼즐 진행도 표시
        int puzzleWidth = bigPuzzleDataTemp.width;
        int puzzleHeight = bigPuzzleDataTemp.height;

        String strPuzzleProgress = bigPuzzleDataTemp.progress + "/" + puzzleHeight*puzzleWidth;

        tv_puzzleSize.setText(strPuzzleProgress);

        //레벨 아이템 사이즈 표시
        int levelWidth = bigPuzzleDataTemp.l_width;
        int levelHeight = bigPuzzleDataTemp.l_height;

        String strLevelSize = levelWidth + "X" + levelHeight;

        tv_levelSize.setText(strLevelSize);

        tv_level_num.setText((puzzlePosition+1) + "/" + (puzzleNum));


        //레벨 아이템 진행 상황 표시

        if(true||bigPuzzleDataTemp.progress == bigPuzzleDataTemp.width*bigPuzzleDataTemp.height)
        {
            //완료한 게임이면
            //컬러셋을 가져온다
            iv_thumbnail.setImageBitmap(bigPuzzleDataTemp.bitmap);
        }
        else
        {
            //완료되지 않은 게임이면
            //물음표 이미지를 띄워준다.
            iv_thumbnail.setImageResource(R.drawable.ic_unknown);
        }

        if(bigPuzzleDataTemp.custom == 1)
        {
            btn_delete.setAlpha(1.0F);
        }
    }
}