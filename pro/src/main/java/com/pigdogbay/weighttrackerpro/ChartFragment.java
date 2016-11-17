package com.pigdogbay.weighttrackerpro;

import java.io.File;
import java.util.List;

import com.pigdogbay.lib.utils.ActivityUtils;
import com.pigdogbay.lib.utils.FileUtils;
import com.pigdogbay.weightrecorder.model.ChartLogic;
import com.pigdogbay.weightrecorder.model.DummyData;
import com.pigdogbay.weightrecorder.model.MainModel;
import com.pigdogbay.weightrecorder.model.Query;
import com.pigdogbay.weightrecorder.model.Reading;
import com.pigdogbay.weightrecorder.model.UserSettings;
import com.pigdogbay.weightrecorder.utils.ActivitiesHelper;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class ChartFragment extends Fragment {
	public static final String TAG = "chart";

	private static final int MINIMUM_READINGS = 3;
	static int _Period = 0;
	private MainModel _MainModel;
	private boolean _UseDummyReadings;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_MainModel = new MainModel(getActivity());
		_UseDummyReadings =_MainModel.getDatabase().getReadingsCount()< MINIMUM_READINGS; 
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_chart, container,false);
		((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setHasOptionsMenu(true);
		return rootView;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		createChart();
		if (_UseDummyReadings)
		{
			ActivitiesHelper.showInfoDialog(getActivity(), R.string.chart_notenoughdata_title, R.string.chart_notenoughdata_message);
		}
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		_MainModel.close();
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_chart, menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
		case (R.id.menu_chart_share):
			shareScreenShot();
			return true;
		case (R.id.menu_chart_all):
			_Period = 0;
			break;
		case (R.id.menu_chart_30_days):
			_Period = 30;
			break;
		case (R.id.menu_chart_60_days):
			_Period = 60;
			break;
		case (R.id.menu_chart_90_days):
			_Period = 90;
			break;
		case (R.id.menu_chart_one_year):
			_Period = 365;
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		createChart();
		return true;
	}
	private void createChart()
	{
		ViewGroup layout = (ViewGroup) getView().findViewById(R.id.chart_container);
		layout.removeAllViews();
		UserSettings userSettings = _MainModel.getUserSettings();
		Query query = new Query(createReadings());
		query.sortByDate();
		ChartLogic chartLogic = new ChartLogic(userSettings);
		ReadingsChart chart = new ReadingsChart(getActivity());
		chart.setXAxisTitle(getString(R.string.chart_xaxis_title));
		chart.setYAxisTitle(String.format("%s (%s)", 
				getString(R.string.chart_yaxis_title),
				userSettings.WeightConverter.getUnits()));
		chart.setChartAxesRanges(chartLogic.calculateAxesRanges(query, _Period));
		chart.addReadings(chartLogic.createReadingsSeries(query));
		if (userSettings.ShowTargetLine)
		{
			chart.addTarget(chartLogic.createTargetSeries());
		}
		if (userSettings.ShowTrendLine)
		{
			chart.addTrend(chartLogic.createTrendSeries(query, _Period));
		}
		layout.addView(chart.createView());
	}
	private List<Reading> createReadings()
	{
		return _UseDummyReadings ?
				DummyData.createRandomData(120) : 
				_MainModel.getDatabase().getAllReadings();
	}
	private void shareScreenShot()
	{
		try
		{
			Bitmap screenshot = ActivityUtils.takeScreenShot(getActivity());
			String filename = FileUtils.appendDate(this.getString(R.string.app_name),".png");
			File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			File file = new File(path,filename);
			if (file.exists()) {
				file.delete();
			}			
			FileUtils.writeImage(file, screenshot);
			ActivitiesHelper.SendFile(getActivity(), file,"image/png", R.string.chart_share_chooser_title);
		}
		catch(Exception e){
			Toast.makeText(getActivity(),
					this.getString(R.string.readings_export_error),
					Toast.LENGTH_SHORT).show();		}
	}

}
