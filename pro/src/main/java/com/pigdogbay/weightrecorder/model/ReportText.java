package com.pigdogbay.weightrecorder.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ReportText {

	private Map<String,String> _Map = new HashMap<>();

	private static final String LatestBMI_Key = "$LatestBMI";
	private static final String GoalBMI_Key = "$GoalBMI";
	private static final String IdealRange_Key = "$IdealRange";
	private static final String TrendWeekTitle_Key = "$TrendWeekTitle";
	private static final String TrendWeekValue_Key = "$TrendWeekValue";
	private static final String TrendWeekGoalDate_Key = "$TrendWeekGoalDate";
	private static final String TrendMonthTitle_Key = "$TrendMonthTitle";
	private static final String TrendMonthValue_Key = "$TrendMonthValue";
	private static final String TrendMonthGoalDate_Key = "$TrendMonthGoalDate";
	private static final String TrendAllTitle_Key = "$TrendAllTitle";
	private static final String TrendAllValue_Key = "$TrendAllValue";
	private static final String TrendAllGoalDate_Key = "$TrendAllGoalDate";
	private static final String MinWeight_Key = "$MinWeight";
	private static final String MaxWeight_Key = "$MaxWeight";
	private static final String MaxMinusMinWeight_Key = "$MaxMinusMinWeight";
	private static final String FirstWeight_Key = "$FirstWeight";
	private static final String LastWeight_Key = "$LastWeight";
	private static final String FirstMinusLastWeight_Key = "$FirstMinusLastWeight";
	private static final String AverageWeight_Key = "$AverageWeight";
	private static final String AverageBMI_Key = "$AverageBMI";
	private static final String Count_Key = "$Count";
	private static final String FirstDate_Key = "$FirstDate";
	private static final String LastDate_Key = "$LastDate";
	private static final String TimeSpent_Key = "$TimeSpent";
	private static final String Date_Key = "$Date";

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
		_Map.put(Date_Key,formatter.getDateString(new Date()));
		_Map.put("$NextBMI", formatter.getNextBMI(analysis.getNextBMI(),analysis.getNextBMIWeight()));
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
