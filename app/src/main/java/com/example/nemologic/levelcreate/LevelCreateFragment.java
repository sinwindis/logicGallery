package com.example.nemologic.levelcreate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.nemologic.R;
import com.example.nemologic.data.DbOpenHelper;

import java.sql.SQLException;

import static android.app.Activity.RESULT_OK;

public class LevelCreateFragment extends Fragment {

    Context ctx;
    ImageView iv_input;
    LevelCreator levelCreator;
    ImageView iv_result;
    int actionType;

    EditText et_name;
    EditText et_height;
    EditText et_width;
    EditText et_puzzle_height;
    EditText et_puzzle_width;

    TextView tv_bigpuzzle;

    Bitmap dataBitmap;
    Bitmap colorBitmap;

    int width;
    int height;
    int puzzle_width;
    int puzzle_height;

    Button btn_make;
    CheckBox cb_big;

    private final int REQ_LOAD_IMAGE = 2;

    public LevelCreateFragment(Context ctx) {
        // Required empty public constructor
        this.ctx = ctx;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"ClickableViewAccessibility"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_levelcreate, container, false);

        et_name = fragmentView.findViewById(R.id.et_name);
        et_height = fragmentView.findViewById(R.id.et_height);
        et_width = fragmentView.findViewById(R.id.et_width);
        et_puzzle_height = fragmentView.findViewById(R.id.et_row);
        et_puzzle_width = fragmentView.findViewById(R.id.et_column);
        cb_big = fragmentView.findViewById(R.id.cb_bigpuzzle);
        tv_bigpuzzle = fragmentView.findViewById(R.id.tv_input_bigpuzzle);
        ImageView img_back = fragmentView.findViewById(R.id.img_back);

        levelCreator = new LevelCreator();

        iv_result = fragmentView.findViewById(R.id.iv_result);

        iv_input = fragmentView.findViewById(R.id.iv_input);

        btn_make = fragmentView.findViewById(R.id.btn_make);
        btn_make.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                buttonAction();
            }
        });

        Button btn_cancel = fragmentView.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePrevData();
            }
        });

        cb_big.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    //체크되었으면
                    tv_bigpuzzle.setTextColor(Color.parseColor("#404040"));
                    et_puzzle_height.setFocusable(true);
                    et_puzzle_height.setFocusableInTouchMode(true);
                    et_puzzle_height.setClickable(true);
                    et_puzzle_width.setFocusable(true);
                    et_puzzle_width.setFocusableInTouchMode(true);
                    et_puzzle_width.setClickable(true);
                }
                else
                {
                    tv_bigpuzzle.setTextColor(Color.parseColor("#a0a0a0"));
                    et_puzzle_height.setFocusable(false);
                    et_puzzle_height.setFocusableInTouchMode(false);
                    et_puzzle_height.setClickable(false);
                    et_puzzle_width.setFocusable(false);
                    et_puzzle_width.setFocusableInTouchMode(false);
                    et_puzzle_width.setClickable(false);
                }
            }
        });



        img_back.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getActionMasked())
                {
                    case MotionEvent.ACTION_DOWN:
                        view.setBackground(ctx.getResources().getDrawable(R.drawable.background_btn_shadow));
                        break;

                    case MotionEvent.ACTION_UP:
                        view.setBackground(null);
                        break;
                }
                return false;
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                getFragmentManager().popBackStackImmediate();

            }
        });

        return fragmentView;
    }
    
    private void setEditable(boolean b)
    {
        et_height.setClickable(b);
        et_height.setFocusable(b);
        et_height.setFocusableInTouchMode(b);
        et_width.setClickable(b);
        et_width.setFocusable(b);
        et_width.setFocusableInTouchMode(b);
        et_puzzle_height.setFocusable(b);
        et_puzzle_height.setFocusableInTouchMode(b);
        et_puzzle_height.setClickable(b);
        et_puzzle_width.setFocusable(b);
        et_puzzle_width.setFocusableInTouchMode(b);
        et_puzzle_width.setClickable(b);
        cb_big.setFocusable(b);
        cb_big.setFocusableInTouchMode(b);
        cb_big.setClickable(b);
    }

    private void removePrevData()
    {
        actionType = 0;
        btn_make.setText(getResources().getString(R.string.str_loadimage));
        iv_input.setImageDrawable(null);
        iv_result.setImageDrawable(null);

        //recyclerView 내부 아이템 비우기

        et_name.setText("");
        et_width.setText("");
        et_height.setText("");
        et_puzzle_width.setText("");
        et_puzzle_height.setText("");

        setEditable(true);

        cb_big.setChecked(false);
    }

    @SuppressLint("SetTextI18n")
    private void buttonAction()
    {
        switch (actionType)
        {
            case 0:
                //load image
                openFile();
                break;
            case 1:
                //make bitmap puzzle

                setEditable(false);

                if(et_height.getText().toString().length() == 0)
                {
                    height = 10;
                    et_height.setText("10");
                }
                else
                {
                    height = Integer.parseInt(et_height.getText().toString());
                }

                if(et_width.getText().toString().length() == 0)
                {
                    width = 10;
                    et_width.setText("10");
                }
                else
                {
                    width = Integer.parseInt(et_width.getText().toString());
                }

                if(cb_big.isChecked())
                {
                    //big level 일 경우 big level 크기도 기본값을 설정해 준다.
                    if(et_puzzle_height.getText().toString().length() == 0)
                    {
                        puzzle_height = 5;
                        et_puzzle_height.setText("5");
                    }
                    else
                    {
                        puzzle_height = Integer.parseInt(et_height.getText().toString());
                    }

                    if(et_puzzle_width.getText().toString().length() == 0)
                    {
                        puzzle_width = 5;
                        et_puzzle_width.setText("5");
                    }
                    else
                    {
                        puzzle_width = Integer.parseInt(et_width.getText().toString());
                    }

                }

                makeLevel();

                btn_make.setText(getResources().getString(R.string.str_savepuzzle));
                actionType = 2;
                break;
            case 2:
                //save bitmap
                if(et_name.length() > 0)
                {
                    String alert = getResources().getString(R.string.str_levelsaved);
                    Toast.makeText(ctx, et_name.getText() + alert, Toast.LENGTH_SHORT).show();

                    if(cb_big.isChecked())
                    {
                        saveBigLevel();
                    }
                    else
                    {
                        saveLevel();
                    }

                    removePrevData();
                }
                else
                {
                    String alert = getResources().getString(R.string.str_reqname);
                    Toast.makeText(ctx, alert, Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, (Uri) null);
        }

        startActivityForResult(intent, REQ_LOAD_IMAGE);
    }

    private void makeLevel()
    {
        if(cb_big.isChecked())
        {
            //빅 레벨 만들기
            levelCreator.makeBigLevelDataSet(puzzle_height, puzzle_width, height, width);
        }
        else
        {
            //싱글 레벨 만들기
            levelCreator.makeSingleDataSet(height, width);
        }


        //Bitmap resultImage = CustomParser.parseDataSetByteArrayToBitmap(levelCreator.getDataBlob(), width, height);
        Bitmap resultImage = levelCreator.getScaledBitmap();

        iv_result.setImageBitmap(resultImage);
    }

    private void saveLevel()
    {
        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String name = et_name.getText().toString();
        byte[] dataBlob = levelCreator.getDataBlob();
        byte[] colorBlob = levelCreator.getColorBlob();
        mDbOpenHelper.insertLevel(name, getResources().getString(R.string.str_custom), width, height, dataBlob, colorBlob, 1);
        mDbOpenHelper.close();
    }

    private void saveBigLevel()
    {
        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String name = et_name.getText().toString();
        byte[] colorBlob = levelCreator.getColorBlob();
        int p_id = (int) mDbOpenHelper.insertBigPuzzle(name, puzzle_width, puzzle_height, colorBlob, 1);

        Log.d("saveBigLevel", String.valueOf(p_id));

        byte[][] dataBlobs = levelCreator.getDataBlobs();
        byte[][] colorBlobs = levelCreator.getColorBlobs();

        for(int i = 0; i < puzzle_width*puzzle_height; i++)
        {
            mDbOpenHelper.insertBigLevel(p_id, i, width, height, dataBlobs[i], colorBlobs[i]);
        }

        mDbOpenHelper.close();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_LOAD_IMAGE) {
            if (resultCode == RESULT_OK)
            {
                Uri uri;
                if (data != null) {
                    uri = data.getData();
                    iv_input.setImageURI(uri);
                    levelCreator.loadFile(ctx, uri);
                    actionType = 1;
                    btn_make.setText(getResources().getString(R.string.str_makepuzzle));
                }

            } else {   // RESULT_CANCEL
            }
//        } else if (requestCode == REQUEST_ANOTHER) {
//            ...
        }
    }
}