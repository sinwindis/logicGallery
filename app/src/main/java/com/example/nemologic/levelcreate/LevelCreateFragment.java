package com.example.nemologic.levelcreate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import static android.app.Activity.RESULT_OK;

public class LevelCreateFragment extends Fragment {

    Context ctx;
    ImageView iv;
    LevelCreator levelCreator;
    RecyclerView rv_board;

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

        levelCreator = new LevelCreator();

        rv_board = fragmentView.findViewById(R.id.rv_levelcreate);


        iv = fragmentView.findViewById(R.id.iv_levelcreate);
        iv.setImageBitmap(levelCreator.getBitmap());

        Button btn_make = fragmentView.findViewById(R.id.btn_make);
        btn_make.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openFile(null);
            }
        });

        return fragmentView;
    }

    private static final int PICK_PDF_FILE = 2;

    private void openFile(Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, PICK_PDF_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_FILE) {
            if (resultCode == RESULT_OK)
            {
                Uri uri = null;
                if (data != null) {
                    uri = data.getData();
                    iv.setImageURI(uri);
                    levelCreator.loadFile(ctx, uri);
                    rv_board.setLayoutManager(new GridLayoutManager(ctx, 40));
                    levelCreator.reduceImageSize(40, 40);
                    levelCreator.makeDataSet();
                    rv_board.setAdapter(new RvLevelCreateBoardAdapter(levelCreator.getResultDataSet()));
                    // Perform operations on the document using its URI.
                }

            } else {   // RESULT_CANCEL
            }
//        } else if (requestCode == REQUEST_ANOTHER) {
//            ...
        }
    }
}