package com.example.nemologic.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.example.nemologic.R;
import com.example.nemologic.animation.ButtonAnimation;
import com.example.nemologic.data.StringGetter;

public class SearchFragment extends Fragment {

    private Context ctx;

    //search 부분 뷰
    private ImageView img_back;

    public SearchFragment(Context ctx) {
        // Required empty public constructor
        this.ctx = ctx;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_search, container, false);

        img_back = fragmentView.findViewById(R.id.img_back);

        Spinner artistSpinner = fragmentView.findViewById(R.id.sp_search_artist);
        Spinner progressSpinner = fragmentView.findViewById(R.id.sp_search_progress);
        Spinner sizeSpinner = fragmentView.findViewById(R.id.sp_search_size);
        Spinner puzzleSpinner = fragmentView.findViewById(R.id.sp_search_puzzle);

        ArrayAdapter<String> puzzleSpinnerAdapter = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_dropdown_item, StringGetter.p_name);
        ArrayAdapter<String> artistSpinnerAdapter = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_dropdown_item, StringGetter.a_name);

        puzzleSpinner.setAdapter(puzzleSpinnerAdapter);
        artistSpinner.setAdapter(artistSpinnerAdapter);


        //뒤로가기 버튼
        ButtonAnimation.setOvalButtonAnimationBlack(this.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert getFragmentManager() != null;
                getFragmentManager().popBackStackImmediate();
            }
        });

        return fragmentView;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}