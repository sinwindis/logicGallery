package com.sinwindis.logicgallery.mainactivity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.ads.initialization.AdapterStatus;
import com.sinwindis.logicgallery.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MainActivity", "activity created");

        //광고
        MobileAds.initialize(this, initializationStatus -> {

            /* get the adapter status */
            Map<String, AdapterStatus> map = initializationStatus.getAdapterStatusMap();
            for (Map.Entry<String, AdapterStatus> entry : map.entrySet()) {
                AdapterStatus adapterStatus = entry.getValue();
                AdapterStatus.State state = adapterStatus.getInitializationState();
            }
        });

        //MobileAds.initialize(this, getString(R.string.admob_app_id));
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.add(R.id.fl_main, new MainFragment(this));
        t.commit();

//        mIntersisialAd = new InterstitialAd(this);
//        mIntersisialAd.setAdUnitId(getString(R.string.popup_ad_unit_id));
//        mIntersisialAd.loadAd(new AdRequest.Builder().build());
    }

    public void fragmentMove(Fragment dest)
    {
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.addToBackStack(null);
        t.setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out, R.anim.anim_fade_in, R.anim.anim_fade_out);
        t.replace(R.id.fl_main, dest);
        t.commit();
    }

    public void fragmentMoveNoStack(Fragment dest)
    {
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        getSupportFragmentManager().popBackStack();
        t.addToBackStack(null);
        t.setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out, R.anim.anim_fade_in, R.anim.anim_fade_out);
        t.replace(R.id.fl_main, dest);
        t.commit();
    }
}