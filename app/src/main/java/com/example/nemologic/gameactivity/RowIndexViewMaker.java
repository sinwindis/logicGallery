package com.example.nemologic.gameactivity;

import android.app.Activity;
import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;

public class RowIndexViewMaker {
    
    int[][] dataSet;
    int[][] idxDataSet;

    RecyclerView rv_row;

    public RvRowAdapter rvRowAdapter;
    
    public RowIndexViewMaker(int[][] dataSet)
    {
        this.dataSet = dataSet.clone();
    }

    public void setView(Context ctx)
    {
        makeIdxDataSet();

        rv_row = ((Activity)ctx).findViewById(R.id.rv_row);

        rvRowAdapter = new RvRowAdapter(idxDataSet, ctx);
        rv_row.setLayoutManager(new LinearLayoutManager(ctx));
        rv_row.setAdapter(rvRowAdapter);
    }

    private void makeIdxDataSet()
    {
        idxDataSet = new int[dataSet.length][dataSet[0].length];
        int sumTemp;
        int idx;

        for(int y = 0; y < dataSet.length; y++)
        {
            sumTemp = 0;
            idx = 0;
            idxDataSet[y][0] = 0;
            for(int x = 0; x < dataSet[0].length; x++)
            {
                if(dataSet[y][x] == 1)
                {
                    sumTemp++;
                }
                else if(sumTemp != 0)
                {
                    idxDataSet[y][idx] = sumTemp;
                    idx++;
                    sumTemp = 0;
                }
            }

            if(sumTemp != 0)
            {
                idxDataSet[y][idx] = sumTemp;
            }
        }
    }
}
