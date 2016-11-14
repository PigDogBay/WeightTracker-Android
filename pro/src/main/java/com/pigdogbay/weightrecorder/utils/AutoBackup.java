package com.pigdogbay.weightrecorder.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.pigdogbay.weightrecorder.model.MainModel;

import java.util.Date;


public class AutoBackup {

    private static final long WEEKLY_BACKUP_PERIOD_IN_DAYS = 7L;
    private static final long DAY_IN_MILLIS = 24L * 60L * 60L * 1000L;

    public static boolean isBackupDue(Context context) {
        MainModel mainModel = new MainModel(context);
        if (mainModel.getIsAutoBackupEnabled() && mainModel.getDriveConnected()) {
            long now = new Date().getTime();
            long last = mainModel.getBackupDate();
            if (last == 0L) {
                //first time called, set it to today's date
                mainModel.setBackupDate(now);
                return false;
            }
            return ((now - last) > (WEEKLY_BACKUP_PERIOD_IN_DAYS * DAY_IN_MILLIS));
        }
        return false;
    }


    public static void backUpReadings(final Activity activity) {
        final SharedDriveFragmentCode sharedDriveFragmentCode = new SharedDriveFragmentCode(activity);
        sharedDriveFragmentCode.connect(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                //set timestamp to now, don't attempt to save for another week
                MainModel mainModel = new MainModel(activity);
                mainModel.setBackupDate(new Date().getTime());
                sharedDriveFragmentCode.autoSave();
            }

            @Override
            public void onConnectionSuspended(int i) {
            }
        });
    }
}
