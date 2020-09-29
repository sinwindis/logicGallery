package com.example.nemologic.game;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RvRowAdapter extends RecyclerView.Adapter<RvRowAdapter.ViewHolder> {

    int[][] dataSet;
    int[] idxNumSet;
    public boolean[] endRow;
    List<TextView> tvList = new ArrayList<>();
    float heightUnder = 0;
    int heightOffset = 0;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView) ;
            // 뷰 객체에 대한 참조. (hold strong reference)
        }
    }

    RvRowAdapter(int[][] dataSet) {
        //생성자
        this.dataSet = dataSet.clone();
        this.idxNumSet = new int[dataSet.length];
        for(int i = 0; i < dataSet.length; i++)
        {
            idxNumSet[i] = 0;
        }
        endRow = new boolean[dataSet.length];

    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @NonNull
    @Override
    public RvRowAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.item_row, parent, false) ;

        heightUnder += parent.getMeasuredHeight()%this.dataSet.length/((float)this.dataSet.length);

        if(heightUnder >= 1)
        {
            heightOffset = 1;
            heightUnder--;
        }

        view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parent.getMeasuredHeight()/this.dataSet.length + heightOffset));

        return new ViewHolder(view);
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(RvRowAdapter.ViewHolder holder, int position) {
        tvList.add((TextView)  holder.itemView.findViewById(R.id.tv_item_row));

        //첫 번째 숫자부터 0 이라면 textview에 표시
        tvList.get(position).append(String.valueOf(dataSet[position][0]));
        idxNumSet[position]++;
        //그 이후는 0이면 표시 skip
        for(int i = 1; i < dataSet[position].length; i++)
        {
            if(dataSet[position][i] != 0)
            {
                tvList.get(position).append(' ' + String.valueOf(dataSet[position][i]));
                idxNumSet[position]++;
            }
            else
                break;
        }

    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return dataSet.length;
    }

    private boolean[] getIdxMatch(int rowNum, int[][] checkedSet)
    {
        endRow[rowNum] = false;
        int idxNum = idxNumSet[rowNum];
        boolean[] checkTemp = new boolean[idxNum];
        int sumTemp = 0;
        int checkIdx = 0;

        if(dataSet[rowNum][0] == 0)
        {
            //해당 row 에 채워야 할 칸이 없다면
            //모든 칸을 채웠다고 반환한다
            checkTemp = new boolean[1];
            checkTemp[0] = true;
            return checkTemp;
        }

        for(int i = 0; i < idxNum; i++)
        {
            checkTemp[i] = false;
        }

        //처음부터 확인
        for(int i = 0; i < checkedSet[rowNum].length; i++)
        {
            if(checkedSet[rowNum][i] == 0)
            {
                //만약 공백이 나오면 확인 반복문 종료
                break;
            }
            else if(checkedSet[rowNum][i] == 1)
            {
                //체크되어있을 경우 개수를 센다
                sumTemp++;
            }
            else if(checkedSet[rowNum][i] == 2)
            {
                //x 표시일 경우 센 개수를 리셋해준다
                sumTemp = 0;
            }

            if(sumTemp == dataSet[rowNum][checkIdx])
            {
                //이번 칸까지 체크를 셌는데 개수가 맞다면

                if(i == checkedSet[rowNum].length - 1)
                {
                    //여기가 마지막 칸이면 완성시키고 반복문을 종료한다.
                    checkTemp[checkIdx] = true;
                    break;
                }
                else if(checkedSet[rowNum][i+1] != 1)
                {
                    //마지막 칸이 아니라면 다음 칸을 확인하고 채워져 있지 않다면 완성시킨다.
                    checkTemp[checkIdx] = true;
                    sumTemp = 0;
                    checkIdx++;
                }

                if(checkIdx == idxNumSet[rowNum])
                {
                    //마지막 숫자까지 다 찾았으면 남은 칸을 전체 스캔한다.
                    for(int j = i+1; j < checkedSet[rowNum].length; j++)
                    {
                        if(checkedSet[rowNum][j] == 1)
                        {
                            //체크된 칸이 있는 경우 뭔가 문제가 있다고 판단한다.
                            //모든 숫자의 완성 여부를 false로 만든 후 반환한다.
                            Arrays.fill(checkTemp, false);

                            return checkTemp;
                        }
                    }
                    break;
                }
            }
        }

        //끝부터 확인
        sumTemp = 0;
        checkIdx = idxNumSet[rowNum] - 1;

        for(int i = checkedSet[rowNum].length - 1; i >= 0; i--)
        {
            if(checkedSet[rowNum][i] == 0)
            {
                //만약 공백이 나오면 반복문 종료
                break;
            }
            else if(checkedSet[rowNum][i] == 1)
            {
                //체크되어있을 경우 갯수를 센다
                sumTemp++;
            }
            else if(checkedSet[rowNum][i] == 2)
            {
                //x 표시일 경우 센 개수를 리셋해준다
                sumTemp = 0;
            }

            if(sumTemp == dataSet[rowNum][checkIdx])
            {
                //이번 칸까지 체크를 셌는데 갯수가 맞다면

                if(i == 0)
                {
                    //여기가 마지막 칸이면 완성시키고 반복문을 종료한다.
                    checkTemp[checkIdx] = true;
                    break;
                }
                else if(checkedSet[rowNum][i-1] != 1)
                {
                    //마지막 칸이 아니라면 다음 칸을 확인하고 채워져 있지 않다면 완성시킨다.
                    checkTemp[checkIdx] = true;
                    sumTemp = 0;
                    checkIdx--;
                }

                if(checkIdx == -1)
                {
                    //마지막 숫자까지 다 찾았으면 남은 칸을 전체 스캔한다.
                    for(int j = i-1; j >= 0; j--)
                    {
                        if(checkedSet[rowNum][j] == 1)
                        {
                            //체크된 칸이 있는 경우 뭔가 문제가 있다고 판단한다.
                            //모든 숫자의 완성 여부를 false로 만든 후 반환한다.
                            Arrays.fill(checkTemp, false);

                            return checkTemp;
                        }
                    }
                    break;
                }
            }
        }

        //혹시 모든 숫자가 채워진 경우인지 확인
        checkIdx = 0;
        sumTemp = 0;

        for(int i = 0; i < checkedSet[rowNum].length; i++)
        {
            if(checkedSet[rowNum][i] == 1)
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


            if(sumTemp == dataSet[rowNum][checkIdx])
            {
                //sumTemp의 숫자가 dataSet에 저장된 숫자와 동일할 때
                if(i == checkedSet[rowNum].length - 1 || checkedSet[rowNum][i + 1] != 1)
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
                for(int j = i+1; j < checkedSet[0].length; j++)
                {
                    if(checkedSet[rowNum][j] == 1)
                    {
                        Arrays.fill(checkTemp, false);
                        return checkTemp;
                    }
                }
                Arrays.fill(checkTemp, true);
                endRow[rowNum] = true;
                return checkTemp;
            }
        }



        if(sumTemp == dataSet[rowNum][checkIdx])
        {
            checkIdx++;
        }

        if(checkIdx == idxNumSet[rowNum])
        {
            //모든 idx가 맞춰졌을 때
            Arrays.fill(checkTemp, true);
            endRow[rowNum] = true;
            return checkTemp;
        }

        return checkTemp;
    }

    public void updateNumColor(int columnNum, int[][] checkedSet)
    {
        //숫자가 다 채워질 경우 색 바꾸기
        boolean[] checkTemp = getIdxMatch(columnNum, checkedSet);

        tvList.get(columnNum).setText("");
        SpannableStringBuilder numStr;
        for(int i = 0; i < idxNumSet[columnNum]; i++)
        {
            numStr = new SpannableStringBuilder(' ' + String.valueOf(dataSet[columnNum][i]));
            if(checkTemp[i])
            {
                numStr.setSpan(new ForegroundColorSpan(Color.parseColor("#a0a0a0")), 0, numStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            else
            {
                numStr.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, numStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            tvList.get(columnNum).append(numStr);
        }
    }
}