package com.pigdogbay.weighttrackerpro;

import java.util.List;

import com.pigdogbay.lib.utils.FileUtils;
import com.pigdogbay.weightrecorder.model.MainModel;
import com.pigdogbay.weightrecorder.model.Reading;
import com.pigdogbay.weightrecorder.model.ReadingsSerializer;
import com.pigdogbay.weightrecorder.model.Synchronization;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ImportActivity extends AppCompatActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import);
		((Button) findViewById(R.id.ImportOKButton))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						importReadings();
					}
				});
		Intent intent = getIntent();
		Uri uri = intent.getData();
		if (null != uri) {
			loadReadings(uri);
		}
		else {
			showHelp();
		}
	}

	private void loadReadings(Uri uri) {
		try {
			String data = FileUtils.readText(this, uri);
			TextView textView = (TextView) findViewById(R.id.ImportEdit);
			textView.setText(data);
		}
		catch (Exception e) {
		}
	}

	private void showHelp() {
		String title = getResources().getString(R.string.import_dialog_title);
		String message = getResources().getString(
				R.string.import_dialog_message);
		new AlertDialog.Builder(this)
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
		EditText editText = (EditText) findViewById(R.id.ImportEdit);
		String text = editText.getText().toString();
		if (text == null || "".equals(text)) {
			Toast.makeText(this, getString(R.string.import_no_text),
					Toast.LENGTH_SHORT).show();
		}
		else {
			List<Reading> readings = ReadingsSerializer.parse(text);
			int count = readings.size();
			if (count > 0) {
				MainModel mainModel = new MainModel(this);
				try {
					Synchronization sync = new Synchronization(
							mainModel.getReverseOrderedReadings());
					sync.Merge(readings);
					mainModel.getDatabase().mergeReadings(sync._Readings);
				}
				finally {
					mainModel.close();
				}

				setResult(RESULT_OK);
			}
			Toast.makeText(
					this,
					String.valueOf(count)
							+ getString(R.string.import_readings_added),
					Toast.LENGTH_SHORT).show();
		}
		this.finish();
	}
}
