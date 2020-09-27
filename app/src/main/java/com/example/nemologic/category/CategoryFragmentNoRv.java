package com.example.nemologic.category;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.nemologic.R;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.SqlManager;
import com.example.nemologic.level.LevelFragment;
import com.example.nemologic.mainactivity.MainActivity;

import java.sql.SQLException;
import java.util.ArrayList;

public class CategoryFragmentNoRv extends Fragment {

    Context ctx;

    public CategoryFragmentNoRv(Context ctx) {
        // Required empty public constructor
        this.ctx = ctx;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_category_norv, container, false);

        LinearLayout ll_category = fragmentView.findViewById(R.id.ll_category);

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

        for(int i = 0; i < categoryArray.size(); i++)
        {

            View categoryBtnView = inflater.inflate(R.layout.item_category, container, false);
            final String categoryName = categoryArray.get(i);

            Button btn_item_category_name = categoryBtnView.findViewById(R.id.btn_category_name);
            btn_item_category_name.setText(categoryName);

            btn_item_category_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //클릭 시 해당 카테고리에 해당하는 게임 레벨들을 나열하는 LevelActivity로 이동
                    Fragment dest = new LevelFragment(ctx);
                    // Fragment 생성
                    Bundle bundle = new Bundle();
                    bundle.putString("category", categoryName);
                    dest.setArguments(bundle);
                    ((MainActivity)ctx).fragmentMove(dest);
                }
            });

            ll_category.addView(categoryBtnView);
        }




        //////////////////fragment button events
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