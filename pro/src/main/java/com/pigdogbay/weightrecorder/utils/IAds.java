package com.pigdogbay.weightrecorder.utils;

import android.view.View;

/**
 * Interface to show ads, pro version will implement a dummy version of this
 * Created by Mark on 21/11/2016.
 */
public interface IAds {
    void pause();
    void resume();
    void setUp(View view);
}
