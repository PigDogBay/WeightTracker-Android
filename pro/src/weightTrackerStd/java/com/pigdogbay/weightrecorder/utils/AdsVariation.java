package com.pigdogbay.weightrecorder.utils;

import android.os.Bundle;
import android.view.View;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.pigdogbay.weighttrackerpro.R;

public class AdsVariation implements IAds {

    private AdView _AdView;

    @Override
    public void pause() {
        if (_AdView != null) {
            _AdView.pause();
        }
    }

    @Override
    public void resume() {
        if (_AdView != null) {
            _AdView.resume();
        }
    }

    @Override
    public void setUp(View view) {
        MobileAds.initialize(view.getContext(), "ca-app-pub-3582986480189311~6146792389");
        // Look up the AdView as a resource and load a request.
        _AdView = view.findViewById(R.id.adView);
        Bundle extras = new Bundle();
        extras.putString("max_ad_content_rating", "G");
        //MA = Mature Adult, may improve eCPM?
        //extras.putString("max_ad_content_rating", "MA");

        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(view.getResources().getString(R.string.code_test_device_acer_tablet))
                .addTestDevice(view.getResources().getString(R.string.code_test_device_moto_g))
                .addTestDevice(view.getResources().getString(R.string.code_test_device_nokia_6))
                .build();
        _AdView.loadAd(adRequest);

    }
}