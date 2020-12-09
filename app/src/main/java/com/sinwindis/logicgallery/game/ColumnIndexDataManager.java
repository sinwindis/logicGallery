package com.sinwindis.logicgallery.game;

import android.util.Log;

import java.util.Arrays;

public class ColumnIndexDataManager {

    Board board;
    int[][] idxDataSet;
    int[] idxNumSet;

    boolean[] isIdxComplete;

    public ColumnIndexDataManager(Board board) {
        this.board = board;
        isIdxComplete = new boolean[board.getWidth()];
        for (int i = 0; i < board.getWidth(); i++) {
            isIdxComplete[i] = false;
        }
    }

    public void makeIdxDataSet() {

        int maxIdxNum = board.getHeight() / 2 + 1;
        idxDataSet = new int[board.getWidth()][maxIdxNum];
        idxNumSet = new int[board.getWidth()];
        int sumTemp;
        int idxNum;

        for (int x = 0; x < board.getWidth(); x++) {
            sumTemp = 0;
            idxNum = 0;
            idxDataSet[x][0] = 0;
            for (int y = 0; y < board.getHeight(); y++) {
                if (board.getCell(y, x).getCorrectValue() == 1) {
                    //dataSet 의 값이 1이라면
                    //개수를 세어준다.
                    sumTemp++;
                } else if (sumTemp != 0) {
                    //dataSet 의 값이 1이 아니고 이제까지 센 1의 개수가 존재하면
                    //인덱스 셋에 이어진 칸의 수를 저장해준다.
                    idxDataSet[x][idxNum] = sumTemp;
                    idxNum++;
                    sumTemp = 0;
                }
            }

            if (sumTemp != 0) {
                //마지막 칸까지 다 셌는데 sumTemp 에 값이 남아있다면
                //해당 값을 인덱스셋에 저장해준다.
                idxDataSet[x][idxNum] = sumTemp;
                idxNum++;
            }
            idxNumSet[x] = idxNum;
        }
    }

    public boolean[] getIdxMatch(int columnNum) {
        isIdxComplete[columnNum] = false;
        int idxNum = idxNumSet[columnNum];

        boolean[] idxMatch = new boolean[idxNum];

        Arrays.fill(idxMatch, false);

        int sumTemp = 0;
        int checkIdx = 0;
        int lastCheckIdx = 0;
        boolean forOut;

        if (idxDataSet[columnNum][0] == 0) {
            //해당 줄의 첫 인덱스가 0이라면 (체크해야 할 칸이 없다면)
            //해당 줄의 인덱스 매치 배열을 완성시키고 반환한다.

            idxMatch = new boolean[1];
            idxMatch[0] = true;
            isIdxComplete[columnNum] = true;
            return idxMatch;
        }

        forOut = false;
        //해당 세로줄의 맨 처음부터 끝까지 순서대로 확인
        for (int i = 0; i < board.getHeight(); i++) {
            lastCheckIdx = i;
            switch (board.getCell(i, columnNum).getCurrentValue()) {
                case 0:
                    //만약 공백이 나오면
                    //개수가 맞는지 체크하고 맞건 안맞건 반복문을 끝낸다.
                    if (sumTemp == idxDataSet[columnNum][checkIdx]) {
                        idxMatch[checkIdx] = true;
                        checkIdx++;
                    }
                    forOut = true;
                    sumTemp = 0;
                    break;
                case 1:
                    //체크되어있을 경우 개수를 센다
                    sumTemp++;
                    //마지막 칸이라면 방금까지 센 sumTemp 의 개수를 마지막으로 확인해 준다.
                    if (i == board.getHeight() - 1) {
                        //개수가 맞는지 체크한다.
                        if (sumTemp == idxDataSet[columnNum][checkIdx]) {
                            idxMatch[checkIdx] = true;
                            checkIdx++;
                        }
                    }
                    break;
                case 2:
                    //x 표시일 경우 개수를 리셋해준다
                    //개수가 맞는지 체크하고 안맞으면 반복문을 끝낸다.
                    if (sumTemp == idxDataSet[columnNum][checkIdx]) {
                        idxMatch[checkIdx] = true;
                        checkIdx++;
                    } else if (sumTemp != 0) {
                        forOut = true;
                    }
                    sumTemp = 0;
                    break;
            }

            //모든 인덱스를 다 찾았으면
            //그 이후 체크된 부분이 없는지 확인하고
            //반복문을 끝낸다.
            if (checkIdx == idxNum) {
                for (int j = i + 1; j < board.getHeight(); j++) {
                    if (board.getCell(j, columnNum).getCurrentValue() == 1) {
                        //체크된 부분이 있다면
                        //모든 idxMatch 를 false 해주고 리턴한다.
                        Arrays.fill(idxMatch, false);
                        isIdxComplete[columnNum] = false;
                        return idxMatch;
                    }

                }
                //체크된 부분이 없었다면
                //인덱스가 완료되었다 보고 complete 시켜준다.
                isIdxComplete[columnNum] = true;
                return idxMatch;
            }

            if (forOut)
                break;
        }

        //끝부터 확인
        sumTemp = 0;
        checkIdx = idxNum - 1;
        forOut = false;

        for (int i = board.getHeight() - 1; i > lastCheckIdx; i--) {
            switch (board.getCell(i, columnNum).getCurrentValue()) {
                case 0:
                    //만약 공백이 나오면
                    //개수가 맞는지 체크하고 맞건 안맞건 반복문을 끝낸다.
                    if (sumTemp == idxDataSet[columnNum][checkIdx]) {
                        idxMatch[checkIdx] = true;
                        checkIdx--;
                    }
                    forOut = true;
                    sumTemp = 0;
                    break;
                case 1:
                    //체크되어있을 경우 개수를 센다
                    sumTemp++;
                    break;
                case 2:
                    //x 표시일 경우 개수를 리셋해준다
                    //개수가 맞는지 체크하고 안맞으면 반복문을 끝낸다.
                    if (sumTemp == idxDataSet[columnNum][checkIdx]) {
                        idxMatch[checkIdx] = true;
                        checkIdx--;
                    } else if (sumTemp != 0) {
                        forOut = true;
                    }
                    sumTemp = 0;
                    break;
            }

            //모든 인덱스를 다 찾았으면
            //그 이후 체크된 부분이 없는지 확인하고
            //반복문을 끝낸다.
            if (checkIdx == -1) {
                for (int j = i - 1; j >= 0; j--) {
                    int tempIdx = j * board.getWidth() + columnNum;
                    if (board.getCell(j, columnNum).getCurrentValue() == 1) {
                        //체크된 부분이 있다면
                        //모든 idxMatch 를 false 해주고 리턴한다.
                        Arrays.fill(idxMatch, false);
                        isIdxComplete[columnNum] = false;
                        return idxMatch;
                    }

                }
                //체크된 부분이 없었다면
                //인덱스가 완료되었다 보고 complete 시켜준다.
                //모든 idxMatch 를 true 해주고 리턴한다.
                Arrays.fill(idxMatch, true);
                isIdxComplete[columnNum] = true;
                return idxMatch;
            }

            if (forOut)
                break;
        }

        //모든 숫자가 채워진 상태인지 확인
        checkIdx = 0;
        sumTemp = 0;

        for (int i = 0; i < board.getHeight(); i++) {
            if (board.getCell(i, columnNum).getCurrentValue() == 1) {
                //해당 칸이 채워져있을때
                sumTemp++;
            } else {
                //해당 칸이 채워져있지 않을 때
                //센 개수를 리셋해준다
                sumTemp = 0;
            }

            if (sumTemp == idxDataSet[columnNum][checkIdx]) {
                //sumTemp의 숫자가 dataSet에 저장된 숫자와 동일할 때
                if (i == board.getHeight() - 1 || board.getCell(i + 1, columnNum).getCurrentValue() != 1) {
                    //마지막 칸이거나 다음칸에 체크가 안 되어 있을 때
                    sumTemp = 0;
                    checkIdx++;
                }

            }

            if (checkIdx == idxNumSet[columnNum]) {
                //모든 idx가 맞춰졌을 때
                //혹시 과잉 체크된 칸이 없는지 확인
                for (int j = i + 1; j < board.getHeight(); j++) {
                    if (board.getCell(j, columnNum).getCurrentValue() == 1) {
                        Arrays.fill(idxMatch, false);
                        isIdxComplete[columnNum] = false;
                        return idxMatch;
                    }
                }
                isIdxComplete[columnNum] = true;
                Arrays.fill(idxMatch, true);
                return idxMatch;
            }
        }
        return idxMatch;
    }

    public int[] getIdxNumSet() {
        return idxNumSet;
    }

    public int[][] getIdxDataSet() {
        return idxDataSet;
    }
}
