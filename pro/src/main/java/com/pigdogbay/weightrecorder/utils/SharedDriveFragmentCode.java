package com.pigdogbay.weightrecorder.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;
import com.pigdogbay.lib.utils.FileUtils;
import com.pigdogbay.weightrecorder.model.MainModel;
import com.pigdogbay.weightrecorder.model.Reading;
import com.pigdogbay.weightrecorder.model.ReadingsSerializer;
import com.pigdogbay.weighttrackerpro.MainActivity;
import com.pigdogbay.weighttrackerpro.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Mark on 11/11/2016.
 * Handles the Google Drive operations. Used by ConnectToDriveFragment and DriveFragment
 */
public class SharedDriveFragmentCode implements GoogleApiClient.OnConnectionFailedListener
{
    private static final int REQUEST_CODE_RESOLUTION = 3;
    private static final int MAX_RETRIES = 3;

    private static final String FILENAME_PREFIX = "readings";
    private static final String FILENAME_EXTENSION = ".csv";
    private static final String FOLDER_NAME = "WeightTracker";
    private static final String MIME_TYPE = "text/csv";

    private final Activity activity;
    private int resolutionCounter, onActivityResultCounter;
    private boolean isTaskRunning;
    private GoogleApiClient googleApiClient;
    private TextView statusTextView;

    public void setStatusTextView(TextView statusTextView) {
        this.statusTextView = statusTextView;
    }

    public SharedDriveFragmentCode(Activity activity){
        this.activity = activity;
        resolutionCounter = 0;
        onActivityResultCounter = 0;
        isTaskRunning = false;

    }

    private void setText(int stringId){
        if (statusTextView!=null){
            statusTextView.setText(stringId);
        }
    }

    public void disconnect(){
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }

    }

    public boolean isConnected(){
        return googleApiClient!=null && googleApiClient.isConnected();
    }

    public void connect(GoogleApiClient.ConnectionCallbacks connectionCallbacks){
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(activity)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addOnConnectionFailedListener(this)
                    .addConnectionCallbacks(connectionCallbacks)
                    .build();
        }
        if (!googleApiClient.isConnecting() && !googleApiClient.isConnected()) {
            setText(R.string.drive_status_connecting);
            googleApiClient.connect();
        }

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        setText(R.string.drive_status_disconnected);
        resolutionCounter++;
        if (resolutionCounter > MAX_RETRIES) {
            return;
        }
        // Called whenever the API client fails to connect.
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(activity, result.getErrorCode(), 0).show();
            return;
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an
        // authorization
        // dialog is displayed to the user.
        try {
            result.startResolutionForResult(activity, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }

    }

    public void onActivityResult(int requestCode, int resultCode, GoogleApiClient.ConnectionCallbacks connectionCallbacks) {

        //Only try to connect once
        onActivityResultCounter++;
        if (onActivityResultCounter>1){return;}

        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case SharedDriveFragmentCode.REQUEST_CODE_RESOLUTION:
                    //try to connect again
                    connect(connectionCallbacks);
                    break;
            }
        }

    }

    public void onConnected() {
        setText(R.string.drive_status_connected);
        //set a flag to indicate drive has connected
        //if the drive has connected at least once, it is then worth trying to backup data in the future
        MainModel mainModel = new MainModel(activity);
        mainModel.setDriveConnected(true);
    }

    public void onConnectionSuspended() {
        setText(R.string.drive_status_disconnected);
    }

    private boolean checkIfConnected()
    {
        if (isTaskRunning)
        {
            Toast.makeText(activity, R.string.drive_toast_busy, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (googleApiClient != null && googleApiClient.isConnected())
        {
            return true;
        }
        Toast.makeText(activity, R.string.drive_toast_not_connected, Toast.LENGTH_SHORT).show();
        return false;
    }

    public void query()
    {
        if (checkIfConnected())
        {
            setText(R.string.drive_status_refreshing);
            QueryAsyncTask queryAsyncTask = new QueryAsyncTask();
            queryAsyncTask.execute();
        }
    }

    public void save()
    {
        if (checkIfConnected())
        {
            setText(R.string.drive_status_saving);
            SaveAsyncTask saveAsyncTask = new SaveAsyncTask();
            saveAsyncTask.execute();
        }
    }

    public void restore()
    {
        if (checkIfConnected())
        {
            setText(R.string.drive_status_restoring);
            RestoreAsyncTask restoreAsyncTask = new RestoreAsyncTask();
            restoreAsyncTask.execute();
        }
    }

    /**
     * Quietly save readings and disconnect when done
     */
    void autoSave(){
        AutoSaveAsyncTask autoSaveAsyncTask = new AutoSaveAsyncTask();
        autoSaveAsyncTask.execute();
    }

    public void deleteFolder()
    {
        if (!checkIfConnected())
        {
            return;
        }
        String title = activity.getResources().getString(R.string.editreading_delete_dialog_title);
        String message = activity.getResources().getString(R.string.drive_delete_dialog_message);

        new AlertDialog.Builder(activity)
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
                                setText(R.string.drive_status_deleting);
                                DeleteFolderAsyncTask deleteAsyncTask = new DeleteFolderAsyncTask();
                                deleteAsyncTask.execute(FOLDER_NAME);
                                dialog.dismiss();
                            }
                        }).show();


    }



    private String readingsToString(Activity activity)
    {
        MainModel mainModel = new MainModel(activity);
        List<Reading> readings = mainModel.getReverseOrderedReadings();
        mainModel.close();
        if (readings.size() == 0)
        {
            return "";
        }
        return ReadingsSerializer.format(readings);
    }


    private class DeleteFolderAsyncTask extends AsyncTask<String, Void, Boolean>
    {

        @Override
        protected Boolean doInBackground(String... fileNames)
        {
            isTaskRunning = true;
            DriveUtils driveUtils = new DriveUtils(googleApiClient);
            DriveFolder driveFolder = driveUtils.findFolder(fileNames[0]);
            if (driveFolder != null)
            {
                com.google.android.gms.common.api.Status deleteStatus = driveFolder.delete(googleApiClient).await();
                if (deleteStatus.isSuccess())
                {
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            super.onPostExecute(aBoolean);
            isTaskRunning = false;
            isTaskRunning = false;
            int msg = aBoolean ? R.string.drive_status_delete_folder_success : R.string.drive_status_delete_folder_failed;
            setText(msg);
        }
    }

    private class SaveAsyncTask extends AsyncTask<Void, Void, Integer>
    {

        @Override
        protected Integer doInBackground(Void... voids)
        {
            isTaskRunning = true;
            DriveUtils driveUtils = new DriveUtils(googleApiClient);
            String data = readingsToString(activity);
            if (data.equals(""))
            {
                return R.string.drive_status_save_no_readings;
            }

            DriveFolder driveFolder = driveUtils.createOpenFolder(FOLDER_NAME);
            if (driveFolder == null)
            {
                return R.string.drive_status_save_create_folder_failed;
            }
            String fileName = FileUtils.appendDate(FILENAME_PREFIX, FILENAME_EXTENSION);
            DriveFile driveFile = driveUtils.createOpenFile(driveFolder, fileName, MIME_TYPE);
            if (driveFile == null)
            {
                return R.string.drive_status_save_create_file_failed;
            }
            try
            {
                return driveUtils.writeString(driveFile, data) ? R.string.drive_status_save_success : R.string.drive_status_save_write_failed;
            } catch (IOException e)
            {
                e.printStackTrace();
                return R.string.drive_status_save_write_failed;
            }
        }

        @Override
        protected void onPostExecute(Integer id)
        {
            super.onPostExecute(id);
            isTaskRunning = false;
            setText(id);
        }
    }

    /**
     * Saves in the background so don't report back and close the connection afterwards
     */
    private class AutoSaveAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Log.v(MainActivity.TAG,"Backing up readings");
            DriveUtils driveUtils = new DriveUtils(googleApiClient);
            String data = readingsToString(activity);
            if (!data.equals("")) {
                DriveFolder driveFolder = driveUtils.createOpenFolder(FOLDER_NAME);
                if (driveFolder != null) {
                    String fileName = FileUtils.appendDate(FILENAME_PREFIX, FILENAME_EXTENSION);
                    DriveFile driveFile = driveUtils.createOpenFile(driveFolder, fileName, MIME_TYPE);
                    if (driveFile != null)
                        try {
                            if (driveUtils.writeString(driveFile, data)) {
                                Log.v(MainActivity.TAG,"Backup - success");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.v(MainActivity.TAG,"Backup - disconnecting from drive");
            disconnect();
        }
    }

    private class RestoreAsyncTask extends AsyncTask<Void, Void, Integer>
    {

        @Override
        protected Integer doInBackground(Void... voids)
        {
            isTaskRunning = true;
            DriveUtils driveUtils = new DriveUtils(googleApiClient);
            Metadata latest = driveUtils.getLatest(FOLDER_NAME, FILENAME_PREFIX);
            if (latest == null)
            {
                return R.string.drive_status_restore_no_files;
            }

            try
            {
                String readings = driveUtils.readString(latest.getDriveId().asDriveFile());
                ActivitiesHelper.mergeReadings(activity, readings);
                return R.string.drive_status_restore_succuess;
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            return R.string.drive_status_restore_load_error;

        }

        @Override
        protected void onPostExecute(Integer id)
        {
            super.onPostExecute(id);
            isTaskRunning = false;
            setText(id);
        }
    }

    private class QueryAsyncTask extends AsyncTask<Void, Void, Metadata>
    {

        @Override
        protected Metadata doInBackground(Void... voids)
        {
            isTaskRunning = true;
            DriveUtils driveUtils = new DriveUtils(googleApiClient);
            return driveUtils.getLatest(FOLDER_NAME,FILENAME_PREFIX);
        }

        @Override
        protected void onPostExecute(Metadata latest)
        {
            super.onPostExecute(latest);
            isTaskRunning = false;
            if (latest == null)
            {
                setText(R.string.drive_status_no_files_found);
            } else
            {
                String dateString = SimpleDateFormat.getDateTimeInstance().format(latest.getModifiedDate());
                if (statusTextView!=null) {
                    statusTextView.setText(String.format(activity.getString(R.string.drive_status_latest), dateString));
                }
            }
        }
    }
}
