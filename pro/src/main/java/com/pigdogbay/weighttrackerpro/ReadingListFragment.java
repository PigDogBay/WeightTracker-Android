package com.pigdogbay.weighttrackerpro;

import com.pigdogbay.lib.utils.ActivityUtils;
import com.pigdogbay.weightrecorder.model.BMICalculator;
import com.pigdogbay.weightrecorder.model.MainModel;
import com.pigdogbay.weightrecorder.model.Reading;
import com.pigdogbay.weightrecorder.model.ReportFormatting;
import com.pigdogbay.weightrecorder.utils.ActivitiesHelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ReadingListFragment extends ListFragment {
	public static final String TAG = "list";

	private MainModel _MainModel;
	private ReadingsAdapter _Adapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        _MainModel = new MainModel(getActivity());
        _Adapter = this.new ReadingsAdapter();
        setListAdapter(_Adapter);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setHasOptionsMenu(true);
		View rootView = inflater.inflate(R.layout.fragment_readings_list, container,false);
		return rootView;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        if (_Adapter.getCount()==0)
        {
			ActivityUtils.showInfoDialog(getActivity(),R.string.readings_no_readings_dialog_title,R.string.readings_no_readings_dialog_msg, R.string.ok);
        }
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_readings_list, menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
		case (R.id.menu_readings_list_delete_all):
			deleteAllMenuOption();
			return true;
		case (R.id.menu_readings_list_export):
			ActivitiesHelper.shareReadings(getActivity());
			return true;
		case (R.id.menu_readings_list_import):
			((MainActivity)getActivity()).showImport();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}	
	@Override
	public void onDestroy() {
		super.onDestroy();
		_MainModel.close();
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Reading reading = ((ReadingsAdapter)getListAdapter()).getItem(position);
		((MainActivity)getActivity()).showEdit(reading);
	}
	private void deleteAllMenuOption()
	{
		String title = getResources().getString(R.string.editreading_delete_dialog_title);
		String message = getResources().getString(R.string.editreading_delete_dialog_message);
		new AlertDialog.Builder(getActivity())
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle(title)
				.setMessage(message)
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog,
									int which)
							{
								dialog.dismiss();
							}
						})	
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog,
									int which)
							{
								_MainModel.getDatabase().deleteAllReadings();
								_Adapter.clear();
								_Adapter.notifyDataSetChanged();
								dialog.dismiss();
							}
						}).show();		
	}


	private class ReadingsAdapter extends ArrayAdapter<Reading>
	{
		private ReportFormatting _ReportFormatting;
		private BMICalculator _BMICalculator;

		public ReadingsAdapter() {
			super(getActivity(),R.layout.readings_item,_MainModel.getReverseOrderedReadings());
			_ReportFormatting = new ReportFormatting(getActivity(), _MainModel.getUserSettings());
			_BMICalculator = new BMICalculator(_MainModel.getUserSettings());
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			if (convertView == null)
			{
				LayoutInflater inflater = getActivity().getLayoutInflater();
				convertView = inflater.inflate(R.layout.readings_item, null);
			}
			Reading reading = getItem(position);
			TextView textView = (TextView) convertView
					.findViewById(R.id.ResultsItemWeight);
			textView.setText(_ReportFormatting.getWeightString(reading.getWeight()));

			textView = (TextView) convertView.findViewById(R.id.ResultsItemDate);
			String dateText = _ReportFormatting.getDateTimeString(reading.getDate()); 
			textView.setText(dateText);
			textView = (TextView) convertView.findViewById(R.id.ResultsItemComment);
			textView.setText(getComment(reading));
			return convertView;
		}
		
		private String getComment(Reading reading)
		{
			String comment = reading.getComment();
			if ("".equals(comment))
			{
				double bmi = _BMICalculator.calculateBMI(reading);
				comment = getActivity().getString(R.string.readings_bmi_prefix)+" "+_ReportFormatting.getBMIString(bmi);
			}
			return comment;
		}
		
	}

}
