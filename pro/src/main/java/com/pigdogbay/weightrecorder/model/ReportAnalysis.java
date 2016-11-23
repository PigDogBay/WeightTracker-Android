package com.pigdogbay.weightrecorder.model;

import java.util.Date;

public class ReportAnalysis {
	private static final long DAY_IN_MILLIS = 24L * 60L * 60L * 1000L;

	private UserSettings _UserSettings;
	private BMICalculator _BMICalculator;
	private TrendAnalysis _TrendAnalysis, _TrendanalysisLastWeek, _TrendanalysisLastMonth;
	double MinWeight,MaxWeight,AverageWeight;
	private boolean IsWeekTrendAvaialble=false, IsMonthTrendAvailable=false;
	int Count;
	Reading FirstReading, LastReading;
	
	public ReportAnalysis(UserSettings userSettings, Query query)
	{
		query.sortByDate();
		_UserSettings = userSettings;
		_BMICalculator = new BMICalculator(userSettings);
		MinWeight = query.getMinWeight().getWeight();
		MaxWeight = query.getMaxWeight().getWeight();
		FirstReading = query.getFirstReading();
		LastReading = query.getLatestReading();
		AverageWeight = query.getAverageWeight();
		Count = query._Readings.size();
		
		Date now = new Date();
		Date lastWeek = new Date(now.getTime() - 7L * DAY_IN_MILLIS);
		Date lastMonth = new Date(now.getTime() - 30L * DAY_IN_MILLIS);
		_TrendAnalysis = new TrendAnalysis(query.getReadings());
		_TrendanalysisLastMonth = _TrendAnalysis;
		_TrendanalysisLastWeek = _TrendAnalysis;
		Query querySubset = query.getReadingsBetweenDates(lastMonth, now);
		if (querySubset.getReadings().size()>1)
		{
			_TrendanalysisLastMonth = new TrendAnalysis(querySubset.getReadings());
			IsMonthTrendAvailable=true;
			querySubset = querySubset.getReadingsBetweenDates(lastWeek, now);
			if (querySubset.getReadings().size()>1)
			{
				_TrendanalysisLastWeek = new TrendAnalysis(querySubset.getReadings());
				IsWeekTrendAvaialble=true;
			}
		}
	}
	double getFirstMinusLast()
	{
		return FirstReading.getWeight()-LastReading.getWeight();
	}
	double getLatestBMI()
	{
		return _BMICalculator.calculateBMI(LastReading.getWeight());		
	}
	double getTargetBMI()
	{
		return _BMICalculator.calculateBMI(_UserSettings.TargetWeight);
	}
	double getBottomOfIdealWeightRange()
	{
		return _BMICalculator.calculateWeightFromBMI(BMICalculator.UNDERWEIGHT_UPPER_LIMIT);
	}
	double getTopOfIdealWeightRange()
	{
		return _BMICalculator.calculateWeightFromBMI(BMICalculator.NORMAL_UPPER_LIMIT);
	}
	double getAverageBMI()
	{
		return _BMICalculator.calculateBMI(AverageWeight);
	}
	double getNextBMI(){
		double currentBmi = getLatestBMI();
		return Math.floor(currentBmi);
	}
	double getNextBMIWeight(double offset){
		double nextBmi = getNextBMI() -offset;
		if (nextBmi<0){
			nextBmi=0;
		}
		return _BMICalculator.calculateWeightFromBMI(nextBmi);
	}

	double getWeeklyTrendOverLastWeek()
	{
		return _TrendanalysisLastWeek.getTrendInDays()*7D;
	}
	double getWeeklyTrendOverLastMonth()
	{
		return _TrendanalysisLastMonth.getTrendInDays()*7D;
	}
	double getWeeklyTrendAllTime()
	{
		return _TrendAnalysis.getTrendInDays()*7D;
	}
	long getEstimatedDateUsingLastWeek()
	{
		return _TrendanalysisLastWeek.getEstimatedDate(_UserSettings.TargetWeight);
	}
	long getEstimatedDateUsingLastMonth()
	{
		return _TrendanalysisLastMonth.getEstimatedDate(_UserSettings.TargetWeight);
	}
	long getEstimatedDateUsingAllTime()
	{
		return _TrendAnalysis.getEstimatedDate(_UserSettings.TargetWeight);
	}
	long getTimeSpent()
	{
		return LastReading.getDate().getTime() - FirstReading.getDate().getTime();
	}
}
