package com.pigdogbay.weightrecorder.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.pigdogbay.weighttrackerpro.R;

import android.content.Context;

public class ReportFormatting {
	private static final String InvalidDateString = "---";
	UserSettings _UserSettings;
	Context _Context;
	
	public ReportFormatting(Context context, UserSettings userSettings){
		_UserSettings = userSettings;
		_Context = context;
	}
	/**
	 * @param weight in kilograms
	 * @return string representation with user units
	 */
	public String getWeightString(double weight) {
		if (Double.isNaN(weight) || Double.isInfinite(weight))
		{
			//prevent user seeing INF or NaN 
			weight = 0;
		}
		weight = _UserSettings.WeightConverter.convert(weight);
		return _UserSettings.WeightConverter.getDisplayString(weight);
	}
	public String getBMIString(double bmi) {
		if (Double.isNaN(bmi) || Double.isInfinite(bmi) || bmi<0 || bmi>BMICalculator.MAX_BMI)
		{
			return _Context.getString(R.string.bmi_error);
		}
		return String.format("%.1f (%s)", bmi, BMICalculator.getString(_Context, bmi));
	}
	public String getIdealWeightRange(double startWeight, double endWeight) {
		return getWeightString(startWeight) +" - "+getWeightString(endWeight);
	}
	public String getNextBMI(double bmi, double weight){
		return String.format("(%.0f) %s", bmi, getWeightString(weight));
	}
	public String getValidDateString(long timeInMillis)
	{
		return TrendAnalysis.isGoalDateValid(timeInMillis) ? getDateString(timeInMillis) : InvalidDateString;
	}
	public String getDateString(long timeInMillis)
	{
		//DateUtils.formatDateTime suffers from the Year 2038 problem
		//One gripe with SDF is that UK locale formats like June 14, 2013 which is more American IMHO
		return SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG).format(new Date(timeInMillis));
	}
	public String getDateString(Date date)
	{
		//DateUtils.formatDateTime suffers from the Year 2038 problem
		//One gripe with SDF is that UK locale formats like June 14, 2013 which is more American IMHO
		return SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG).format(date);
	}
	public String getNumberOfDays(Long timeInMillis)
	{
		timeInMillis = timeInMillis/(1000L*60L*60L*24L);
		return Long.toString(timeInMillis)+" "+_Context.getString(R.string.report_days);
	}
	public String getDateTimeString(Date date)
	{
		return SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.LONG, SimpleDateFormat.MEDIUM).format(date);
	}
	public String getWeightTrendDirection(double trend)
	{
		int id =trend>0 ? R.string.report_gaining : R.string.report_losing;
		return  _Context.getString(id);
	}
	public String getWeightTrend(double trend)
	{
		return  String.format("%s %s",getWeightString(trend),_Context.getString(R.string.report_per_week));
	}
	
}
