/*********************************************************
 * 
 * Copyright 2013, Mark Bailey
 * http://www.pigdogbay.com
 * 
 *********************************************************/
package com.pigdogbay.weightrecorder.model;

import java.util.Calendar;
import java.util.List;

import com.pigdogbay.lib.math.BestLineFit;

public class TrendAnalysis {

	//Roughly 5 years in milliseconds
	private static final long MAX_PROJECTION = 1000L*60L*60L*24L*365L*5L;
	private BestLineFit _BestLineFit = new BestLineFit();

	/**
	 * Weights must be already converted to the correct units
	 * @param readings 
	 */
	public TrendAnalysis(List<Reading> readings)
	{
		_BestLineFit = new BestLineFit();
		for (Reading r : readings) {
			//divide by 1000 to avoid precision errors in the long<-->double conversion
			long time = r.getDate().getTime()/1000;
			_BestLineFit.Add((double) time, r.getWeight());
		}
	}
	
	/**
	 * @param targetWeight
	 * @return estimated date (UTC time) when target weight is hit
	 */
	public long getEstimatedDate(double targetWeight)
	{
		long time =(long)_BestLineFit.calculateX(targetWeight);
		//convert to milliseconds
		return time*1000; 
	}
	
	/**
	 * Checks to see if the date is a sensible goal date
	 * @param UTC Time in milliseconds 
	 * @return true if date is a sensible goal date
	 */
	public static boolean isGoalDateValid(long timeInMillis)
	{
		long now = Calendar.getInstance().getTimeInMillis();
		//5 years hence
		long maxTime = now+MAX_PROJECTION;
		return timeInMillis>now && timeInMillis<maxTime;
	}
	
	/**
	 * Negative values indicate weight loss, positive weight gain
	 * 
	 * @return Amount of weight loss or gained each day
	 */
	public double getTrendInDays()
	{
		double slope = _BestLineFit.getSlope();
		// convert slope for per s to per day
		slope = slope * 24D * 60D * 60D;
		if (Double.isNaN(slope) || Double.isInfinite(slope)) {
			slope = 0;
		}
		return slope;
	}
	
	/**
	 * Date().getTime() and Calendar.getTimeInMillis() both return UTC Time
	 * @param UTC Time in milliseconds
	 * @return estimated weight for the specified date
	 */
	public double getEstimatedWeight(long timeInMillis)
	{
		return _BestLineFit.calculateY(timeInMillis/1000);
	}
	
}
