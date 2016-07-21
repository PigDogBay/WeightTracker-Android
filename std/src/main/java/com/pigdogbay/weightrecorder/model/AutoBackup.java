package com.pigdogbay.weightrecorder.model;

import java.util.Date;

import com.pigdogbay.lib.utils.PreferencesHelper;
import com.pigdogbay.weightrecorder.R;


public class AutoBackup {

	public static final long WEEKLY_BACKUP_PERIOD_IN_DAYS = 7L;
	public static final long DAY_IN_MILLIS = 24L * 60L * 60L * 1000L;
	private PreferencesHelper _PreferencesHelper; 

	public AutoBackup(PreferencesHelper preferencesHelper)
	{
		_PreferencesHelper = preferencesHelper;
	}
	
	public boolean isAutoBackupEnabled()
	{
		return _PreferencesHelper.getBoolean(R.string.code_pref_auto_backup_key, true);
	}
	
	public boolean isBackupDue(long days)
	{
		long now = new Date().getTime();
		long last =  _PreferencesHelper.getLong(R.string.code_pref_auto_backup_last_date_key, 0L);
		return (now-last>days*DAY_IN_MILLIS);
	}
	
	public void setBackupDateToNow()
	{
		long now = new Date().getTime();
		_PreferencesHelper.setLong(R.string.code_pref_auto_backup_last_date_key, now);
	}
	public void setBackupDate(Date date)
	{
		_PreferencesHelper.setLong(R.string.code_pref_auto_backup_last_date_key, date.getTime());
	}
	
}
