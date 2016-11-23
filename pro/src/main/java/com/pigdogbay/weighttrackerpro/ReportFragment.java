package com.pigdogbay.weighttrackerpro;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pigdogbay.lib.utils.ActivityUtils;
import com.pigdogbay.lib.utils.HtmlPrinting;
import com.pigdogbay.weightrecorder.model.DummyData;
import com.pigdogbay.weightrecorder.model.MainModel;
import com.pigdogbay.weightrecorder.model.Query;
import com.pigdogbay.weightrecorder.model.Reading;
import com.pigdogbay.weightrecorder.model.ReportAnalysis;
import com.pigdogbay.weightrecorder.model.ReportFormatting;
import com.pigdogbay.weightrecorder.model.ReportText;
import com.pigdogbay.weightrecorder.model.UserSettings;
import com.pigdogbay.weightrecorder.utils.ActivitiesHelper;
import com.pigdogbay.weightrecorder.utils.NativeAd;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReportFragment  extends Fragment {
	public static final String TAG = "report";

	private static final String TEXT_FIELD_PREFIX = "ReportTextView";
	private static final int MINIMUM_READINGS = 2;
	private ReportText _ReportText;
	private ReportFormatting _ReportFormatting;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainModel mainModel = new MainModel(getActivity());
		createReport(mainModel);
		mainModel.close();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_report, container,false);
		ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		if (supportActionBar != null)
		{
			supportActionBar.setDisplayHomeAsUpEnabled(true);
		}
		setHasOptionsMenu(true);
		try {
			populateTextViews(rootView);
		}
		catch (Exception e) {
			Log.v(MainActivity.TAG,
					"Unable to populate views: " + e.getMessage());
		}
		NativeAd.setUpAd(rootView.findViewById(R.id.ReportNativeAdHolder));
		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_report, menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
		case (R.id.menu_report_export):
			emailReport();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}	
	private void createReport(MainModel mainModel) {
		UserSettings userSettings = mainModel.getUserSettings();
		List<Reading> readings = mainModel.getDatabase().getAllReadings();
		if (readings.size() < MINIMUM_READINGS) {
			readings = DummyData.createRandomData(120);
			ActivitiesHelper.showInfoDialog(getActivity(),
					R.string.report_notenoughdata_title,
					R.string.report_notenoughdata_message);
		}
		Query query = new Query(readings);
		ReportAnalysis analysis = new ReportAnalysis(userSettings, query);
		_ReportFormatting = new ReportFormatting(getActivity(), userSettings);
		_ReportText = new ReportText(analysis, _ReportFormatting);
	}

	private void populateTextViews(View view) throws IllegalArgumentException,
			IllegalAccessException, NoSuchFieldException {
		for (Map.Entry<String, String> entry : _ReportText.getEntrySet()) {
			String fieldName = entry.getKey().replace("$", TEXT_FIELD_PREFIX);
			try {
				int id = R.id.class.getField(fieldName).getInt(null);
				TextView textView = (TextView) view.findViewById(id);
				textView.setText(entry.getValue());
			}catch (Exception e){e.printStackTrace();}
		}
	}	
	private void emailReport() {
		Date now = new Date();
		String dateText = _ReportFormatting.getDateString(now.getTime());
		String subject = getString(R.string.report_email_subject) + " "	+ dateText;
		String template = getActivity().getString(R.string.report_template);
		String text = _ReportText.createReport(template);
		HtmlPrinting htmlPrinting = new HtmlPrinting(getActivity());
		htmlPrinting.print(text);
	}

}
