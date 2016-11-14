package com.pigdogbay.weightrecorder.utils;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.pigdogbay.weightrecorder.model.MainModel;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class AutoBackupTest
{
    @Test
    public void isBackupDue1() {
        Context context =  InstrumentationRegistry.getInstrumentation().getTargetContext();
        MainModel mainModel = new MainModel(context);
        mainModel.setIsAutoBackupEnabled(true);
        mainModel.setDriveConnected(true);
        mainModel.setBackupDate(new GregorianCalendar(2015, Calendar.JANUARY,25).getTimeInMillis());
        assertThat(AutoBackup.isBackupDue(context),is(true));
    }
    @Test
    public void isBackupDue2() {
        Context context =  InstrumentationRegistry.getInstrumentation().getTargetContext();
        MainModel mainModel = new MainModel(context);
        mainModel.setIsAutoBackupEnabled(true);
        mainModel.setDriveConnected(true);
        Calendar calendar = Calendar.getInstance();
        calendar.roll(Calendar.DAY_OF_MONTH,-1);
        mainModel.setBackupDate(calendar.getTimeInMillis());
        assertThat(AutoBackup.isBackupDue(context),is(false));
    }
    @Test
    public void isBackupDue3() {
        Context context =  InstrumentationRegistry.getInstrumentation().getTargetContext();
        MainModel mainModel = new MainModel(context);
        mainModel.setIsAutoBackupEnabled(false);
        mainModel.setDriveConnected(true);
        mainModel.setBackupDate(new GregorianCalendar(2015, Calendar.JANUARY,25).getTimeInMillis());
        assertThat(AutoBackup.isBackupDue(context),is(false));
    }
    @Test
    public void isBackupDue4() {
        Context context =  InstrumentationRegistry.getInstrumentation().getTargetContext();
        MainModel mainModel = new MainModel(context);
        mainModel.setIsAutoBackupEnabled(true);
        mainModel.setDriveConnected(false);
        mainModel.setBackupDate(new GregorianCalendar(2015, Calendar.JANUARY,25).getTimeInMillis());
        assertThat(AutoBackup.isBackupDue(context),is(false));
    }
    @Test
    public void isBackupDue5() {
        Context context =  InstrumentationRegistry.getInstrumentation().getTargetContext();
        MainModel mainModel = new MainModel(context);
        mainModel.setIsAutoBackupEnabled(true);
        mainModel.setDriveConnected(true);
        mainModel.setBackupDate(0L);
        assertThat(AutoBackup.isBackupDue(context),is(false));
        assertThat(mainModel.getBackupDate(),greaterThan(0L));
    }


}
