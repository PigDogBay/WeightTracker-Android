package com.pigdogbay.weighttrackerpro;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WelcomeWizardFragment extends Fragment {
	public static final String TAG = "welcome";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
////        if (supportActionBar!=null){
////            supportActionBar.setDisplayHomeAsUpEnabled(true);
////        }
//        setHasOptionsMenu(true);
		View rootView = inflater.inflate(R.layout.fragment_welcome_wizard, container,false);
		//Need to use ChildFragmentManager as ViewPager is nested in a fragment
		//If you use getFragmentManager then the red/blue/green fragments are not released
		//when the VPFragment is destroyed
		WelcomePagerAdapter adapter = new WelcomePagerAdapter(getChildFragmentManager(), getActivity());
        ViewPager viewPager = (ViewPager)rootView.findViewById(R.id.welcome_wizard_viewpager);
        viewPager.setAdapter(adapter);		
		return rootView;
	}
	
    public static class WelcomePagerAdapter extends FragmentPagerAdapter {
    	Context _Context;

        WelcomePagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            _Context = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new WelcomeStartFragment();
                case 1:
                    return new WeightSettingsFragment();
                case 2:
                    return new HeightSettingsFragment();
                case 3:
                    return new ConnectToDriveFragment();
                case 4:
                    return new WelcomeEndFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return _Context.getString(R.string.fragment_welcome_wizard_page_title_welcome);
                case 1:
                    return _Context.getString(R.string.fragment_welcome_wizard_page_title_weight);
                case 2:
                    return _Context.getString(R.string.fragment_welcome_wizard_page_title_height);
                case 3:
                    return _Context.getString(R.string.fragment_welcome_wizard_page_title_connect);
                case 4:
                    return _Context.getString(R.string.fragment_welcome_wizard_page_title_finished);
            }
            return null;
        }
    }
	
    public static class WelcomeStartFragment extends Fragment{
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_welcome_start, container, false);
        }
    }	
    public static class WelcomeEndFragment extends Fragment{
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_welcome_end, container, false);
            view.findViewById(R.id.welcomeEndDoneBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().onBackPressed();
                }
            });
            return view;
        }
    }		
}
