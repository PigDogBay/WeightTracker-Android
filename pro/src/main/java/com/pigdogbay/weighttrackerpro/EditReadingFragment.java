package com.pigdogbay.weighttrackerpro;

import com.pigdogbay.weightrecorder.model.MainModel;
import com.pigdogbay.weightrecorder.model.Reading;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class EditReadingFragment extends EditFragment{
	private static final String BUNDLE_READING_ID = "BUNDLE_READING_ID";
	
	public static final String TAG = "edit";
	private Reading _Reading;
	private MainModel _MainModel;
	
	public void setReadingToEdit(Reading reading)
	{
		_Reading = reading;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_edit_reading, container,false);

		return rootView;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setHasOptionsMenu(true);
		((Button) getView().findViewById(R.id.EditReadingBtnUpdate))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onUpdateClick();
			}
		});
		((Button) getView().findViewById(R.id.EditReadingBtnDelete))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onDeleteClick();
			}
		});
		_MainModel = new MainModel(getActivity());
		setWeightConvert(_MainModel.getWeightConverter());
		if (savedInstanceState!=null)
		{
			int id = savedInstanceState.getInt(BUNDLE_READING_ID);
			_Reading = _MainModel.getDatabase().getReading(id);
		}
		if (null!=_Reading)
		{
			setReading(_Reading);
		}
		
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(BUNDLE_READING_ID, _Reading.getID());
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		_MainModel.close();
	}
	
	private void onUpdateClick() {
		Reading reading = getReading();
		reading.setID(_Reading.getID());
		_MainModel.getDatabase().updateReading(reading);
		Toast.makeText(getActivity(), getString(R.string.editreading_updated),
				Toast.LENGTH_SHORT).show();
		((MainActivity)getActivity()).navigateBack(TAG);
	}

	private void onDeleteClick() {
		_MainModel.getDatabase().deleteReading(_Reading);
		Toast.makeText(getActivity(), getString(R.string.editreading_deleted),
				Toast.LENGTH_SHORT).show();
		((MainActivity)getActivity()).navigateBack(TAG);
	}

	
}
