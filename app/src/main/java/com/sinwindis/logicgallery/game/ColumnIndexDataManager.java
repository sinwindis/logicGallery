package com.sinwindis.logicgallery.game;

import android.util.Log;

import com.sinwindis.logicgallery.data.LevelPlayManager;

import java.util.Arrays;

public class ColumnIndexDataManager {
    
    LevelPlayManager lpm;
    int[][] idxDataSet;
    int[] idxNumSet;

    boolean[] isIdxComplete;
    
    public ColumnIndexDataManager(LevelPlayManager lpm)
    {
        this.lpm = lpm;
        isIdxComplete = new boolean[lpm.width];
        for(int i = 0; i < lpm.width; i++)
        {
            isIdxComplete[i] = false;
        }
    }

    public void makeIdxDataSet()
    {
        int maxIdxNum = lpm.height/2 + 1;
        idxDataSet = new int[lpm.width][maxIdxNum];
        idxNumSet = new int[lpm.width];
        int sumTemp;
        int idxNum;

        for(int x = 0; x < lpm.width; x++)
        {
            sumTemp = 0;
            idxNum = 0;
            idxDataSet[x][0] = 0;
            for(int y = 0; y < lpm.height; y++)
            {
                if(lpm.dataSet[y*lpm.width + x] == 1)
                {
                    //dataSet 의 값이 1이라면
                    //개수를 세어준다.
                    sumTemp++;
                }
                else if(sumTemp != 0)
                {
                    //dataSet 의 값이 1이 아니고 이제까지 센 1의 개수가 존재하면
                    //인덱스 셋에 이어진 칸의 수를 저장해준다.
                    idxDataSet[x][idxNum] = sumTemp;
                    idxNum++;
                    sumTemp = 0;
                }
            }

            if(sumTemp != 0)
            {
                //마지막 칸까지 다 셌는데 sumTemp 에 값이 남아있다면
                //해당 값을 인덱스셋에 저장해준다.
                idxDataSet[x][idxNum] = sumTemp;
                idxNum++;
            }
            idxNumSet[x] = idxNum;
        }
    }

    public boolean[] getIdxMatch(int columnNum)
    {
        Log.d("ColumnIdx", "column " + columnNum);
        isIdxComplete[columnNum] = false;
        int idxNum = idxNumSet[columnNum];

        boolean[] idxMatch = new boolean[idxNum];

        Arrays.fill(idxMatch, false);

        int sumTemp = 0;
        int checkIdx = 0;
        int lastCheckIdx = 0;
        boolean forOut;

        if(idxDataSet[columnNum][0] == 0)
        {
            //해당 줄의 첫 인덱스가 0이라면 (체크해야 할 칸이 없다면)
            //해당 줄의 인덱스 매치 배열을 완성시키고 반환한다.

            idxMatch = new boolean[1];
            idxMatch[0] = true;
            isIdxComplete[columnNum] = true;
            Log.d("ColumnIdx", "null idx");
            return idxMatch;
        }

        forOut = false;
        //해당 세로줄의 맨 처음부터 끝까지 순서대로 확인
        for(int i = 0; i < lpm.height; i++)
        {
            lastCheckIdx = i;
            int targetIdx = i * lpm.width + columnNum;
            switch (lpm.checkedSet[targetIdx])
            {
                case 0:
                    //만약 공백이 나오면
                    //개수가 맞는지 체크하고 맞건 안맞건 반복문을 끝낸다.
                    if(sumTemp == idxDataSet[columnNum][checkIdx])
                    {
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
                    if(i == lpm.height-1)
                    {
                        //개수가 맞는지 체크한다.
                        if(sumTemp == idxDataSet[columnNum][checkIdx])
                        {
                            idxMatch[checkIdx] = true;
                            checkIdx++;
                        }
                    }
                    break;
                case 2:
                    //x 표시일 경우 개수를 리셋해준다
                    //개수가 맞는지 체크하고 안맞으면 반복문을 끝낸다.
                    if(sumTemp == idxDataSet[columnNum][checkIdx])
                    {
                        idxMatch[checkIdx] = true;
                        checkIdx++;
                    }
                    else if(sumTemp != 0)
                    {
                        forOut = true;
                    }
                    sumTemp = 0;
                    break;
            }

            //모든 인덱스를 다 찾았으면
            //그 이후 체크된 부분이 없는지 확인하고
            //반복문을 끝낸다.
            if(checkIdx == idxNum)
            {
                for(int j = i + 1; j < lpm.height; j++)
                {
                    int tempIdx = j * lpm.width + columnNum;
                    if(lpm.checkedSet[tempIdx] == 1)
                    {
                        //체크된 부분이 있다면
                        //모든 idxMatch 를 false 해주고 리턴한다.
                        Arrays.fill(idxMatch, false);
                        isIdxComplete[columnNum] = false;
                        Log.d("ColumnIdx", "over idx");
                        return idxMatch;
                    }

                }
                //체크된 부분이 없었다면
                //인덱스가 완료되었다 보고 complete 시켜준다.
                isIdxComplete[columnNum] = true;
                Log.d("ColumnIdx", "full idx");
                return idxMatch;
            }

            if(forOut)
                break;
        }

        //끝부터 확인
        sumTemp = 0;
        checkIdx = idxNum - 1;
        forOut = false;

        for(int i = lpm.height - 1; i > lastCheckIdx; i--)
        {
            int targetIdx = i * lpm.width + columnNum;
            switch (lpm.checkedSet[targetIdx])
            {
                case 0:
                    //만약 공백이 나오면
                    //개수가 맞는지 체크하고 맞건 안맞건 반복문을 끝낸다.
                    if(sumTemp == idxDataSet[columnNum][checkIdx])
                    {
                        idxMatch[checkIdx] = true;
                        checkIdx--;
                    }
                    forOut = true;
                    sumTemp = 0;
                    break;
                case 1:
                    //체크되어있을 경우 개수를 센다
                    sumTemp++;
                    //첫 칸이라면 방금까지 센 sumTemp 의 개수를 마지막으로 확인해 준다.
                    if(i == 0)
                    {
                        //개수가 맞는지 체크한다.
                        if(sumTemp == idxDataSet[columnNum][checkIdx])
                        {
                            idxMatch[checkIdx] = true;
                            checkIdx--;
                        }
                    }
                    break;
                case 2:
                    //x 표시일 경우 개수를 리셋해준다
                    //개수가 맞는지 체크하고 안맞으면 반복문을 끝낸다.
                    if(sumTemp == idxDataSet[columnNum][checkIdx])
                    {
                        idxMatch[checkIdx] = true;
                        checkIdx--;
                    }
                    else if(sumTemp != 0)
                    {
                        forOut = true;
                    }
                    sumTemp = 0;
                    break;
            }

            //모든 인덱스를 다 찾았으면
            //그 이후 체크된 부분이 없는지 확인하고
            //반복문을 끝낸다.
            if(checkIdx == -1)
            {
                for(int j = i - 1; j >= 0; j--)
                {
                    int tempIdx = j * lpm.width + columnNum;
                    if(lpm.checkedSet[tempIdx] == 1)
                    {
                        //체크된 부분이 있다면
                        //모든 idxMatch 를 false 해주고 리턴한다.
                        Arrays.fill(idxMatch, false);
                        isIdxComplete[columnNum] = false;
                        Log.d("ColumnIdx", "reverse over idx");
                        return idxMatch;
                    }

                }
                //체크된 부분이 없었다면
                //인덱스가 완료되었다 보고 complete 시켜준다.
                //모든 idxMatch 를 true 해주고 리턴한다.
                Arrays.fill(idxMatch, true);
                isIdxComplete[columnNum] = true;
                Log.d("ColumnIdx", "reverse full idx");
                return idxMatch;
            }

            if(forOut)
                break;
        }

        //모든 숫자가 채워진 상태인지 확인
        checkIdx = 0;
        sumTemp = 0;

        for(int i = 0; i < lpm.height; i++)
        {
            if(lpm.checkedSet[i * lpm.width + columnNum] == 1)
            {
                //해당 칸이 채워져있을때
                sumTemp++;
            }
            else
            {
                //해당 칸이 채워져있지 않을 때
                //센 개수를 리셋해준다
                sumTemp = 0;
            }

            if(sumTemp == idxDataSet[columnNum][checkIdx])
            {
                //sumTemp의 숫자가 dataSet에 저장된 숫자와 동일할 때
                if(i == lpm.height - 1 || lpm.checkedSet[(i+1) * lpm.width + columnNum] != 1)
                {
                    //마지막 칸이거나 다음칸에 체크가 안 되어 있을 때
                    sumTemp = 0;
                    checkIdx++;
                }

            }

            if(checkIdx == idxNumSet[columnNum])
            {
                //모든 idx가 맞춰졌을 때
                //혹시 과잉 체크된 칸이 없는지 확인
                for(int j = i+1; j < lpm.height; j++)
                {
                    if(lpm.checkedSet[j * lpm.width + columnNum] == 1)
                    {
                        Arrays.fill(idxMatch, false);
                        isIdxComplete[columnNum] = false;
                        Log.d("ColumnIdx", "overall over idx");
                        return idxMatch;
                    }
                }
                isIdxComplete[columnNum] = true;
                Arrays.fill(idxMatch, true);
                Log.d("ColumnIdx", "overall full idx");
                return idxMatch;
            }
        }
        return idxMatch;
    }

    public int[] getIdxNumSet() {
        return idxNumSet;
    }

    public int[][] getIdxDataSet()
    {
        return idxDataSet;
    }
}
