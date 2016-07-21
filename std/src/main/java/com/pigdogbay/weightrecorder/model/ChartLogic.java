package com.pigdogbay.weightrecorder.model;

import java.util.Calendar;
import java.util.Date;

import org.achartengine.model.TimeSeries;

public class ChartLogic {
	
	public static final double AXIS_DEFAULT_MIN_WEIGHT = 50D;
	public static final double AXIS_DEFAULT_MAX_WEIGHT = 100D;
	public static final double AXIS_MIN_PADDING = 0.5D;

	public static final double AXIS_Y_PERCENTAGE_PADDING = 0.1D;
	private static final long MILLIS_IN_DAY = 1000L*60L*60L*24L;
	
	UserSettings _UserSettings;
	public ChartLogic(UserSettings userSettings) {
		_UserSettings = userSettings;
	}
	/**
	 * If period is 0,  the axes fit the entire data set. If period is 0 and 0 readings are passed, this is likely to
	 * be a bug as readings should be guaranteed by callee, if this happens 0,0,0,0 is returned.
	 * 
	 * For period>0, the time axis, X, is set from period days past upto now.
	 * If 0 readings are in this period then default y-axis values are used, this may happen if the user has not entered
	 * any readings for a while.
	 * 
	 * The Y axis is padded by 10% of the min-max range both ends. The x-axis has one extra day added at the RHS
	 * Minimum Y-axis padding is AXIS_MIN_PADDING
	 * 
	 * @param query containing data to plot
	 * @param period in days
	 */
	public ChartAxesRanges calculateAxesRanges(Query query, long period) {
		if (query.getReadings().size()==0)
		{
			return new ChartAxesRanges();
		}
		Date endTime = new Date();
		Date startTime = new Date(endTime.getTime() - period * MILLIS_IN_DAY);
		if (period == 0) {
			startTime = query.getFirstReading().getDate();
			endTime = query.getLatestReading().getDate();
		}
		else
		{
			query = query.getReadingsBetweenDates(startTime, endTime);
		}
		
		ChartAxesRanges chartAxesRanges = calculateYAxisRange(query);
		padYAxis(chartAxesRanges, AXIS_Y_PERCENTAGE_PADDING);
		// add 1 day to current date
		chartAxesRanges.XAxisMax = endTime.getTime() + MILLIS_IN_DAY;
		chartAxesRanges.XAxisMin = startTime.getTime();
		return chartAxesRanges;
	}
	/**
	 * Calculate the Y Axis values based on the min and max values in the query
	 * Values are converted into the user units
	 * 
	 * If 0 readings are in this period then default y-axis values are used, this may happen if the user has not entered
	 * any readings for a while.
	 * 
	 * @param query
	 * @return ChartAxesRanges with Y axis values set
	 */
	private ChartAxesRanges calculateYAxisRange(Query query)
	{
		ChartAxesRanges chartAxesRanges = new ChartAxesRanges();
		double min = query.getMinWeight().getWeight();
		double max = query.getMaxWeight().getWeight();
		if (query.getReadings().size()==0)
		{
			min = AXIS_DEFAULT_MIN_WEIGHT;
			max = AXIS_DEFAULT_MAX_WEIGHT;
		}
		chartAxesRanges.YAxisMin = _UserSettings.WeightConverter.convert(min);
		chartAxesRanges.YAxisMax = _UserSettings.WeightConverter.convert(max);
		
		return chartAxesRanges;
		
	}
	/*
	 * Extends the Y-axis at both ends to give a more pleasing look
	 */
	private void padYAxis(ChartAxesRanges chartAxesRanges, double percentagePadding)
	{
		double padding = (chartAxesRanges.YAxisMax-chartAxesRanges.YAxisMin) *percentagePadding;
		if (padding < AXIS_MIN_PADDING) {
			padding = AXIS_MIN_PADDING;
		}
		chartAxesRanges.YAxisMin -= padding;
		chartAxesRanges.YAxisMax += padding;
	}	
	
	public TimeSeries createReadingsSeries(Query query)
	{
		TimeSeries series = new TimeSeries("weights");
		for (Reading reading : query.getReadings())
		{
			double weight = reading.getWeight();
			weight = _UserSettings.WeightConverter.convert(weight);
			series.add(reading.getDate(), weight);
		}
		return series;
	}
	public TimeSeries createTargetSeries()
	{
		TimeSeries series = new TimeSeries("target");
		//Display line one year back and forward
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.roll(Calendar.YEAR, false);
		double targetWeight = _UserSettings.WeightConverter.convert(_UserSettings.TargetWeight);
		for (int i=0;i<25; i++)
		{
			series.add(cal.getTime(), targetWeight);
			cal.add(Calendar.MONTH,1);
		}
		return series;
	}
	public TimeSeries createTrendSeries(Query query, long period)
	{
		//ensure readings are sorted by date before proceding
		query.sortByDate();
		TimeSeries series = new TimeSeries("trend");
		long startTime = new Date().getTime() - period * MILLIS_IN_DAY;
		if (period==0)
		{
			//use first reading
			startTime = query.getFirstReading().getDate().getTime();
		}
		else
		{
			query = query.getReadingsBetweenDates(new Date(startTime),new Date());
		}
		
		if (query.getReadings().size()<3)
		{
			return series;
		}
		TrendAnalysis trendAnalysis = new TrendAnalysis(query.getReadings());

		//Add start weight
		double weight = trendAnalysis.getEstimatedWeight(startTime);
		weight = _UserSettings.WeightConverter.convert(weight);
		series.add(new Date(startTime), weight);
		//Project forward 1 year
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<13; i++)
		{
			weight = trendAnalysis.getEstimatedWeight(cal.getTimeInMillis());
			weight = _UserSettings.WeightConverter.convert(weight);
			series.add(cal.getTime(),weight);
			cal.add(Calendar.MONTH,1);
		}		
		return series;
	}
	
	
}
