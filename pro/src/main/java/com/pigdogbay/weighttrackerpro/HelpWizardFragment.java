package com.pigdogbay.weighttrackerpro;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HelpWizardFragment extends Fragment {
	public static final String TAG = "help";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_help_wizard, container,false);
		((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setHasOptionsMenu(true);
		//Need to use ChildFragmentManager as ViewPager is nested in a fragment
		//If you use getFragmentManager then the red/blue/green fragments are not released
		//when the VPFragment is destroyed
    	HelpPagerAdapter helpPagerAdapter = new HelpPagerAdapter(getChildFragmentManager(), getActivity());
    	ViewPager viewPager = (ViewPager)rootView.findViewById(R.id.help_wizard_viewpager);
    	viewPager.setAdapter(helpPagerAdapter);
		return rootView;
	}
	
    public static class HelpPagerAdapter extends FragmentPagerAdapter {
    	Context _Context;

        public HelpPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            _Context = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new QuickHelpFragment();
                case 1:
                    return new HelpFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return _Context.getString(R.string.fragment_help_wizard_page_title_quick_help);
                case 1:
                    return _Context.getString(R.string.fragment_help_wizard_page_title_help);
            }
            return null;
        }
    }
	
    public static class QuickHelpFragment extends Fragment{
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_quickhelp, container, false);
            return rootView;
        }
    }	
    public static class HelpFragment extends Fragment{
    	static int _TextStyleID = android.R.style.TextAppearance_Medium;

    	@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_help, container, false);
            return rootView;
        }
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
        	super.onActivityCreated(savedInstanceState);
    		TextView textView = (TextView) getActivity().findViewById(R.id.HelpTextBox);
    		textView.setText(Html.fromHtml(getString(R.string.help_html)));
    		textView.setMovementMethod(new ScrollingMovementMethod());
    		((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    		setHasOptionsMenu(true);
        }
        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        	super.onCreateOptionsMenu(menu, inflater);
    		inflater.inflate(R.menu.menu_help, menu);
        }
    	@Override
    	public boolean onOptionsItemSelected(MenuItem item)
    	{
    		switch (item.getItemId())
    		{
    		case (R.id.menu_help_text_small):
    			_TextStyleID = android.R.style.TextAppearance_Small;
    			break;
    		case (R.id.menu_help_text_medium):
    			_TextStyleID = android.R.style.TextAppearance_Medium;
    			break;
    		case (R.id.menu_help_text_large):
    			_TextStyleID = android.R.style.TextAppearance_Large;
    			break;
    		default:
    			return super.onOptionsItemSelected(item);
    		}
    		TextView textView = (TextView) getView().findViewById(R.id.HelpTextBox);
    		if(textView!=null)
    		{
    			textView.setTextAppearance(getActivity(), _TextStyleID);
    		}
    		return true;
    	}	
        
    }		
}
