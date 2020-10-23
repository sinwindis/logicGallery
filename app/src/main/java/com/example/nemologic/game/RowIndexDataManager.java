package com.example.nemologic.game;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;
import com.example.nemologic.data.LevelPlayManager;

import java.util.Arrays;
import java.util.logging.Level;

public class RowIndexDataManager {

    private LevelPlayManager lpm;
    private int[][] idxDataSet;
    private int[] idxNumSet;

    boolean[] isIdxComplete;

    public RowIndexDataManager(LevelPlayManager lpm)
    {
        this.lpm = lpm;
        isIdxComplete = new boolean[lpm.height];
        for(int i = 0; i < lpm.height; i++)
        {
            isIdxComplete[i] = false;
        }
    }

    public void makeIdxDataSet()
    {
        int maxIdxNum = lpm.width/2 + 1;
        idxDataSet = new int[lpm.height][maxIdxNum];
        idxNumSet = new int[lpm.height];
        int sumTemp;
        int idxNum;

        for(int y = 0; y < lpm.height; y++)
        {
            sumTemp = 0;
            idxNum = 0;
            idxDataSet[y][0] = 0;
            for(int x = 0; x < lpm.width; x++)
            {
                if(lpm.dataSet[x + y*lpm.width] == 1)
                {
                    //dataSet 의 값이 1이라면
                    //개수를 세어준다.
                    sumTemp++;
                }
                else if(sumTemp != 0)
                {
                    //dataSet 의 값이 1이 아니고 이제까지 센 1의 개수가 존재하면
                    //인덱스 셋에 이어진 칸의 수를 저장해준다.
                    idxDataSet[y][idxNum] = sumTemp;
                    idxNum++;
                    sumTemp = 0;
                }
            }

            if(sumTemp != 0)
            {
                //마지막 칸까지 다 셌는데 sumTemp 에 값이 남아있다면
                //해당 값을 인덱스셋에 저장해준다.
                idxDataSet[y][idxNum] = sumTemp;
                idxNum++;
            }
            idxNumSet[y] = idxNum;
        }
    }

    public boolean[] getIdxMatch(int rowNum)
    {
        isIdxComplete[rowNum] = false;
        int idxNum = idxNumSet[rowNum];

        boolean[] idxMatch = new boolean[idxNum];

        for(int i = 0; i < idxNum; i++)
        {
            idxMatch[i] = false;
        }

        boolean forOut;

        int sumTemp = 0;
        int checkIdx = 0;

        if(idxDataSet[rowNum][0] == 0)
        {
            //해당 줄의 첫 인덱스가 0이라면 (체크해야 할 칸이 없다면)
            //해당 줄의 인덱스 매치 배열을 완성시키고 반환한다.

            idxMatch = new boolean[1];
            idxMatch[0] = true;
            isIdxComplete[rowNum] = true;
            return idxMatch;
        }

        //해당 가로줄의 맨 처음부터 끝까지 순서대로 확인
        forOut = false;
        for(int i = 0; i < lpm.width; i++)
        {
            int targetIdx = i + rowNum * lpm.width;
            switch (lpm.checkedSet[targetIdx])
            {
                case 0:
                    //만약 공백이 나오면
                    //개수가 맞는지 체크하고 맞건 안맞건 반복문을 끝낸다.
                    if(sumTemp == idxDataSet[rowNum][checkIdx])
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
                    if(i == lpm.width-1)
                    {
                        //개수가 맞는지 체크하고 맞건 안맞건 반복문을 끝낸다.
                        if(sumTemp == idxDataSet[rowNum][checkIdx])
                        {
                            idxMatch[checkIdx] = true;
                            checkIdx++;
                        }
                    }
                    break;
                case 2:
                    //x 표시일 경우 개수를 리셋해준다
                    //개수가 맞는지 체크하고 안맞으면 반복문을 끝낸다.
                    if(sumTemp == idxDataSet[rowNum][checkIdx])
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
                for(int j = i + 1; j < lpm.width; j++)
                {
                    int tempIdx = j + rowNum * lpm.width;
                    if(lpm.checkedSet[tempIdx] == 1)
                    {
                        //체크된 부분이 있다면
                        //모든 idxMatch 를 false 해주고 리턴한다.
                        Arrays.fill(idxMatch, false);
                        isIdxComplete[rowNum] = false;
                        return idxMatch;
                    }

                }
                //체크된 부분이 없었다면
                //인덱스가 완료되었다 보고 complete 시켜준다.
                //모든 idxMatch 를 true 해주고 리턴한다.
                Arrays.fill(idxMatch, true);
                isIdxComplete[rowNum] = true;
                return idxMatch;
            }

            if(forOut)
                break;
        }

        //끝부터 확인
        sumTemp = 0;
        checkIdx = idxNum - 1;
        forOut = false;

        for(int i = lpm.width - 1; i >= 0; i--)
        {
            int targetIdx = i + rowNum * lpm.width;
            switch (lpm.checkedSet[targetIdx])
            {
                case 0:
                    //만약 공백이 나오면
                    //개수가 맞는지 체크하고 맞건 안맞건 반복문을 끝낸다.
                    if(sumTemp == idxDataSet[rowNum][checkIdx])
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
                        //개수가 맞는지 체크하고 맞건 안맞건 반복문을 끝낸다.
                        if(sumTemp == idxDataSet[rowNum][checkIdx])
                        {
                            idxMatch[checkIdx] = true;
                            checkIdx--;
                        }
                    }

                    break;
                case 2:
                    //x 표시일 경우 개수를 리셋해준다
                    //개수가 맞는지 체크하고 안맞으면 반복문을 끝낸다.
                    if(sumTemp == idxDataSet[rowNum][checkIdx])
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
                    int tempIdx = j + rowNum * lpm.width;
                    if(lpm.checkedSet[tempIdx] == 1)
                    {
                        //체크된 부분이 있다면
                        //모든 idxMatch 를 false 해주고 리턴한다.
                        Arrays.fill(idxMatch, false);
                        isIdxComplete[rowNum] = false;
                        return idxMatch;
                    }

                }
                //체크된 부분이 없었다면
                //인덱스가 완료되었다 보고 complete 시켜준다.
                //모든 idxMatch 를 true 해주고 리턴한다.
                Arrays.fill(idxMatch, true);
                isIdxComplete[rowNum] = true;
                return idxMatch;
            }

            if(forOut)
                break;
        }

        //모든 숫자가 채워진 상태인지 확인
        checkIdx = 0;
        sumTemp = 0;

        for(int i = 0; i < lpm.width; i++)
        {
            if(lpm.checkedSet[i + rowNum * lpm.width] == 1)
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

            if(sumTemp == idxDataSet[rowNum][checkIdx])
            {
                //sumTemp의 숫자가 dataSet에 저장된 숫자와 동일할 때
                if(i == lpm.width - 1 || lpm.checkedSet[(i+1) + rowNum * lpm.width] != 1)
                {
                    //마지막 칸이거나 다음칸에 체크가 안 되어 있을 때
                    sumTemp = 0;
                    checkIdx++;
                }

            }

            if(checkIdx == idxNumSet[rowNum])
            {
                //모든 idx가 맞춰졌을 때
                //혹시 과잉 체크된 칸이 없는지 확인
                for(int j = i+1; j < lpm.width; j++)
                {
                    if(lpm.checkedSet[j + rowNum * lpm.width] == 1)
                    {
                        Arrays.fill(idxMatch, false);
                        isIdxComplete[rowNum] = false;
                        return idxMatch;
                    }
                }
                isIdxComplete[rowNum] = true;
                Arrays.fill(idxMatch, true);
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
