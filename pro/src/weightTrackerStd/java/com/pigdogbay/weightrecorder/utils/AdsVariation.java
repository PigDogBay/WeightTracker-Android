package com.pigdogbay.weightrecorder.utils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.pigdogbay.weighttrackerpro.R;

public class AdsVariation {

    private AdView _AdView;
    private final Activity activity;

    public AdsVariation(Activity activity) {
        this.activity = activity;
    }

    public void pause() {
        if (_AdView != null) {
            _AdView.pause();
        }
    }

    public void resume() {
        if (_AdView != null) {
            _AdView.resume();
        }
    }

    public void setUp() {
        MobileAds.initialize(activity, "ca-app-pub-3582986480189311~6146792389");
        // Look up the AdView as a resource and load a request.
        _AdView = activity.findViewById(R.id.adView);
        Bundle extras = new Bundle();
        extras.putString("max_ad_content_rating", "G");
        //MA = Mature Adult, may improve eCPM?
        //extras.putString("max_ad_content_rating", "MA");

        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(activity.getString(R.string.code_test_device_acer_tablet))
                .addTestDevice(activity.getString(R.string.code_test_device_moto_g))
                .addTestDevice(activity.getString(R.string.code_test_device_nokia_6))
                .build();
        _AdView.loadAd(adRequest);

    }
}