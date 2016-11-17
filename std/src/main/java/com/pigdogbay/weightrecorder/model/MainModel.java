package com.pigdogbay.weightrecorder.model;

import java.util.Collections;
import java.util.List;

import com.pigdogbay.lib.mvp.BackgroundColorModel;
import com.pigdogbay.lib.utils.PreferencesHelper;
import com.pigdogbay.weightrecorder.R;
import android.content.Context;

/*
 * Interface to the user data and settings
 * 
 */
public class MainModel
{
	public static final double DEFAULT_HEIGHT=1.72D;
	//Same as 175lb
	public static final double DEFAULT_TARGET_WEIGHT=79.3787D;
	public static final double DEFAULT_HEIGHT_INCHES=72D;
	public static final double DEFAULT_TARGET_WEIGHT_POUNDS=175D;
	public static final long AD_FREE_GRACE_PERIOD_IN_MILLIS = 7L*24L*60L*60L*1000L;
	
	private Context _Context;
	private IReadingsDatabase _DatabaseHelper;
	private PreferencesHelper _PreferencesHelper; 
	
	public MainModel(Context context)
	{
		_Context = context;
	}
	
	public IReadingsDatabase getDatabase()
	{
		if (_DatabaseHelper==null)
		{
			_DatabaseHelper = new DatabaseHelper(_Context); 
		}
		return _DatabaseHelper;
	}
	public PreferencesHelper getPreferencesHelper()
	{
		if (_PreferencesHelper==null)
		{
			_PreferencesHelper = new PreferencesHelper(_Context);
		}
		return _PreferencesHelper;
	}
	public void close()
	{
		if (_DatabaseHelper!=null)
		{
			_DatabaseHelper.close();
		}
	}
	public BackgroundColorModel createBackgroundColorModel()
	{
		return new BackgroundColorModel(getPreferencesHelper(), BackgroundColorModel.SKY_BLUE_INDEX);
	}
	
	public List<Reading> getReverseOrderedReadings(){
		List<Reading> readings = getDatabase().getAllReadings();
		Query query = new Query(readings);
		query.sortByDate();
		readings = query.getReadings();
		Collections.reverse(readings);
		return readings;
	}
	/**
	 * @return weight in currently selected units
	 */
	public double getHeight()
	{
		return getPreferencesHelper().getDouble(R.string.code_pref_height_key, DEFAULT_HEIGHT_INCHES);
	}
	public void setHeight(double height)
	{
		getPreferencesHelper().setDouble(R.string.code_pref_height_key, height);
	}
	public double getTargetWeight()
	{
		return getPreferencesHelper().getDouble(R.string.code_pref_target_weight_key, DEFAULT_TARGET_WEIGHT_POUNDS);
	}
	public void setTargetWeight(double weight)
	{
		getPreferencesHelper().setDouble(R.string.code_pref_target_weight_key, weight);
	}
	public IUnitConverter getWeightConverter()
	{
		int converterType = getPreferencesHelper().getInt(R.string.code_pref_weight_units_key, UnitConverterFactory.KILOGRAMS_TO_POUNDS);
		return UnitConverterFactory.create(converterType);
	}
	public int getWeightUnitsId()
	{
		return getPreferencesHelper().getInt(R.string.code_pref_weight_units_key, UnitConverterFactory.KILOGRAMS_TO_POUNDS);
	}
	public void setWeightUnitsId(int id)
	{
		getPreferencesHelper().setInt(R.string.code_pref_weight_units_key, id);
	}
	public IUnitConverter getLengthConverter()
	{
		int converterType = getPreferencesHelper().getInt(R.string.code_pref_length_units_key, UnitConverterFactory.METRES_TO_INCHES);
		return UnitConverterFactory.createLengthConverter(converterType);
	}
	public int getLengthUnitsId()
	{
		return getPreferencesHelper().getInt(R.string.code_pref_length_units_key, UnitConverterFactory.METRES_TO_INCHES);
	}
	public void setLengthUnitsId(int id)
	{
		getPreferencesHelper().setInt(R.string.code_pref_length_units_key, id);
	}
	public double getHeightInMetres()
	{
		return getLengthConverter().inverse(getHeight());
	}
	public double getTargetWeightInKilograms()
	{
		return getWeightConverter().inverse(getTargetWeight());
	}
	public boolean getShowTargetLine()
	{
		return getPreferencesHelper().getBoolean(R.string.code_pref_show_targetline_key, true);
	}
	public void setShowTargetLine(boolean show)
	{
		getPreferencesHelper().setBoolean(R.string.code_pref_show_targetline_key, show);
	}
	public boolean getShowTrendLine()
	{
		return getPreferencesHelper().getBoolean(R.string.code_pref_show_trendline_key, true);
	}
	public void setShowTrendLine(boolean show)
	{
		getPreferencesHelper().setBoolean(R.string.code_pref_show_trendline_key, show);
	}
	public boolean getIsAutoBackupEnabled()
	{
		return getPreferencesHelper().getBoolean(R.string.code_pref_auto_backup_key, true);
	}
	public void setIsAutoBackupEnabled(boolean flag)
	{
		getPreferencesHelper().setBoolean(R.string.code_pref_auto_backup_key, flag);
	}
	public boolean getIsFirstTime()
	{
		return getPreferencesHelper().getBoolean(R.string.code_pref_welcome_shown_key, false);
	}
	public void setIsFirstTime(boolean flag)
	{
		getPreferencesHelper().setBoolean(R.string.code_pref_welcome_shown_key, flag);
	}
	public boolean getIsGoalCongratulationsEnabled()
	{
		return getPreferencesHelper().getBoolean(R.string.code_pref_enable_goal_congratulations_key, true);
	}
	public void setIsGoalCongratulationsEnabled(boolean flag)
	{
		getPreferencesHelper().setBoolean(R.string.code_pref_enable_goal_congratulations_key, flag);
	}
	
	public UserSettings getUserSettings()
	{
		UserSettings userSettings = new UserSettings();
		userSettings.Height = getHeightInMetres();
		userSettings.TargetWeight = getTargetWeightInKilograms();
		userSettings.LengthConverter = getLengthConverter();
		userSettings.WeightConverter = getWeightConverter();
		userSettings.ShowTargetLine= getShowTargetLine();
		userSettings.ShowTrendLine = getShowTrendLine();
		return userSettings;
	}
		
}
