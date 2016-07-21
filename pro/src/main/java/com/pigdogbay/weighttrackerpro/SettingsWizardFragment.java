package com.pigdogbay.weighttrackerpro;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsWizardFragment extends Fragment{
	public static final String TAG = "settings";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setHasOptionsMenu(true);
		View rootView = inflater.inflate(R.layout.fragment_settings_wizard, container,false);
		//Need to use ChildFragmentManager as ViewPager is nested in a fragment
		//If you use getFragmentManager then the red/blue/green fragments are not released
		//when the VPFragment is destroyed
        SettingsPagerAdapter adapter = new SettingsPagerAdapter(getChildFragmentManager(), getActivity());
        ViewPager viewPager = (ViewPager)rootView.findViewById(R.id.settings_wizard_viewpager);
        viewPager.setAdapter(adapter);
		return rootView;
	}

    public static class SettingsPagerAdapter extends FragmentPagerAdapter {
    	Context _Context;

        public SettingsPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            _Context = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new WeightSettingsFragment();
                case 1:
                    return new HeightSettingsFragment();
                case 2:
                    return new BackgroundSettingsFragment();
                case 3:
                    return new OtherSettingsFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return _Context.getString(R.string.fragment_settings_wizard_page_title_weight);
                case 1:
                    return _Context.getString(R.string.fragment_settings_wizard_page_title_height);
                case 2:
                    return _Context.getString(R.string.fragment_settings_wizard_page_title_background);
                case 3:
                    return _Context.getString(R.string.fragment_settings_wizard_page_title_other);
            }
            return null;
        }
    }

}
