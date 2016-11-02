package com.pigdogbay.weighttrackerpro;


import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Scanner;


/**
 * A simple {@link Fragment} subclass.
 */
public class DriveFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = "drive";
    private static final int REQUEST_CODE_RESOLUTION = 3;

    private GoogleApiClient googleApiClient;
    private TextView statusTextView;

    private static final String FILENAME = "WeightTracker.csv";
    private static final String FOLDER_NAME = "WeightTracker";

    private String readString="";

    public DriveFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drive, container, false);
        wireUpControls(view);
        return view;
    }
    private void wireUpControls(View view) {
        view.findViewById(R.id.driveQueryBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryFiles();
            }
        });
        view.findViewById(R.id.driveCreateBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createFile();
            }
        });
        view.findViewById(R.id.driveReadBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                read();
            }
        });
        view.findViewById(R.id.driveDeleteBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete();
            }
        });
        view.findViewById(R.id.driveFolderBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createFolder();
            }
        });
        view.findViewById(R.id.driveDeleteFolderBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFolder();
            }
        });
        statusTextView = (TextView)view.findViewById(R.id.driveStatus);
    }


    @Override
    public void onResume() {
        super.onResume();

        if (googleApiClient==null){
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
        Log.i(TAG,"Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG,"Connection Suspended");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Called whenever the API client fails to connect.
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
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
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_RESOLUTION:
                Log.i(TAG,"Resolution Result");
                break;
        }
    }

    private void queryFiles(){
        statusTextView.setText("Querying...");
        Query query = new Query.Builder().addFilter(Filters.and(
                Filters.contains(SearchableField.TITLE, "WeightTracker")
        )).build();

        Drive.DriveApi.query(googleApiClient,query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(@NonNull DriveApi.MetadataBufferResult metadataBufferResult) {
                Status status =  metadataBufferResult.getStatus();
                statusTextView.setText("Files Found: "+String.valueOf(metadataBufferResult.getMetadataBuffer().getCount()));
                if (status.isSuccess()) {
                    for (Metadata metadata : metadataBufferResult.getMetadataBuffer()) {
                        Log.i(TAG, metadata.getTitle());
                    }
                }
            }
        });

    }


    private void delete(){
        statusTextView.setText("Deleting...");
        DeleteAsyncTask deleteAsyncTask = new DeleteAsyncTask();
        deleteAsyncTask.execute(FILENAME);
    }
    private void deleteFolder(){
        statusTextView.setText("Deleting Folder...");
        DeleteFolderAsyncTask deleteAsyncTask = new DeleteFolderAsyncTask();
        deleteAsyncTask.execute(FOLDER_NAME);
    }
    private void createFile(){
        statusTextView.setText("Writing...");
        WriteAsyncTask writeAsyncTask = new WriteAsyncTask();
        writeAsyncTask.execute(FILENAME);
    }

    private void read(){
        statusTextView.setText("Reading...");
        ReadAsyncTask readAsyncTask = new ReadAsyncTask();
        readAsyncTask.execute(FILENAME);
    }

    private void createFolder(){
        statusTextView.setText("Creating Folder...");
        CreateFolderAsyncTask createFolderAsyncTask = new CreateFolderAsyncTask();
        createFolderAsyncTask.execute(FOLDER_NAME);

    }

    private DriveId find(String title){
        Query query = new Query.Builder().addFilter(Filters.and(
                Filters.eq(SearchableField.TITLE, title),
                Filters.eq(SearchableField.TRASHED,false)
        )).build();
        DriveApi.MetadataBufferResult metadataBufferResult= Drive.DriveApi.query(googleApiClient,query).await();
        com.google.android.gms.common.api.Status queryStatus =  metadataBufferResult.getStatus();
        if (!queryStatus.isSuccess()){
            return null;
        }
        if (metadataBufferResult.getMetadataBuffer().getCount()==0){
            return null;
        }
        return metadataBufferResult.getMetadataBuffer().get(0).getDriveId();
    }

    private DriveFile findFile(String title){
        DriveId driveId = find(title);
        if (driveId!=null){
            return driveId.asDriveFile();
        }
        return null;
    }
    private DriveFolder findFolder(String title){
        DriveId driveId = find(title);
        if (driveId!=null){
            return driveId.asDriveFolder();
        }
        return null;
    }

    private void writeDate(DriveContents driveContents) throws IOException {
        OutputStream outputStream = driveContents.getOutputStream();
        Writer writer = new OutputStreamWriter(outputStream);
        String message = new Date().toString();
        writer.write(message);
        writer.close();
    }
    private class CreateFolderAsyncTask extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {

            DriveFolder driveFolder = findFolder(strings[0]);
            if (driveFolder!=null) {
                //folder already exists
                return Boolean.TRUE;
            }
            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(strings[0])
                .build();
            DriveFolder.DriveFolderResult driveFolderResult = Drive.DriveApi.getRootFolder(googleApiClient).createFolder(googleApiClient, changeSet).await();
            return driveFolderResult.getStatus().isSuccess() ? Boolean.TRUE : Boolean.FALSE;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            String status = aBoolean ? "Folder - Success" : "Folder - Failed";
            statusTextView.setText(status);
        }
    }

    private class WriteAsyncTask extends AsyncTask<String, Void, Boolean>{
        @Override
        protected Boolean doInBackground(String... fileNames) {
            DriveFile driveFile = findFile(fileNames[0]);
            if (driveFile==null) {
                //create a new file
                DriveApi.DriveContentsResult result = Drive.DriveApi.newDriveContents(googleApiClient).await();
                if (result.getStatus().isSuccess()) {

                    DriveContents driveContents = result.getDriveContents();
                    try {
                        writeDate(driveContents);
                    } catch (IOException e) {
                        return Boolean.FALSE;
                    }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(FILENAME)
                            .setMimeType("text/csv")
                            .build();

                    // create a file on root folder
                    DriveFolder.DriveFileResult createFileResult = Drive.DriveApi.getRootFolder(googleApiClient)
                            .createFile(googleApiClient, changeSet, driveContents)
                            .await();

                    if (createFileResult.getStatus().isSuccess())
                    {
                        return Boolean.TRUE;
                    }

                }
            }
            else
            {
                //overwrite file contents
                DriveApi.DriveContentsResult openResult = driveFile.open(googleApiClient, DriveFile.MODE_WRITE_ONLY, null).await();
                if (openResult.getStatus().isSuccess()){
                    DriveContents driveContents = openResult.getDriveContents();
                    try {
                        writeDate(driveContents);
                    } catch (IOException e) {
                        return Boolean.FALSE;
                    }
                    com.google.android.gms.common.api.Status commitStatus = driveContents.commit(googleApiClient, null).await();
                    if (commitStatus.isSuccess()){
                        return Boolean.TRUE;
                    }
                }
            }
            return Boolean.FALSE;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            String status = aBoolean ? "Write - Success" : "Write - Failed";
            statusTextView.setText(status);
        }

    }
    private class ReadAsyncTask extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... fileNames) {
            DriveFile driveFile = findFile(fileNames[0]);
            if (driveFile!=null) {
                DriveApi.DriveContentsResult openResult = driveFile.open(googleApiClient, DriveFile.MODE_READ_ONLY, null).await();
                if (openResult.getStatus().isSuccess()) {
                    DriveContents driveContents = openResult.getDriveContents();
                    Scanner scanner = new Scanner(driveContents.getInputStream());
                    while(scanner.hasNextLine()){
                        readString = scanner.nextLine();
                    }
                    return  Boolean.TRUE;
                }

            }
            return Boolean.FALSE;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean){
                statusTextView.setText(readString);

            } else {
                statusTextView.setText("Read - Failed");
            }
        }
    }

    private class DeleteAsyncTask extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... fileNames) {
            DriveFile driveFile = findFile(fileNames[0]);
            if (driveFile!=null) {
                com.google.android.gms.common.api.Status deleteStatus = driveFile.delete(googleApiClient).await();
                if (deleteStatus.isSuccess()) {
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            String status = aBoolean ? "Delete - Success" : "Delete - Failed";
            statusTextView.setText(status);
        }
    }
    private class DeleteFolderAsyncTask extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... fileNames) {
            DriveFolder driveFolder = findFolder(fileNames[0]);
            if (driveFolder!=null) {
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
            String status = aBoolean ? "Delete Folder - Success" : "Delete Folder- Failed";
            statusTextView.setText(status);
        }
    }

}
