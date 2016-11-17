package com.pigdogbay.weighttrackerpro;

import com.pigdogbay.weightrecorder.utils.ActivitiesHelper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class ImportFragment extends Fragment {
	public static final String TAG = "import";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_import, container,false);
		ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		if (supportActionBar!=null) {
			supportActionBar.setDisplayHomeAsUpEnabled(true);
		}
		rootView.findViewById(R.id.ImportOKButton)
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				importReadings();
			}
		});
		return rootView;
	}

	private void importReadings() {
		EditText editText = (EditText) getView().findViewById(R.id.ImportEdit);
		String text = editText.getText().toString();
		if ("".equals(text)) {
			Toast.makeText(getActivity(), getString(R.string.import_no_text),
					Toast.LENGTH_SHORT).show();
		}
		else {
			int count = ActivitiesHelper.mergeReadings(getActivity(), text);
			Toast.makeText(
					getActivity(),
					String.format(getString(R.string.import_readings_added), count),
					Toast.LENGTH_SHORT).show();
		}
		((MainActivity)getActivity()).navigateBack(TAG);
	}
	

}
