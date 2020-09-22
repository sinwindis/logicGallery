package com.example.nemologic.mainactivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.nemologic.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

    public void fragmentMove(Fragment dest)
    {
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.fl_main, dest);
        t.addToBackStack(null);
        t.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combined);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.add(R.id.fl_main, new MainFragment(this));
        t.commit();

        //광고
        MobileAds.initialize(this, getString(R.string.admob_app_id));
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

}