package com.pigdogbay.weighttrackerpro;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.pigdogbay.lib.utils.FileUtils;
import com.pigdogbay.weightrecorder.model.MainModel;
import com.pigdogbay.weightrecorder.model.Reading;
import com.pigdogbay.weightrecorder.model.ReadingsSerializer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DriveFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = "drive";
    private static final int REQUEST_CODE_RESOLUTION = 3;
    private static final String FILENAME_PREFIX = "readings";
    private static final String FILENAME_EXTENSION = ".csv";
    private static final String FOLDER_NAME = "WeightTracker";
    private static final String MIME_TYPE = "text/csv";
    private static final int MAX_RETRIES = 3;

    private GoogleApiClient googleApiClient;
    private TextView statusTextView;
    private int resolutionCounter;
    private boolean isTaskRunning;

    public DriveFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drive, container, false);
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (supportActionBar!=null){
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        setHasOptionsMenu(true);
        wireUpControls(view);
        resolutionCounter = 0;
        isTaskRunning = false;
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_drive, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.menu_drive_delete):
                deleteFolder();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void wireUpControls(View view) {
        view.findViewById(R.id.driveSaveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        view.findViewById(R.id.driveRestoreBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restore();
            }
        });
        view.findViewById(R.id.driveQueryBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query();
            }
        });
        statusTextView = (TextView) view.findViewById(R.id.driveStatus);
    }


    @Override
    public void onResume() {
        super.onResume();

        statusTextView.setText(R.string.drive_status_connecting);
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addOnConnectionFailedListener(this)
                    .addConnectionCallbacks(this)
                    .build();
        }
        googleApiClient.connect();

    }

    @Override
    public void onPause() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        query();
    }

    @Override
    public void onConnectionSuspended(int i) {
        statusTextView.setText(R.string.drive_status_disconnected);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        statusTextView.setText(R.string.drive_status_disconnected);
        resolutionCounter++;
        if (resolutionCounter > MAX_RETRIES) {
            return;
        }
        // Called whenever the API client fails to connect.
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), result.getErrorCode(), 0).show();
            return;
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an
        // authorization
        // dialog is displayed to the user.
        try {
            result.startResolutionForResult(getActivity(), REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_RESOLUTION:
                break;
        }
    }

    private boolean checkIfConnected() {
        if (isTaskRunning) {
            Toast.makeText(getContext(), R.string.drive_toast_busy, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (googleApiClient != null && googleApiClient.isConnected()) {
            return true;
        }
        Toast.makeText(getContext(), R.string.drive_toast_not_connected, Toast.LENGTH_SHORT).show();
        return false;
    }

    private void query() {
        if (checkIfConnected()) {
            statusTextView.setText(R.string.drive_status_refreshing);
            QueryAsyncTask queryAsyncTask = new QueryAsyncTask();
            queryAsyncTask.execute();
        }
    }

    private void save() {
        if (checkIfConnected()) {
            statusTextView.setText(R.string.drive_status_saving);
            SaveAsyncTask saveAsyncTask = new SaveAsyncTask();
            saveAsyncTask.execute();
        }
    }

    private void restore() {
        if (checkIfConnected()) {
            statusTextView.setText(R.string.drive_status_restoring);
            RestoreAsyncTask restoreAsyncTask = new RestoreAsyncTask();
            restoreAsyncTask.execute();
        }
    }

    private void deleteFolder() {
        if (!checkIfConnected()) {
            return;
        }
        String title = getResources().getString(R.string.editreading_delete_dialog_title);
        String message = getResources().getString(R.string.drive_delete_dialog_message);

        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        })
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                statusTextView.setText(R.string.drive_status_deleting);
                                DeleteFolderAsyncTask deleteAsyncTask = new DeleteFolderAsyncTask();
                                deleteAsyncTask.execute(FOLDER_NAME);
                                dialog.dismiss();
                            }
                        }).show();


    }


    private DriveId find(String title) {
        Query query = new Query.Builder().addFilter(Filters.and(
                Filters.eq(SearchableField.TITLE, title),
                Filters.eq(SearchableField.TRASHED, false)
        )).build();
        DriveApi.MetadataBufferResult metadataBufferResult = Drive.DriveApi.query(googleApiClient, query).await();
        com.google.android.gms.common.api.Status queryStatus = metadataBufferResult.getStatus();
        if (!queryStatus.isSuccess()) {
            return null;
        }
        if (metadataBufferResult.getMetadataBuffer().getCount() == 0) {
            return null;
        }
        return metadataBufferResult.getMetadataBuffer().get(0).getDriveId();
    }

    private List<Metadata> findFiles(DriveFolder folder, String contains) {
        List<Metadata> files = new ArrayList<>();
        Query query = new Query.Builder().addFilter(Filters.and(
                Filters.contains(SearchableField.TITLE, contains),
                Filters.eq(SearchableField.TRASHED, false)
        )).build();

        DriveApi.MetadataBufferResult metadataBufferResult = folder.queryChildren(googleApiClient, query).await();
        if (metadataBufferResult.getStatus().isSuccess()) {
            for (Metadata metadata : metadataBufferResult.getMetadataBuffer()) {
                if (!metadata.isFolder()) {
                    files.add(metadata);
                }
            }
        }
        return files;
    }

    private void sortFilesByData(List<Metadata> files) {
        if (files.size() > 0) {
            Collections.sort(files, new Comparator<Metadata>() {
                @Override
                public int compare(Metadata metadata, Metadata t1) {
                    return metadata.getCreatedDate().compareTo(t1.getCreatedDate());
                }
            });
        }
    }

    private DriveFile findFile(String title) {
        DriveId driveId = find(title);
        if (driveId != null) {
            return driveId.asDriveFile();
        }
        return null;
    }

    private DriveFolder findFolder(String title) {
        DriveId driveId = find(title);
        if (driveId != null) {
            return driveId.asDriveFolder();
        }
        return null;
    }

    private Metadata getLatest() {
        DriveFolder driveFolder = findFolder(FOLDER_NAME);
        if (driveFolder != null) {
            List<Metadata> files = findFiles(driveFolder, FILENAME_PREFIX);
            if (files.size() > 0) {
                sortFilesByData(files);
                return files.get(files.size() - 1);
            }
        }
        return null;
    }

    private DriveFolder createOpenFolder(String name) {
        DriveFolder driveFolder = findFolder(name);
        if (driveFolder != null) {
            //folder already exists
            return driveFolder;
        }
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(name)
                .build();
        DriveFolder.DriveFolderResult driveFolderResult = Drive.DriveApi.getRootFolder(googleApiClient).createFolder(googleApiClient, changeSet).await();
        if (driveFolderResult.getStatus().isSuccess()) {
            return driveFolderResult.getDriveFolder();
        }
        return null;
    }

    private DriveFile createOpenFile(DriveFolder folder, String name, String mimeType) {
        DriveFile driveFile = findFile(name);
        if (driveFile != null) {
            return driveFile;
        }
        //create a new file
        DriveApi.DriveContentsResult result = Drive.DriveApi.newDriveContents(googleApiClient).await();
        if (result.getStatus().isSuccess()) {
            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                    .setTitle(name)
                    .setMimeType(mimeType)
                    .build();

            DriveFolder.DriveFileResult createFileResult = folder
                    .createFile(googleApiClient, changeSet, null)
                    .await();

            if (createFileResult.getStatus().isSuccess()) {
                return createFileResult.getDriveFile();
            }
        }
        return null;
    }

    private String readingsToString(Activity activity) {
        MainModel mainModel = new MainModel(activity);
        List<Reading> readings = mainModel.getReverseOrderedReadings();
        mainModel.close();
        if (readings.size() == 0) {
            return "";
        }
        return ReadingsSerializer.format(readings);
    }

    private boolean writeString(DriveFile driveFile, String data) throws IOException {
        DriveApi.DriveContentsResult openResult = driveFile.open(googleApiClient, DriveFile.MODE_WRITE_ONLY, null).await();
        if (openResult.getStatus().isSuccess()) {
            DriveContents driveContents = openResult.getDriveContents();
            OutputStream outputStream = driveContents.getOutputStream();
            Writer writer = new OutputStreamWriter(outputStream);
            writer.write(data);
            writer.close();
            com.google.android.gms.common.api.Status commitStatus = driveContents.commit(googleApiClient, null).await();
            return commitStatus.isSuccess();
        }
        return false;
    }

    private String readString(DriveFile driveFile) throws IOException {
        DriveApi.DriveContentsResult openResult = driveFile.open(googleApiClient, DriveFile.MODE_READ_ONLY, null).await();
        if (openResult.getStatus().isSuccess()) {
            DriveContents driveContents = openResult.getDriveContents();
            Reader reader = new InputStreamReader(driveContents.getInputStream());
            return FileUtils.readText(reader);
        }
        return "";
    }

    private class DeleteFolderAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... fileNames) {
            isTaskRunning = true;
            DriveFolder driveFolder = findFolder(fileNames[0]);
            if (driveFolder != null) {
                com.google.android.gms.common.api.Status deleteStatus = driveFolder.delete(googleApiClient).await();
                if (deleteStatus.isSuccess()) {
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            isTaskRunning = false;
            isTaskRunning = false;
            int msg = aBoolean ? R.string.drive_status_delete_folder_success : R.string.drive_status_delete_folder_failed;
            statusTextView.setText(msg);
        }
    }

    private class SaveAsyncTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            isTaskRunning = true;
            String data = readingsToString(getActivity());
            if (data.equals("")) {
                return R.string.drive_status_save_no_readings;
            }

            DriveFolder driveFolder = createOpenFolder(FOLDER_NAME);
            if (driveFolder == null) {
                return R.string.drive_status_save_create_folder_failed;
            }
            String fileName = FileUtils.appendDate(FILENAME_PREFIX, FILENAME_EXTENSION);
            DriveFile driveFile = createOpenFile(driveFolder, fileName, MIME_TYPE);
            if (driveFile == null) {
                return R.string.drive_status_save_create_file_failed;
            }
            try {
                return writeString(driveFile, data) ? R.string.drive_status_save_success : R.string.drive_status_save_write_failed;
            } catch (IOException e) {
                e.printStackTrace();
                return R.string.drive_status_save_write_failed;
            }
        }

        @Override
        protected void onPostExecute(Integer id) {
            super.onPostExecute(id);
            isTaskRunning = false;
            statusTextView.setText(id);
        }
    }

    private class RestoreAsyncTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            isTaskRunning = true;
            Metadata latest = getLatest();
            if (latest == null) {
                return R.string.drive_status_restore_no_files;
            }

                try {
                    String readings = readString(latest.getDriveId().asDriveFile());
                    ActivitiesHelper.mergeReadings(getActivity(), readings);
                    return R.string.drive_status_restore_succuess;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            return R.string.drive_status_restore_load_error;

        }

        @Override
        protected void onPostExecute(Integer id) {
            super.onPostExecute(id);
            isTaskRunning = false;
            statusTextView.setText(id);
        }
    }

    private class QueryAsyncTask extends AsyncTask<Void, Void, Metadata> {

        @Override
        protected Metadata doInBackground(Void... voids) {
            isTaskRunning = true;
            return getLatest();
        }

        @Override
        protected void onPostExecute(Metadata latest) {
            super.onPostExecute(latest);
            isTaskRunning = false;
            if (latest == null) {
                statusTextView.setText(R.string.drive_status_no_files_found);
            } else {
                String dateString = SimpleDateFormat.getDateTimeInstance().format(latest.getModifiedDate());
                statusTextView.setText(String.format(getString(R.string.drive_status_latest), dateString));
            }
        }
    }

}
