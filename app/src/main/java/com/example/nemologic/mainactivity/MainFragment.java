package com.example.nemologic.mainactivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.nemologic.R;
import com.example.nemologic.categoryactivity.CategoryFragment;
import com.example.nemologic.data.DataManager;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.SqlManager;
import com.example.nemologic.gameactivity.GameFragment;
import com.example.nemologic.optionactivity.OptionFragment;

import java.sql.SQLException;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment {

    Context ctx;

    public MainFragment(Context ctx) {
        // Required empty public constructor
        this.ctx = ctx;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Context mainActivityCtx = ctx;

        final View fragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        Context ctx = fragmentView.getContext();

        Button btn_continue = fragmentView.findViewById(R.id.btn_continue);
        Button btn_category = fragmentView.findViewById(R.id.btn_category);
        Button btn_option = fragmentView.findViewById(R.id.btn_option);

        btn_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) Objects.requireNonNull(getActivity())).fragmentMove(new OptionFragment());
            }
        });

        //DB를 갱신해 준다.
        DataManager.loadLevel(ctx);
        DataManager.loadCategory(ctx);

        SharedPreferences lastPlayPref = ctx.getSharedPreferences("LASTPLAY", MODE_PRIVATE);

        //마지막 플레이한 레벨의 이름과 카테고리를 받아온다.
        final String lastPlayName = lastPlayPref.getString("name", "");

        final String lastPlayCategory = lastPlayPref.getString("category", "");


        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mDbOpenHelper.create();

        Cursor cursor;

        TextView tv_item_level_name = fragmentView.findViewById(R.id.tv_item_level_name);

        TextView tv_item_level_size = fragmentView.findViewById(R.id.tv_item_level_size);

        if(lastPlayName.isEmpty() || lastPlayCategory.isEmpty())
        {

        }
        else
        {
            cursor =  mDbOpenHelper.getLevelCursorByCategoryAndName(lastPlayCategory, lastPlayName);

            cursor.moveToNext();
            int lastPlayWidth = cursor.getInt(cursor.getColumnIndex(SqlManager.CreateLevelDB.WIDTH));
            int lastPlayHeight = cursor.getInt(cursor.getColumnIndex(SqlManager.CreateLevelDB.HEIGHT));



            if(cursor.getCount() > 0)
            {
                tv_item_level_name.setText(lastPlayName);
                tv_item_level_size.setText(lastPlayWidth + " X " + lastPlayHeight);
            }
        }


        mDbOpenHelper.close();

//        btn_category.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(categoryIntent);
//            }
//        });
//
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameFragment gameFragment = new GameFragment();

                Bundle bundle = new Bundle();
                bundle.putString("category", lastPlayCategory);
                bundle.putString("name", lastPlayName);
                gameFragment.setArguments(bundle);

                ((MainActivity)getActivity()).fragmentMove(gameFragment);
            }
        });

//        Fragment fragment = new OptionFragment();
//        // Fragment 생성
//        Bundle bundle = new Bundle();
//        //bundle.putString("param1", param1);
//        fragment.setArguments(bundle);

        btn_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).fragmentMove(new CategoryFragment(mainActivityCtx));
            }
        });


        return fragmentView;
    }
}