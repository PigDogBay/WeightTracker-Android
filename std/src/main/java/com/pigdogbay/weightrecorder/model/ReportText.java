package com.pigdogbay.weightrecorder.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.pigdogbay.weightrecorder.R;

public class ReportText {

	Map<String,String> _Map = new HashMap<String,String>();

	public static final String LatestBMI_Key = "$LatestBMI";
	public static final String GoalBMI_Key = "$GoalBMI";
	public static final String IdealRange_Key = "$IdealRange";
	public static final String TrendWeekTitle_Key = "$TrendWeekTitle";
	public static final String TrendWeekValue_Key = "$TrendWeekValue";
	public static final String TrendWeekGoalDate_Key = "$TrendWeekGoalDate";
	public static final String TrendMonthTitle_Key = "$TrendMonthTitle";
	public static final String TrendMonthValue_Key = "$TrendMonthValue";
	public static final String TrendMonthGoalDate_Key = "$TrendMonthGoalDate";
	public static final String TrendAllTitle_Key = "$TrendAllTitle";
	public static final String TrendAllValue_Key = "$TrendAllValue";
	public static final String TrendAllGoalDate_Key = "$TrendAllGoalDate";
	public static final String MinWeight_Key = "$MinWeight";
	public static final String MaxWeight_Key = "$MaxWeight";
	public static final String MaxMinusMinWeight_Key = "$MaxMinusMinWeight";
	public static final String FirstWeight_Key = "$FirstWeight";
	public static final String LastWeight_Key = "$LastWeight";
	public static final String FirstMinusLastWeight_Key = "$FirstMinusLastWeight";
	public static final String AverageWeight_Key = "$AverageWeight";
	public static final String AverageBMI_Key = "$AverageBMI";
	public static final String Count_Key = "$Count";
	public static final String FirstDate_Key = "$FirstDate";
	public static final String LastDate_Key = "$LastDate";
	public static final String TimeSpent_Key = "$TimeSpent";
	
	public ReportText(ReportAnalysis analysis, ReportFormatting formatter)
	{
		_Map.put(LatestBMI_Key, formatter.getBMIString(analysis.getLatestBMI()));
		_Map.put(GoalBMI_Key, formatter.getBMIString(analysis.getTargetBMI()));
		_Map.put(IdealRange_Key, formatter.getIdealWeightRange(
				analysis.getBottomOfIdealWeightRange(),
				analysis.getTopOfIdealWeightRange()));
		_Map.put(TrendWeekTitle_Key, formatter.getWeightTrendDirection(analysis.getWeeklyTrendOverLastWeek()));
		_Map.put(TrendWeekValue_Key, formatter.getWeightTrend(analysis.getWeeklyTrendOverLastWeek()));
		_Map.put(TrendWeekGoalDate_Key, formatter.getValidDateString(analysis.getEstimatedDateUsingLastWeek()));
		_Map.put(TrendMonthTitle_Key, formatter.getWeightTrendDirection(analysis.getWeeklyTrendOverLastMonth()));
		_Map.put(TrendMonthValue_Key, formatter.getWeightTrend(analysis.getWeeklyTrendOverLastMonth()));
		_Map.put(TrendMonthGoalDate_Key, formatter.getValidDateString(analysis.getEstimatedDateUsingLastMonth()));
		_Map.put(TrendAllTitle_Key, formatter.getWeightTrendDirection(analysis.getWeeklyTrendAllTime()));
		_Map.put(TrendAllValue_Key, formatter.getWeightTrend(analysis.getWeeklyTrendAllTime()));
		_Map.put(TrendAllGoalDate_Key, formatter.getValidDateString(analysis.getEstimatedDateUsingAllTime()));
		_Map.put(MinWeight_Key, formatter.getWeightString(analysis.MinWeight));
		_Map.put(MaxWeight_Key, formatter.getWeightString(analysis.MaxWeight));
		_Map.put(MaxMinusMinWeight_Key, formatter.getWeightString(analysis.MaxWeight-analysis.MinWeight));
		_Map.put(FirstWeight_Key, formatter.getWeightString(analysis.FirstReading.getWeight()));
		_Map.put(LastWeight_Key, formatter.getWeightString(analysis.LastReading.getWeight()));
		_Map.put(FirstMinusLastWeight_Key, formatter.getWeightString(analysis.getFirstMinusLast()));
		_Map.put(AverageWeight_Key, formatter.getWeightString(analysis.AverageWeight));
		_Map.put(AverageBMI_Key, formatter.getBMIString(analysis.getAverageBMI()));
		_Map.put(Count_Key, Integer.toString(analysis.Count));
		_Map.put(FirstDate_Key, formatter.getDateString(analysis.FirstReading.getDate()));
		_Map.put(LastDate_Key, formatter.getDateString(analysis.LastReading.getDate()));
		_Map.put(TimeSpent_Key, formatter.getNumberOfDays(analysis.getTimeSpent()));
	}
	public Set<Map.Entry<String, String>> getEntrySet()
	{
		return _Map.entrySet();
	}
	public String createReport(String template)
	{
		for (Map.Entry<String, String> entry : _Map.entrySet())
		{
			template = template.replace(entry.getKey(), entry.getValue());
		}

		return template;
	}	
}
