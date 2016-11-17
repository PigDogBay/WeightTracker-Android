package com.pigdogbay.weightrecorder.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.pigdogbay.lib.utils.ActivityUtils;
import com.pigdogbay.weightrecorder.model.MainModel;
import com.pigdogbay.weightrecorder.model.Reading;
import com.pigdogbay.weightrecorder.model.ReadingsSerializer;
import com.pigdogbay.weightrecorder.model.Synchronization;
import com.pigdogbay.weighttrackerpro.R;

import java.io.File;
import java.util.List;

public class ActivitiesHelper {

	public static void showInfoDialog(Context context, int titleID,
			int messageID) {
		String title = context.getResources().getString(titleID);
		String message = context.getResources().getString(messageID);
		new AlertDialog.Builder(context)
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

	public static void shareReadings(Activity activity) {
		try {
			MainModel mainModel = new MainModel(activity);
			List<Reading> readings = mainModel.getReverseOrderedReadings();
			mainModel.close();
			if (readings.size() == 0) {
				Toast.makeText(
						activity,
						activity.getString(R.string.readings_no_readings_export),
						Toast.LENGTH_SHORT).show();
				return;

			}
			String text = ReadingsSerializer.format(readings);
			ActivityUtils.shareText(activity, activity.getString(R.string.app_name),text, R.string.share_readings_chooser_title);
		}
		catch (Exception e) {
			Toast.makeText(activity,
					activity.getString(R.string.readings_export_error),
					Toast.LENGTH_SHORT).show();
		}
	}

	public static int mergeReadings(Activity activity, String data){
		List<Reading> readings = ReadingsSerializer.parse(data);
		int count = readings.size();
		if (count > 0) {
			MainModel mainModel = new MainModel(activity);
			try {
				Synchronization sync = new Synchronization(
						mainModel.getReverseOrderedReadings());
				sync.Merge(readings);
				mainModel.getDatabase().mergeReadings(sync._Readings);
			}
			finally {
				mainModel.close();
			}
		}
		return count;
	}

	public static void SendFile(Activity activity, File file, String type, int chooserTitleID) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType(type);
		i.putExtra(Intent.EXTRA_EMAIL, "");
		i.putExtra(Intent.EXTRA_SUBJECT, file.getName());
		i.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.facebookPage));
		Uri uri = Uri.fromFile(file);
		i.putExtra(Intent.EXTRA_STREAM, uri);
		try {
			activity.startActivity(Intent.createChooser(i,
					activity.getString(chooserTitleID)));
		}
		catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(activity,
					activity.getString(R.string.about_no_market_app),
					Toast.LENGTH_SHORT).show();
		}
	}
}
