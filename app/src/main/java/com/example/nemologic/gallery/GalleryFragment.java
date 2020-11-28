package com.example.nemologic.gallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
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
import java.util.Objects;

public class GalleryFragment extends Fragment {

    private Context ctx;

    private TextView tv_level_num;

    private BigPuzzleData bigPuzzleDataTemp;

    private DbOpenHelper mDbOpenHelper;

    private int puzzlePosition = 0;
    private int puzzleNum = 0;
    private int customPuzzleNum = 0;

    //액자 부분 뷰
    private TextView tv_puzzleName;
    private TextView tv_artistName;
    private TextView tv_puzzleSize;
    private ImageView iv_thumbnail;
    private TextView tv_levelSize;
    private ImageView btn_delete;

    private Cursor bigPuzzleCursor;
    private Cursor customBigPuzzleCursor;


    public GalleryFragment(Context ctx) {
        // Required empty public constructor
        this.ctx = ctx;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_gallery, container, false);

        final ConstraintLayout cl_frame = fragmentView.findViewById(R.id.cl_frame);

        tv_puzzleName = fragmentView.findViewById(R.id.tv_gallery_item_puzzle_name);
        tv_artistName = fragmentView.findViewById(R.id.tv_gallery_item_artist_name);
        tv_puzzleSize = fragmentView.findViewById(R.id.tv_gallery_item_progress);
        iv_thumbnail = fragmentView.findViewById(R.id.iv_thumbnail);
        tv_levelSize = fragmentView.findViewById(R.id.tv_gallery_item_level_size);
        btn_delete = fragmentView.findViewById(R.id.img_delete);
        ConstraintLayout cl_touchBox = fragmentView.findViewById(R.id.cl_touch);

        tv_level_num = fragmentView.findViewById(R.id.tv_level_num);



        mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        loadCursor();

        //클릭 애니메이션 설정
        @SuppressLint("UseCompatLoadingForDrawables")
        final Drawable press = ctx.getResources().getDrawable(R.drawable.background_frame_press);
        @SuppressLint("UseCompatLoadingForDrawables")
        final Drawable up = ctx.getResources().getDrawable(R.drawable.background_frame);
        cl_touchBox.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
                bundle.putBoolean("custom", bigPuzzleDataTemp.custom);
                Log.d("galleryFrag", "p_id: " + bigPuzzleDataTemp.id + " custom: " + bigPuzzleDataTemp.custom);
                dest.setArguments(bundle);
                ((MainActivity)ctx).fragmentMove(dest);
            }
        });

        //프래그먼트 버튼 이벤트 셋
        ImageView img_back = fragmentView.findViewById(R.id.img_back);

        ButtonAnimation.setOvalButtonAnimationBlack(img_back);

        img_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                assert getFragmentManager() != null;
                getFragmentManager().popBackStackImmediate();
            }
        });

        //검색 버튼
//        ImageView img_search = fragmentView.findViewById(R.id.img_open_search);
//
//        ButtonAnimation.setOvalButtonAnimationNormal(img_search);
//
//        img_search.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                //클릭 시 해당 퍼즐의 레벨들을 보여주는 BigLevelFragment 로 이동한다.
//                Fragment dest = new SearchFragment(ctx);
//                // Fragment 생성
//                ((MainActivity)ctx).fragmentMove(dest);
//            }
//        });

        ImageView img_prev = fragmentView.findViewById(R.id.img_prev);

        ButtonAnimation.setOvalButtonAnimationShadow(img_prev);

        img_prev.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //이전 레벨
                if(puzzlePosition == 0)
                    puzzlePosition = puzzleNum + customPuzzleNum - 1;
                else
                    puzzlePosition--;

                getPuzzle(puzzlePosition);
                showPuzzleData();
            }
        });

        ImageView img_next = fragmentView.findViewById(R.id.img_next);

        ButtonAnimation.setOvalButtonAnimationShadow(img_next);

        img_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //다음 레벨
                if(puzzlePosition == puzzleNum + customPuzzleNum - 1)
                    puzzlePosition = 0;
                else
                    puzzlePosition++;

                getPuzzle(puzzlePosition);
                showPuzzleData();
            }
        });

        //레벨 지우기 버튼
        ButtonAnimation.setOvalButtonAnimationBlack(btn_delete);

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_delete, null);
                builder.setView(dialogView);

                final AlertDialog dialog = builder.create();

                Button btn_accept = dialogView.findViewById(R.id.btn_accept);
                Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);

                btn_accept.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                        deletePuzzle();
                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }
        });

        return fragmentView;
    }

    private void deletePuzzle()
    {
        mDbOpenHelper.deleteCustomBigPuzzle(bigPuzzleDataTemp.id);
        loadCursor();

        if(puzzlePosition == puzzleNum + customPuzzleNum)
            puzzlePosition--;

        getPuzzle(puzzlePosition);
        showPuzzleData();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getPuzzle(puzzlePosition);
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
        bigPuzzleCursor = mDbOpenHelper.getBigPuzzleCursor();
        customBigPuzzleCursor = mDbOpenHelper.getCustomBigPuzzleCursor();
        puzzleNum = bigPuzzleCursor.getCount();
        customPuzzleNum = customBigPuzzleCursor.getCount();
    }

    private void getPuzzle(int position)
    {
        if(position > puzzleNum - 1)
        {
            loadCustomPuzzle(position - puzzleNum);
        }
        else
        {
            loadPuzzle(position);
        }
    }

    private void loadPuzzle(int position)
    {
        bigPuzzleCursor.moveToPosition(position);

        int id = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.ID));
        String p_name = StringGetter.p_name.get(id);
        int a_id = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.A_ID));
        String a_name = StringGetter.a_name.get(a_id);
        int width = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.P_WIDTH));
        int height = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.P_HEIGHT));
        int progress = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.PROGRESS));
        int l_width = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.L_WIDTH));
        int l_height = bigPuzzleCursor.getInt(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.L_HEIGHT));

        byte[] colorSet = bigPuzzleCursor.getBlob(bigPuzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.COLORSET));

        Bitmap bitmap = BitmapFactory.decodeByteArray( colorSet, 0, colorSet.length);

        bigPuzzleDataTemp = new BigPuzzleData(id, a_name, p_name, bitmap, width, height, l_width, l_height, progress, false);
    }

    private void loadCustomPuzzle(int position)
    {
        customBigPuzzleCursor.moveToPosition(position);

        int id = customBigPuzzleCursor.getInt(customBigPuzzleCursor.getColumnIndex(SqlManager.CustomBigPuzzleDBSql.ID));
        String p_name = customBigPuzzleCursor.getString(customBigPuzzleCursor.getColumnIndex(SqlManager.CustomBigPuzzleDBSql.P_NAME));
        String a_name = customBigPuzzleCursor.getString(customBigPuzzleCursor.getColumnIndex(SqlManager.CustomBigPuzzleDBSql.A_NAME));
        int width = customBigPuzzleCursor.getInt(customBigPuzzleCursor.getColumnIndex(SqlManager.CustomBigPuzzleDBSql.P_WIDTH));
        int height = customBigPuzzleCursor.getInt(customBigPuzzleCursor.getColumnIndex(SqlManager.CustomBigPuzzleDBSql.P_HEIGHT));
        int progress = customBigPuzzleCursor.getInt(customBigPuzzleCursor.getColumnIndex(SqlManager.CustomBigPuzzleDBSql.PROGRESS));
        int l_width = customBigPuzzleCursor.getInt(customBigPuzzleCursor.getColumnIndex(SqlManager.CustomBigPuzzleDBSql.L_WIDTH));
        int l_height = customBigPuzzleCursor.getInt(customBigPuzzleCursor.getColumnIndex(SqlManager.CustomBigPuzzleDBSql.L_HEIGHT));

        byte[] colorSet = customBigPuzzleCursor.getBlob(customBigPuzzleCursor.getColumnIndex(SqlManager.CustomBigPuzzleDBSql.COLORSET));

        Bitmap bitmap = BitmapFactory.decodeByteArray( colorSet, 0, colorSet.length);

        bigPuzzleDataTemp = new BigPuzzleData(id, a_name, p_name, bitmap, width, height, l_width, l_height, progress, true);
    }

    @SuppressLint("SetTextI18n")
    private void showPuzzleData() {


        Log.d("showPuzzleData", "refreshing view");
        //퍼즐 이름 표시

        tv_puzzleName.setText(bigPuzzleDataTemp.p_name);

        //퍼즐 아티스트 이름 표시

        String strArtistName = bigPuzzleDataTemp.a_name;
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

        tv_level_num.setText((puzzlePosition+1) + "/" + (puzzleNum+customPuzzleNum));


        //퍼즐 썸네일 표시
        if(bigPuzzleDataTemp.progress == bigPuzzleDataTemp.width*bigPuzzleDataTemp.height)
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

        if(bigPuzzleDataTemp.custom)
        {
            btn_delete.setVisibility(View.VISIBLE);
        }
        else
        {
            btn_delete.setVisibility(View.GONE);
        }
    }
}