package com.example.nemologic.levelcreate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.StringParser;

import java.sql.SQLException;

import static android.app.Activity.RESULT_OK;

public class LevelCreateFragment extends Fragment {

    Context ctx;
    ImageView iv;
    LevelCreator levelCreator;
    RecyclerView rv_board;
    int actionType;

    EditText et_name;
    EditText et_height;
    EditText et_width;

    int width;
    int height;

    Button btn_make;

    private final int REQ_LOAD_IMAGE = 2;

    public LevelCreateFragment(Context ctx) {
        // Required empty public constructor
        this.ctx = ctx;
    }

    private void loadFile()
    {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"ClickableViewAccessibility"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Context mainActivityCtx = ctx;

        final View fragmentView = inflater.inflate(R.layout.fragment_levelcreate, container, false);

        et_name = fragmentView.findViewById(R.id.et_name);
        et_height = fragmentView.findViewById(R.id.et_height);
        et_width = fragmentView.findViewById(R.id.et_width);

        levelCreator = new LevelCreator();

        rv_board = fragmentView.findViewById(R.id.rv_levelcreate);

        iv = fragmentView.findViewById(R.id.iv_levelcreate);

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

        return fragmentView;
    }

    private void removePrevData()
    {
        actionType = 0;
        btn_make.setText("load image");
        iv.setImageDrawable(null);
        rv_board.removeAllViews();
        et_name.setText("");
        et_width.setText("");
        et_height.setText("");
    }

    private void buttonAction()
    {
        switch (actionType)
        {
            case 0:
                //load image
                openFile(null);
                actionType = 1;
                btn_make.setText("make puzzle");
                break;
            case 1:
                //make bitmap puzzle

                if(et_height.getText().toString().length() == 0)
                {
                    height = 10;
                }
                else
                {
                    height = Integer.parseInt(et_height.getText().toString());
                }

                if(et_width.getText().toString().length() == 0)
                {
                    width = 10;
                }
                else
                {
                    width = Integer.parseInt(et_width.getText().toString());
                }

                makeLevel(height, width);

                btn_make.setText("save puzzle");
                actionType = 2;
                break;
            case 2:
                //save bitmap
                saveLevel();
                removePrevData();
                break;
        }
    }

    private void openFile(Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
        }

        startActivityForResult(intent, REQ_LOAD_IMAGE);
    }

    private void makeLevel(int height, int width)
    {
        rv_board.setLayoutManager(new GridLayoutManager(ctx, width));
        levelCreator.reduceImageSize(width, height);
        levelCreator.makeDataSet();
        rv_board.setAdapter(new RvLevelCreateBoardAdapter(levelCreator.getResultPixels()));
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
        String dataSet = StringParser.parseDataSetToString(levelCreator.getResultDataSet(), height, width);
        String colorSet = StringParser.parseColorSetToString(levelCreator.getResultPixels(), height, width);
        mDbOpenHelper.insertLevel(name, "custom", width, height, dataSet, colorSet);
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
                    iv.setImageURI(uri);
                    levelCreator.loadFile(ctx, uri);
                }

            } else {   // RESULT_CANCEL
            }
//        } else if (requestCode == REQUEST_ANOTHER) {
//            ...
        }
    }
}