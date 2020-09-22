package com.example.nemologic.game;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;

public class ColumnIndexViewMaker {
    
    int[][] dataSet;
    int[][] idxDataSet;

    RecyclerView rv_column;

    public RvColumnAdapter rvColumnAdapter;
    
    public ColumnIndexViewMaker(int[][] dataSet)
    {
        this.dataSet = dataSet.clone();
    }
    
    public void setView(View view)
    {
        makeIdxDataSet();

        rv_column = view.findViewById(R.id.rv_column);

        rvColumnAdapter = new RvColumnAdapter(idxDataSet);
        rv_column.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv_column.setAdapter(rvColumnAdapter);
    }

    private void makeIdxDataSet()
    {
        idxDataSet = new int[dataSet[0].length][dataSet.length];
        int sumTemp;
        int idx;

        for(int x = 0; x < dataSet[0].length; x++)
        {
            sumTemp = 0;
            idx = 0;
            idxDataSet[x][0] = 0;
            for(int y = 0; y < dataSet.length; y++)
            {
                if(dataSet[y][x] == 1)
                {
                    sumTemp++;
                }
                else if(sumTemp != 0)
                {
                    idxDataSet[x][idx] = sumTemp;
                    idx++;
                    sumTemp = 0;
                }
            }

            if(sumTemp != 0)
            {
                idxDataSet[x][idx] = sumTemp;
            }
        }
    }
}
