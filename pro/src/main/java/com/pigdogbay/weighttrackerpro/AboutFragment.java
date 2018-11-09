package com.pigdogbay.weighttrackerpro;

import com.pigdogbay.lib.utils.ActivityUtils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class AboutFragment extends Fragment {
	public static final String TAG = "about";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		if (supportActionBar != null)
		{
			supportActionBar.setDisplayHomeAsUpEnabled(true);
		}
		View rootView = inflater.inflate(R.layout.fragment_about, container, false);
		rootView.findViewById(R.id.aboutBtnRate).setOnClickListener(v -> showWebPage(getActivity(),R.string.market_app_url));

		rootView.findViewById(R.id.aboutBtnSendFeedback).setOnClickListener(v -> sendFeedback(getActivity()));

		rootView.findViewById(R.id.aboutBtnLegal).setOnClickListener(v -> showLegalNotices());
		rootView.findViewById(R.id.aboutBtnReleaseNotes).setOnClickListener(v -> showWebPage(getActivity(),R.string.release_notes_url));
		rootView.findViewById(R.id.aboutBtnViewPrivacy).setOnClickListener(v -> showWebPage(getActivity(),R.string.privacy_policy_url));
	return rootView;
	}

	public static void showWebPage(Activity activity, int urlId)
	{
		try
		{
			ActivityUtils.ShowWebPage(activity, activity.getString(urlId));
		}
		catch (ActivityNotFoundException e)
		{
			Toast.makeText(activity, activity.getString(R.string.web_error), Toast.LENGTH_LONG)
					.show();
		}
	}

	private void showLegalNotices(){
		ActivityUtils.showInfoDialog(getActivity(), R.string.copyright_title, R.string.copyright,R.string.ok);
	}

	public static void sendFeedback(Activity activity)
	{
		ActivityUtils.SendEmail(
				activity,
				new String[]{activity.getString(R.string.email)},
				activity.getString(R.string.about_button_feedback_subject),
				activity.getString(R.string.about_body_feedback));
	}}
