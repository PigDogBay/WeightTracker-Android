package com.pigdogbay.weightrecorder.utils;

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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Mark on 11/11/2016.
 * Facade to Google Drive, simplifies basic operations.
 */
class DriveUtils
{
    private final GoogleApiClient googleApiClient;

    DriveUtils(GoogleApiClient googleApiClient){

        this.googleApiClient = googleApiClient;
    }

    DriveId find(String title)
    {
        Query query = new Query.Builder().addFilter(Filters.and(
                Filters.eq(SearchableField.TITLE, title),
                Filters.eq(SearchableField.TRASHED, false)
        )).build();
        DriveApi.MetadataBufferResult metadataBufferResult = Drive.DriveApi.query(googleApiClient, query).await();
        com.google.android.gms.common.api.Status queryStatus = metadataBufferResult.getStatus();
        if (!queryStatus.isSuccess())
        {
            return null;
        }
        if (metadataBufferResult.getMetadataBuffer().getCount() == 0)
        {
            return null;
        }
        return metadataBufferResult.getMetadataBuffer().get(0).getDriveId();
    }

    List<Metadata> findFiles(DriveFolder folder, String contains)
    {
        List<Metadata> files = new ArrayList<>();
        Query query = new Query.Builder().addFilter(Filters.and(
                Filters.contains(SearchableField.TITLE, contains),
                Filters.eq(SearchableField.TRASHED, false)
        )).build();

        DriveApi.MetadataBufferResult metadataBufferResult = folder.queryChildren(googleApiClient, query).await();
        if (metadataBufferResult.getStatus().isSuccess())
        {
            for (Metadata metadata : metadataBufferResult.getMetadataBuffer())
            {
                if (!metadata.isFolder())
                {
                    files.add(metadata);
                }
            }
        }
        return files;
    }

    private void sortFilesByData(List<Metadata> files)
    {
        if (files.size() > 0)
        {
            Collections.sort(files, new Comparator<Metadata>()
            {
                @Override
                public int compare(Metadata metadata, Metadata t1)
                {
                    return metadata.getCreatedDate().compareTo(t1.getCreatedDate());
                }
            });
        }
    }

    DriveFile findFile(String title)
    {
        DriveId driveId = find(title);
        if (driveId != null)
        {
            return driveId.asDriveFile();
        }
        return null;
    }

    DriveFolder findFolder(String title)
    {
        DriveId driveId = find(title);
        if (driveId != null)
        {
            return driveId.asDriveFolder();
        }
        return null;
    }

    Metadata getLatest(String folderName, String fileName)
    {
        DriveFolder driveFolder = findFolder(folderName);
        if (driveFolder != null)
        {
            List<Metadata> files = findFiles(driveFolder, fileName);
            if (files.size() > 0)
            {
                sortFilesByData(files);
                return files.get(files.size() - 1);
            }
        }
        return null;
    }

    DriveFolder createOpenFolder(String name)
    {
        DriveFolder driveFolder = findFolder(name);
        if (driveFolder != null)
        {
            //folder already exists
            return driveFolder;
        }
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(name)
                .build();
        DriveFolder.DriveFolderResult driveFolderResult = Drive.DriveApi.getRootFolder(googleApiClient).createFolder(googleApiClient, changeSet).await();
        if (driveFolderResult.getStatus().isSuccess())
        {
            return driveFolderResult.getDriveFolder();
        }
        return null;
    }

    DriveFile createOpenFile(DriveFolder folder, String name, String mimeType)
    {
        DriveFile driveFile = findFile(name);
        if (driveFile != null)
        {
            return driveFile;
        }
        //create a new file
        DriveApi.DriveContentsResult result = Drive.DriveApi.newDriveContents(googleApiClient).await();
        if (result.getStatus().isSuccess())
        {
            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                    .setTitle(name)
                    .setMimeType(mimeType)
                    .build();

            DriveFolder.DriveFileResult createFileResult = folder
                    .createFile(googleApiClient, changeSet, null)
                    .await();

            if (createFileResult.getStatus().isSuccess())
            {
                return createFileResult.getDriveFile();
            }
        }
        return null;
    }


    boolean writeString(DriveFile driveFile, String data) throws IOException
    {
        DriveApi.DriveContentsResult openResult = driveFile.open(googleApiClient, DriveFile.MODE_WRITE_ONLY, null).await();
        if (openResult.getStatus().isSuccess())
        {
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

    String readString(DriveFile driveFile) throws IOException
    {
        DriveApi.DriveContentsResult openResult = driveFile.open(googleApiClient, DriveFile.MODE_READ_ONLY, null).await();
        if (openResult.getStatus().isSuccess())
        {
            DriveContents driveContents = openResult.getDriveContents();
            Reader reader = new InputStreamReader(driveContents.getInputStream());
            return FileUtils.readText(reader);
        }
        return "";
    }

}
