package com.pigdogbay.weightrecorder.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.pigdogbay.lib.utils.ActivityUtils;
import com.pigdogbay.weightrecorder.model.MainModel;
import com.pigdogbay.weightrecorder.model.Reading;
import com.pigdogbay.weightrecorder.model.ReadingsSerializer;
import com.pigdogbay.weightrecorder.model.Synchronization;
import com.pigdogbay.weighttrackerpro.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
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
            ActivityUtils.shareText(activity, activity.getString(R.string.app_name), text, R.string.share_readings_chooser_title);
        } catch (Exception e) {
            Toast.makeText(activity,
                    activity.getString(R.string.readings_export_error),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static int mergeReadings(Activity activity, String data) {
        List<Reading> readings = ReadingsSerializer.parse(data);
        int count = readings.size();
        if (count > 0) {
            MainModel mainModel = new MainModel(activity);
            try {
                Synchronization sync = new Synchronization(
                        mainModel.getReverseOrderedReadings());
                sync.Merge(readings);
                mainModel.getDatabase().mergeReadings(sync._Readings);
            } finally {
                mainModel.close();
            }
        }
        return count;
    }

    /**
     * Sharing Images using cache
     *
     * Also need
     * xml/filepaths.xml
     * AndroidManifest - provider
     * See
     * http://stackoverflow.com/questions/9049143/android-share-intent-for-a-bitmap-is-it-possible-not-to-save-it-prior-sharing
     */
    private static final String IMAGE_CACHE_DIR = "images";
    private static final String IMAGE_CACHE_FILE_NAME = "image.png";

    public static void saveToCache(Context context, Bitmap bitmap) throws IOException {
        File cachePath = new File(context.getCacheDir(), IMAGE_CACHE_DIR);
        File filePath = new File(cachePath, IMAGE_CACHE_FILE_NAME);
        if (cachePath.mkdirs()) {
            FileOutputStream stream = new FileOutputStream(filePath); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
        }
    }

    public static void shareCacheImage(Activity activity, int chooseTitle) {
        File imagePath = new File(activity.getCacheDir(), IMAGE_CACHE_DIR);
        File newFile = new File(imagePath, IMAGE_CACHE_FILE_NAME);
        Uri contentUri = FileProvider.getUriForFile(activity, activity.getString(R.string.image_cache_fileprovider), newFile);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, activity.getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.app_name));
            shareIntent.putExtra(Intent.EXTRA_TEXT, new Date().toString());
            shareIntent.setType("image/png");
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            activity.startActivity(Intent.createChooser(shareIntent, activity.getString(chooseTitle)));
        }
    }
}
