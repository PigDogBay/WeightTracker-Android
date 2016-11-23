package com.pigdogbay.weighttrackerpro;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HelpWizardFragment extends Fragment {
	public static final String TAG = "help";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_quickhelp, container,false);
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (supportActionBar!=null)
        {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        setHasOptionsMenu(true);
		return rootView;
	}
}
