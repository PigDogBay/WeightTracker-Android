package com.pigdogbay.weightrecorder.utils;

import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;
import com.pigdogbay.lib.diagnostics.TestDevices;

public class NativeAd
{
    /**
     * Takes 900-1500ms to create an native ad, most time is spent in loadAd()
     * Subsequent load times are around 150ms.
     *
     * @param rootView container to hold native ad
     */
    public static void setUpAd(View rootView){
        DisplayMetrics displayMetrics = rootView.getContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        //10dp margin and 2dp padding
        dpWidth = dpWidth - 24.0f;
        NativeExpressAdView adView = new NativeExpressAdView(rootView.getContext());
        adView.setAdUnitId("ca-app-pub-3582986480189311/4756228785");
        if (dpWidth<100) {
            adView.setAdSize(new AdSize(AdSize.FULL_WIDTH, 100));
        }else{
            adView.setAdSize(new AdSize((int) dpWidth, 100));
        }
        ((ViewGroup) rootView).addView(adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(TestDevices.TEST_DEVICE_ID_1)
                .addTestDevice(TestDevices.TEST_DEVICE_ID_2)
                .addTestDevice(TestDevices.TEST_DEVICE_ID_3)
                .addTestDevice(TestDevices.TEST_DEVICE_ID_4)
                .addTestDevice(TestDevices.TEST_DEVICE_ID_5)
                .addTestDevice(TestDevices.TEST_DEVICE_ID_6)
                .addTestDevice(TestDevices.TEST_DEVICE_ID_7)
                .build();
        adView.loadAd(adRequest);
    }
}