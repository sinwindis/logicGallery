package com.sinwindis.logicgallery.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sinwindis.logicgallery.R;
import com.sinwindis.logicgallery.data.TutorialScene;
import com.sinwindis.logicgallery.tutorial.GameBoardBorder;
import com.sinwindis.logicgallery.tutorial.RvTutorialBoardAdapter;

public class TutorialDialog {

    public AlertDialog dialog;

    private TextView tv_tutorial;

    Button btn_continue;

    private Resources res;

    private GridLayoutManager glm;

    private ImageView[] iv_borders;

    private ImageView iv_toggle;

    private TextView[] tv_row;
    private TextView[] tv_column;
    private TextView tv_stack;
    private TextView tv_hint;

    int tutorialCount;
    final int MAX_TUTORIAL = 12;

    private TutorialScene[] tutorialScenes;

    public TutorialDialog() {

    }

    private void showTutorialScene(int index) {
        TutorialScene targetScene = tutorialScenes[index];

        setIndexNumber(targetScene.checkedSet);
        updateBoardView(targetScene.checkedSet);
        setIndexAccent(targetScene.accentArray);
        setToggleButton(targetScene.btnToggleStatus);
        setStackNum(targetScene.tutorialCurrentStackNum, targetScene.tutorialMaxStackNum);
        setHintNum(targetScene.tutorialHintNum);
        tv_tutorial.setText(targetScene.tutorialText);
    }

    private void setToggleButton(int type) {
        switch (type) {
            case 0:
                iv_toggle.setImageResource(R.drawable.ic_o);
                break;
            case 1:
                iv_toggle.setImageResource(R.drawable.ic_x);
                break;
            case 2:
                iv_toggle.setImageResource(R.drawable.ic_hint);
                break;
        }

    }

    @SuppressLint("SetTextI18n")
    private void setStackNum(int currentStackNum, int maxStackNum) {
        tv_stack.setText(currentStackNum + "/" + maxStackNum);
    }

    private void setHintNum(int hintNum) {
        tv_hint.setText(String.valueOf(hintNum));
    }

    private void setIndexNumber(byte[][] checkedSet) {
        //row0 check
        if (checkedSet[0][0] != 1 && checkedSet[0][1] == 1 && checkedSet[0][2] == 1) {
            //row0 index number 색상을 맞춰준다.
            SpannableStringBuilder strTemp;
            strTemp = new SpannableStringBuilder("2");
            strTemp.setSpan(new ForegroundColorSpan(Color.parseColor("#a0a0a0")), 0, strTemp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            tv_row[0].setText(null);
            tv_row[0].append(strTemp);
        } else {
            tv_row[0].setText("2");
        }

        //row1 check
        if (checkedSet[1][0] != 1 && checkedSet[1][1] != 1 && checkedSet[1][2] == 1) {
            //row0 index number 색상을 맞춰준다.
            SpannableStringBuilder strTemp;
            strTemp = new SpannableStringBuilder("1");
            strTemp.setSpan(new ForegroundColorSpan(Color.parseColor("#a0a0a0")), 0, strTemp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            tv_row[1].setText(null);
            tv_row[1].append(strTemp);
        } else {
            tv_row[1].setText("1");
        }

        //row2 check
        if (checkedSet[2][0] != 1 && checkedSet[2][1] == 1 && checkedSet[2][2] == 1) {
            //row0 index number 색상을 맞춰준다.
            SpannableStringBuilder strTemp;
            strTemp = new SpannableStringBuilder("2");
            strTemp.setSpan(new ForegroundColorSpan(Color.parseColor("#a0a0a0")), 0, strTemp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            tv_row[2].setText(null);
            tv_row[2].append(strTemp);
        } else {
            tv_row[2].setText("2");
        }

        //column0 check
        if (checkedSet[0][0] != 1 && checkedSet[1][0] != 1 && checkedSet[2][0] != 1) {
            //row0 index number 색상을 맞춰준다.
            SpannableStringBuilder strTemp;
            strTemp = new SpannableStringBuilder("0");
            strTemp.setSpan(new ForegroundColorSpan(Color.parseColor("#a0a0a0")), 0, strTemp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            tv_column[0].setText(null);
            tv_column[0].append(strTemp);
        } else {
            tv_column[0].setText("0");
        }

        //column1 check
        if (checkedSet[0][1] == 1) {
            //row0 index number 색상을 맞춰준다.
            SpannableStringBuilder strTemp;
            strTemp = new SpannableStringBuilder("1\n");
            strTemp.setSpan(new ForegroundColorSpan(Color.parseColor("#a0a0a0")), 0, strTemp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            tv_column[1].setText(null);
            tv_column[1].append(strTemp);
        } else {
            tv_column[1].setText("1\n");
        }
        if (checkedSet[2][1] == 1) {
            //row0 index number 색상을 맞춰준다.
            SpannableStringBuilder strTemp;
            strTemp = new SpannableStringBuilder("1");
            strTemp.setSpan(new ForegroundColorSpan(Color.parseColor("#a0a0a0")), 0, strTemp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            tv_column[1].setText(null);
            tv_column[1].append(strTemp);
        } else {
            tv_column[1].append("1");
        }

        //column2 check
        if (checkedSet[0][2] == 1 && checkedSet[1][2] == 1 && checkedSet[2][2] == 1) {
            //row0 index number 색상을 맞춰준다.
            SpannableStringBuilder strTemp;
            strTemp = new SpannableStringBuilder("3");
            strTemp.setSpan(new ForegroundColorSpan(Color.parseColor("#a0a0a0")), 0, strTemp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            tv_column[2].setText(null);
            tv_column[2].append(strTemp);
        } else {
            tv_column[2].setText("3");
        }
    }

    private void setIndexAccent(boolean[] accentCode) {
        for (int i = 0; i < accentCode.length; i++) {
            if (accentCode[i]) {
                iv_borders[i].setAlpha(1F);
            } else {
                iv_borders[i].setAlpha(0F);
            }
        }
    }

    public void makeDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_tutorial, null);
        builder.setView(view);

        tutorialScenes = new TutorialScene[MAX_TUTORIAL];
        for (int i = 0; i < MAX_TUTORIAL; i++) {
            tutorialScenes[i] = new TutorialScene();
        }
        res = view.getResources();
        setTutorialScenes();

        tutorialCount = 0;

        RecyclerView rv_board = view.findViewById(R.id.rv_board);
        btn_continue = view.findViewById(R.id.btn_continue);

        iv_borders = new ImageView[10];

        iv_borders[0] = view.findViewById(R.id.border_row0);
        iv_borders[1] = view.findViewById(R.id.border_row1);
        iv_borders[2] = view.findViewById(R.id.border_row2);
        iv_borders[3] = view.findViewById(R.id.border_column0);
        iv_borders[4] = view.findViewById(R.id.border_column1);
        iv_borders[5] = view.findViewById(R.id.border_column2);
        iv_borders[6] = view.findViewById(R.id.border_toggle);
        iv_borders[7] = view.findViewById(R.id.border_prev);
        iv_borders[8] = view.findViewById(R.id.border_next);
        iv_borders[9] = view.findViewById(R.id.border_hint);


        iv_toggle = view.findViewById(R.id.img_button_o);

        tv_stack = view.findViewById(R.id.tv_stack);
        tv_hint = view.findViewById(R.id.tv_hint);
        tv_tutorial = view.findViewById(R.id.tv_tutorial);

        rv_board.addItemDecoration(new GameBoardBorder(view.getContext(), R.drawable.border_gameboard_normal, 3));
        glm = new GridLayoutManager(view.getContext(), 3);
        rv_board.setLayoutManager(glm);
        rv_board.setAdapter(new RvTutorialBoardAdapter(3, 3));

        tv_row = new TextView[3];
        tv_column = new TextView[3];

        tv_row[0] = view.findViewById(R.id.row_0);
        tv_row[1] = view.findViewById(R.id.row_1);
        tv_row[2] = view.findViewById(R.id.row_2);

        tv_column[0] = view.findViewById(R.id.column_0);
        tv_column[1] = view.findViewById(R.id.column_1);
        tv_column[2] = view.findViewById(R.id.column_2);

        rv_board.post(new Runnable() {
            @Override
            public void run() {
                showTutorialScene(0);
            }
        });

        Button btn_prev = view.findViewById(R.id.btn_prev);
        Button btn_next = view.findViewById(R.id.btn_next);

        btn_prev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showPrevTutorial();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showNextTutorial();
            }
        });

        dialog = builder.create();
    }

    private void setTutorialScenes() {
        TutorialScene targetScene;

        //tutorial0
        targetScene = tutorialScenes[0];
        targetScene.tutorialText = res.getString(R.string.tutorial_0);

        //tutorial1
        targetScene = tutorialScenes[1];
        targetScene.tutorialText = res.getString(R.string.tutorial_1);
        targetScene.accentArray[5] = true;

        //tutorial2
        targetScene = tutorialScenes[2];
        targetScene.tutorialText = res.getString(R.string.tutorial_2);
        targetScene.checkedSet = new byte[][]{{0, 0, 1}, {0, 0, 1}, {0, 0, 1}};
        targetScene.accentArray[1] = true;
        targetScene.accentArray[3] = true;
        targetScene.accentArray[5] = true;
        targetScene.tutorialCurrentStackNum = 1;
        targetScene.tutorialMaxStackNum = 1;

        //tutorial3
        targetScene = tutorialScenes[3];
        targetScene.tutorialText = res.getString(R.string.tutorial_3);
        targetScene.checkedSet = new byte[][]{{0, 0, 1}, {0, 0, 1}, {0, 0, 1}};
        targetScene.accentArray[0] = true;

        //tutorial4
        targetScene = tutorialScenes[4];
        targetScene.tutorialText = res.getString(R.string.tutorial_4);
        targetScene.checkedSet = new byte[][]{{0, 1, 1}, {0, 0, 1}, {0, 0, 1}};
        targetScene.tutorialCurrentStackNum = 2;
        targetScene.tutorialMaxStackNum = 2;

        //tutorial5
        targetScene = tutorialScenes[5];
        targetScene.tutorialText = res.getString(R.string.tutorial_5);
        targetScene.checkedSet = new byte[][]{{0, 1, 1}, {0, 0, 1}, {0, 0, 1}};
        targetScene.accentArray[4] = true;
        targetScene.accentArray[6] = true;
        targetScene.btnToggleStatus = 1;

        //tutorial6
        targetScene = tutorialScenes[6];
        targetScene.tutorialText = res.getString(R.string.tutorial_6);
        targetScene.checkedSet = new byte[][]{{0, 1, 1}, {0, 2, 1}, {0, 0, 1}};
        targetScene.btnToggleStatus = 1;
        targetScene.tutorialCurrentStackNum = 3;
        targetScene.tutorialMaxStackNum = 3;

        //tutorial7
        targetScene = tutorialScenes[7];
        targetScene.tutorialText = res.getString(R.string.tutorial_7);
        targetScene.checkedSet = new byte[][]{{0, 1, 1}, {0, 2, 1}, {0, 2, 1}};
        targetScene.btnToggleStatus = 1;
        targetScene.accentArray[7] = true;
        targetScene.tutorialCurrentStackNum = 4;
        targetScene.tutorialMaxStackNum = 4;

        //tutorial8
        targetScene = tutorialScenes[8];
        targetScene.tutorialText = res.getString(R.string.tutorial_8);
        targetScene.checkedSet = new byte[][]{{0, 1, 1}, {0, 2, 1}, {0, 0, 1}};
        targetScene.btnToggleStatus = 1;
        targetScene.accentArray[7] = true;
        targetScene.accentArray[8] = true;
        targetScene.tutorialCurrentStackNum = 3;
        targetScene.tutorialMaxStackNum = 4;

        //tutorial9
        targetScene = tutorialScenes[9];
        targetScene.tutorialText = res.getString(R.string.tutorial_9);
        targetScene.checkedSet = new byte[][]{{0, 1, 1}, {0, 2, 1}, {0, 0, 1}};
        targetScene.btnToggleStatus = 2;
        targetScene.accentArray[6] = true;
        targetScene.accentArray[9] = true;
        targetScene.tutorialCurrentStackNum = 3;
        targetScene.tutorialMaxStackNum = 4;

        //tutorial10
        targetScene = tutorialScenes[10];
        targetScene.tutorialText = res.getString(R.string.tutorial_10);
        targetScene.checkedSet = new byte[][]{{0, 1, 1}, {0, 2, 1}, {0, 1, 1}};
        targetScene.btnToggleStatus = 2;
        targetScene.tutorialCurrentStackNum = 4;
        targetScene.tutorialMaxStackNum = 4;
        targetScene.tutorialHintNum = 0;

        //tutorial11
        targetScene = tutorialScenes[11];
        targetScene.tutorialText = res.getString(R.string.tutorial_11);
        targetScene.checkedSet = new byte[][]{{2, 1, 1}, {2, 2, 1}, {2, 1, 1}};
        targetScene.btnToggleStatus = 0;
        targetScene.tutorialCurrentStackNum = 4;
        targetScene.tutorialMaxStackNum = 4;
        targetScene.tutorialHintNum = 0;
    }

    private void updateBoardView(byte[][] dataSet) {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                ImageView view = (ImageView) glm.findViewByPosition(y * 3 + x);
                assert view != null;
                switch (dataSet[y][x]) {
                    case 0:
                        view.setImageDrawable(null);
                        view.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        break;
                    case 1:
                        view.setImageDrawable(null);
                        view.setBackgroundColor(Color.parseColor("#000000"));
                        break;
                    case 2:
                        view.setImageResource(R.drawable.background_x);
                        view.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        break;
                }
            }
        }
    }

    public void showNextTutorial() {
        if (tutorialCount < MAX_TUTORIAL - 1) {
            tutorialCount++;
            showTutorialScene(tutorialCount);
        } else {
            dialog.dismiss();
        }
    }

    public void showPrevTutorial() {
        if (tutorialCount > 0) {
            tutorialCount--;
            showTutorialScene(tutorialCount);
        }
    }
}