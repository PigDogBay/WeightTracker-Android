package com.pigdogbay.weighttrackerpro;

import java.io.File;
import com.pigdogbay.lib.utils.FileUtils;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ImportFragment extends Fragment {
	public static final String TAG = "import";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_import, container,false);
		((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setHasOptionsMenu(true);
		((Button) rootView.findViewById(R.id.ImportOKButton))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				importReadings();
			}
		});
		((Button) rootView.findViewById(R.id.ImportLoadBackupButton))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				loadBackupReadings();
			}
		});
		return rootView;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		showHelp();
	}
	
	private void loadBackupReadings() {
		File latest = ActivitiesHelper.getLatestBackupFile(getActivity());
		if (latest == null) {
			Toast.makeText(getActivity(), getString(R.string.import_no_backup_file),
					Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			String data = FileUtils.readText(latest);
			TextView textView = (TextView) getView().findViewById(R.id.ImportEdit);
			textView.setText(data);
		}
		catch (Exception e) {
			Toast.makeText(getActivity(), getString(R.string.import_error_loading_backup_file),
					Toast.LENGTH_SHORT).show();
		}
	}

	private void showHelp() {
		String title = getResources().getString(R.string.import_dialog_title);
		String message = getResources().getString(
				R.string.import_dialog_message);
		new AlertDialog.Builder(getActivity())
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).show();

	}

	private void importReadings() {
		EditText editText = (EditText) getView().findViewById(R.id.ImportEdit);
		String text = editText.getText().toString();
		if (text == null || "".equals(text)) {
			Toast.makeText(getActivity(), getString(R.string.import_no_text),
					Toast.LENGTH_SHORT).show();
		}
		else {
			int count = ActivitiesHelper.mergeReadings(getActivity(), text);
			Toast.makeText(
					getActivity(),
					String.valueOf(count)
							+ getString(R.string.import_readings_added),
					Toast.LENGTH_SHORT).show();
		}
		((MainActivity)getActivity()).navigateBack(TAG);
	}
	

}
