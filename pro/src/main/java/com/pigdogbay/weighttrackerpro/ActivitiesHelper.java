package com.pigdogbay.weighttrackerpro;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.pigdogbay.lib.utils.DateStampComparator;
import com.pigdogbay.lib.utils.DateStampedFileFilter;
import com.pigdogbay.lib.utils.FileUtils;
import com.pigdogbay.weightrecorder.model.MainModel;
import com.pigdogbay.weightrecorder.model.Reading;
import com.pigdogbay.weightrecorder.model.ReadingsSerializer;
import com.pigdogbay.weightrecorder.model.Synchronization;

public class ActivitiesHelper {

	private static final String CSV_FILE_EXTENSION = ".csv";

	public static void startImportActivity(Activity activity) {
		Intent intent = new Intent(activity, ImportActivity.class);
		activity.startActivity(intent);
	}

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
			String filename = FileUtils.appendDate(
					activity.getString(R.string.app_name), CSV_FILE_EXTENSION);
			File path = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			File file = new File(path, filename);
			if (file.exists()) {
				file.delete();
			}
			FileUtils.writeTextFile(file, text);
			Toast.makeText(activity, filename, Toast.LENGTH_SHORT).show();
			SendFile(activity, file, "text/plain", R.string.readings_share_chooser_title);

		}
		catch (Exception e) {
			Toast.makeText(activity,
					activity.getString(R.string.readings_export_error),
					Toast.LENGTH_SHORT).show();
		}
	}
	
	public static void backupReadings(Activity activity)
	{
		try {
			MainModel mainModel = new MainModel(activity);
			List<Reading> readings = mainModel.getReverseOrderedReadings();
			mainModel.close();
			if (readings.size() == 0) {
				return;
			}
			String text = ReadingsSerializer.format(readings);
			String filename = FileUtils.appendDate(
					activity.getString(R.string.app_name), CSV_FILE_EXTENSION);
			File path = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			File file = new File(path, filename);
			if (file.exists()) {
				file.delete();
			}
			FileUtils.writeTextFile(file, text);
			Toast.makeText(activity, filename, Toast.LENGTH_SHORT).show();

		}
		catch (Exception e) {
			Toast.makeText(activity,
					activity.getString(R.string.backup_readings_error),
					Toast.LENGTH_SHORT).show();
		}
		
	}
	
	public static File getLatestBackupFile(Activity activity)
	{
		File dir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		File[] matches = dir.listFiles(new DateStampedFileFilter(activity.getString(R.string.app_name), CSV_FILE_EXTENSION));
		if (matches.length>0)
		{
			Arrays.sort(matches, new DateStampComparator());
			return matches[matches.length-1];
		}
		return null;
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

	public static void shareText(Activity activity, String subject, String text, int chooserTitleID) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("text/plain");
		i.putExtra(Intent.EXTRA_EMAIL, "");
		i.putExtra(Intent.EXTRA_SUBJECT, subject);
		i.putExtra(Intent.EXTRA_TEXT, text);
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
