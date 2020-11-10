package com.example.nemologic.data;

import java.util.Arrays;

public class TutorialScene {

    public byte[][] checkedSet;

    //0: row0, 1: row1, 2: row2, 3: column0, 4: column1, 5: column2, 6: toggle, 7: prev, 8: next, 9: hint
    public boolean[] accentArray;

    //0: O, 1: X, 2: hint
    public int btnToggleStatus;

    public String tutorialText;

    public int tutorialCurrentStackNum;
    public int tutorialMaxStackNum;

    public int tutorialHintNum;

    public TutorialScene()
    {
        checkedSet = new byte[3][3];
        accentArray = new boolean[10];
        Arrays.fill(accentArray, false);
        btnToggleStatus = 0;
        tutorialCurrentStackNum = 0;
        tutorialMaxStackNum = 0;
        tutorialHintNum = 1;
        tutorialText = "";
    }
}
