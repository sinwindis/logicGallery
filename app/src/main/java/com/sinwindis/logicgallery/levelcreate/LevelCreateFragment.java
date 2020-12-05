package com.sinwindis.logicgallery.levelcreate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.sinwindis.logicgallery.R;
import com.sinwindis.logicgallery.data.DbOpenHelper;

import java.sql.SQLException;

import static android.app.Activity.RESULT_OK;

public class LevelCreateFragment extends Fragment {

    Context ctx;
    ImageView iv_input;
    LevelCreator levelCreator;
    ImageView iv_result;
    int actionType;

    EditText et_p_name;
    EditText et_a_name;
    EditText et_height;
    EditText et_width;
    EditText et_puzzle_height;
    EditText et_puzzle_width;

    TextView tv_bigpuzzle;

    String p_name;
    String a_name;

    int l_width;
    int l_height;
    int p_width;
    int p_height;

    Button btn_make;

    private final int REQ_LOAD_IMAGE = 2;
    //private final int WRITE_REQUEST_CODE = 43;

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

        et_p_name = fragmentView.findViewById(R.id.et_p_name);
        et_a_name = fragmentView.findViewById(R.id.et_a_name);
        et_height = fragmentView.findViewById(R.id.et_height);
        et_width = fragmentView.findViewById(R.id.et_width);
        et_puzzle_height = fragmentView.findViewById(R.id.et_row);
        et_puzzle_width = fragmentView.findViewById(R.id.et_column);
        tv_bigpuzzle = fragmentView.findViewById(R.id.tv_input_count);
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


        //취소 버튼 액션
        Button btn_cancel = fragmentView.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePrevData();
            }
        });

        img_back.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        view.setBackground(ctx.getResources().getDrawable(R.drawable.background_btn_oval_shadow));
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

    private void setEditable(boolean b) {
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
    }

    private void removePrevData() {
        levelCreator.stopMakeBigLevel();

        actionType = 0;
        btn_make.setText(getResources().getString(R.string.str_loadimage));
        iv_input.setImageDrawable(null);
        iv_result.setImageDrawable(null);

        //recyclerView 내부 아이템 비우기

        et_p_name.setText("");
        et_a_name.setText("");
        et_width.setText("");
        et_height.setText("");
        et_puzzle_width.setText("");
        et_puzzle_height.setText("");

        setEditable(true);
    }

    @SuppressLint("SetTextI18n")
    private void buttonAction() {
        switch (actionType) {
            case 0:
                //load image
                openFile();
                break;
            case 1:
                //make bitmap puzzle
                setEditable(false);

                if (et_height.getText().toString().length() == 0) {
                    l_height = 10;
                    et_height.setText("10");
                } else {
                    l_height = Integer.parseInt(et_height.getText().toString());
                }

                if (et_width.getText().toString().length() == 0) {
                    l_width = 10;
                    et_width.setText("10");
                } else {
                    l_width = Integer.parseInt(et_width.getText().toString());
                }

                //big level 크기도 기본값을 설정해 준다.
                if (et_puzzle_height.getText().toString().length() == 0) {
                    p_height = 5;
                    et_puzzle_height.setText("5");
                } else {
                    p_height = Integer.parseInt(et_puzzle_height.getText().toString());
                }

                if (et_puzzle_width.getText().toString().length() == 0) {
                    p_width = 5;
                    et_puzzle_width.setText("5");
                } else {
                    p_width = Integer.parseInt(et_puzzle_width.getText().toString());
                }

                makeLevel();

                btn_make.setText(getResources().getString(R.string.str_savepuzzle));
                actionType = 2;
                break;
            case 2:
                //save bitmap
                if (et_p_name.length() > 0) {
                    p_name = et_p_name.getText().toString();
                    if (et_a_name.length() > 0) {
                        a_name = et_p_name.getText().toString();
                    } else {
                        a_name = getString(R.string.str_custom);
                    }

                    //나중에 기본 레벨 추가할 때 다시 쓰자
                    //saveDataFile();

                    saveBigLevel();
                    removePrevData();

                    String alert = getResources().getString(R.string.str_levelsaved);
                    Toast.makeText(ctx, p_name + alert, Toast.LENGTH_SHORT).show();
                } else {
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

    private void makeLevel() {
        final Handler setImageHandler = new Handler();

        iv_result.setImageResource(R.drawable.ic_unknown);

        Thread makeImageThread = new Thread(new Runnable() {
            @Override
            public void run() {

                levelCreator.makeBigLevelDataSet(p_height, p_width, l_height, l_width);

                // UI 작업 수행 X
                setImageHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // UI 작업 수행 O
                        iv_result.setImageBitmap(levelCreator.getScaledBitmap());
                    }
                });
            }
        });

        makeImageThread.start();
    }

    private void saveBigLevel() {
        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        byte[] colorBlob = levelCreator.getColorBlob();
        int p_id = (int) mDbOpenHelper.insertCustomBigPuzzle(a_name, p_name, p_width, p_height, l_width, l_height, colorBlob);

        Log.d("levelCreateFragment", "p_id: " + p_id);

        byte[][] dataBlobs = levelCreator.getDataBlobs();
        byte[][] colorBlobs = levelCreator.getColorBlobs();

        for (int i = 0; i < p_width * p_height; i++) {
            mDbOpenHelper.insertCustomBigLevel(p_id, i, l_width, l_height, dataBlobs[i], colorBlobs[i]);
        }

        mDbOpenHelper.close();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_LOAD_IMAGE) {
            if (resultCode == RESULT_OK) {
                Uri uri;
                if (data != null) {
                    uri = data.getData();
                    iv_input.setImageURI(uri);
                    levelCreator.loadFile(ctx, uri);
                    actionType = 1;
                    btn_make.setText(getResources().getString(R.string.str_makepuzzle));
                }
            }
        }
//        else if (requestCode == WRITE_REQUEST_CODE && resultCode == RESULT_OK) {
//            Uri uri = data.getData();
//            addText(uri);
//            try {
//
//                putString("<puzzle>\n");
//                putString("\t<p_id></p_id>\n");
//                putString("\t<p_width>" + p_width + "</p_width>\n");
//                putString("\t<p_height>" + p_height + "</p_height>\n");
//                putString("\t<l_width>" + l_width + "</l_width>\n");
//                putString("\t<l_height>" + l_height + "</l_height>\n");
//
//                Bitmap srcBitmap = levelCreator.getSrcBitmap();
//
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                srcBitmap.compress(Bitmap.CompressFormat.PNG, 0 , baos); //bmp is the bitmap object
//                byte[] byteArray = baos.toByteArray();
//                String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
//
//                putString("\t<colorset>" + encodedImage + "</colorset>\n");
//
//
//                for(int i = 0; i < p_height * p_width; i++)
//                {
//                    putString("\t<level>\n");
//                    putString("\t\t<number>" + i + "</number>\n");
//                    putString("\t\t<width>" + l_width + "</width>\n");
//                    putString("\t\t<height>" + l_height + "</height>\n");
//
//
//                    byte[] dataBlob = levelCreator.getDataBlobs()[i];
//                    String dataStr = "";
//
//                    for (byte b : dataBlob) {
//                        dataStr += b;
//                    }
//
//                    putString("\t\t<dataset>" + dataStr + "</dataset>\n");
//
//
//                    byte[] colorBlob = levelCreator.getColorBlobs()[i];
//
//                    srcBitmap = BitmapFactory.decodeByteArray(colorBlob, 0, colorBlob.length);
//
//                    baos = new ByteArrayOutputStream();
//                    srcBitmap.compress(Bitmap.CompressFormat.PNG, 0 , baos); //bmp is the bitmap object
//                    byteArray = baos.toByteArray();
//                    encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
//
//
//                    putString("\t\t<colorset>" + encodedImage + "</colorset>\n");
//                    putString("\t</level>\n");
//                }
//
//                putString("</puzzle>\n");
//
//                finishRecord();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    //////////////////////////////레벨 추가 xml 제작할때 쓸 코드
//    public void saveDataFile()
//    {
//        try {
//
//            /**
//             * SAF 파일 편집
//             * */
//            String fileName = "test.txt";
//
//            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            intent.setType("text/plain" );
//            intent.putExtra(Intent.EXTRA_TITLE,fileName);
//
//            startActivityForResult(intent, WRITE_REQUEST_CODE);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private ParcelFileDescriptor pfd;
//    private FileOutputStream fileOutputStream;
//
//    public void addText(Uri uri){
//        try {
//            pfd = ctx.getContentResolver().openFileDescriptor(uri, "w");
//            fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void putString(String st) throws IOException {
//        if(fileOutputStream!=null) fileOutputStream.write(st.getBytes());
//    }
//
//    public void finishRecord() throws IOException {
//        Toast.makeText(ctx, "저장되었습니다.", Toast.LENGTH_LONG).show();
//        fileOutputStream.close();
//        pfd.close();
//    }
}