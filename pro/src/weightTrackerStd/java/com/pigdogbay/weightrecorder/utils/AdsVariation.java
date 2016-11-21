package com.pigdogbay.weightrecorder.utils;

import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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
        // Look up the AdView as a resource and load a request.
        _AdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(view.getResources().getString(R.string.code_test_device_1_id))
                .addTestDevice(view.getResources().getString(R.string.code_test_device_2_id))
                .addTestDevice(view.getResources().getString(R.string.code_test_device_3_id))
                .addTestDevice(view.getResources().getString(R.string.code_test_device_4_id))
                .addTestDevice(view.getResources().getString(R.string.code_test_device_5_id))
                .addTestDevice(view.getResources().getString(R.string.code_test_device_6_id))
                .addTestDevice(view.getResources().getString(R.string.code_test_device_7_id))
                .build();
        _AdView.loadAd(adRequest);

    }
}