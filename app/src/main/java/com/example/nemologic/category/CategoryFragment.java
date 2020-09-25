package com.example.nemologic.category;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nemologic.R;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.SqlManager;
import com.example.nemologic.mainactivity.MainActivity;

import java.sql.SQLException;
import java.util.ArrayList;

public class CategoryFragment extends Fragment {

    Context ctx;

    public CategoryFragment(Context ctx) {
        // Required empty public constructor
        this.ctx = ctx;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_category, container, false);

        RecyclerView rv_category = fragmentView.findViewById(R.id.rv_category);

        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Cursor categoryCursor =  mDbOpenHelper.getCategoryCursor();
        ArrayList<String> categoryArray = new ArrayList<>();

        while(categoryCursor.moveToNext()) {

            String name = categoryCursor.getString(categoryCursor.getColumnIndex(SqlManager.CreateCategoryDB.NAME));

            categoryArray.add(name);
        }

        mDbOpenHelper.close();
        String[] categoryStringArray = new String[categoryCursor.getCount()];

        for(int i = 0; i < categoryArray.size(); i++)
        {
            categoryStringArray[i] = categoryArray.get(i);
        }

        rv_category.addItemDecoration(new VerticalSpaceItemDecoration(100));

        rv_category.setLayoutManager(new LinearLayoutManager(ctx));
        rv_category.setAdapter(new RvCategoryAdapter(ctx, categoryStringArray));

        ImageView img_back = fragmentView.findViewById(R.id.img_back);

        img_back.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        view.setAlpha(0.5F);
                        break;
                    case MotionEvent.ACTION_UP:
                        view.setAlpha(1F);
                        getFragmentManager().popBackStackImmediate();
                }
                return false;
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


            }
        });

        return fragmentView;
    }
}