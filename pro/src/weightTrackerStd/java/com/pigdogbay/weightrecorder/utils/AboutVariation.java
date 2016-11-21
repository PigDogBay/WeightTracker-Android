package com.pigdogbay.weightrecorder.utils;

import android.support.v4.app.Fragment;
import android.view.View;

import com.pigdogbay.weighttrackerpro.AboutFragment;
import com.pigdogbay.weighttrackerpro.R;

public class AboutVariation
{
    public AboutVariation(final Fragment fragment,final View rootView){
        rootView.findViewById(R.id.aboutBtnGoPro).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutFragment.showWebPage(fragment.getActivity(),R.string.market_pro_app_url);
            }
        });

    }
}
