package com.pigdogbay.weightrecorder.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;



public class Query
{
	List<Reading> _Readings;
	public List<Reading> getReadings()
	{
		return _Readings;
	}
	public Query(IReadingsDatabase database)
	{
		_Readings = database.getAllReadings();
	}
	public Query(List<Reading> readings)
	{
		_Readings = readings;
	}
	
	public void sortByDate()
	{
		Collections.sort(_Readings,new DateComparator());
	}	
	public void sortByDateDescending()
	{
		sortByDate();
		Collections.reverse(_Readings);
	}	
	public Reading getMinWeight()
	{
		if (_Readings.size() == 0)
		{
			return Reading.NullReading;
		}
		Reading minReading = _Readings.get(0);
		for (Reading r : _Readings)
		{
			if (r.getWeight() < minReading.getWeight())
			{
				minReading = r;
			}
		}
		return minReading;

	}
	
	public Query getReadingsBetweenDates(Date start, Date end)
	{
		List<Reading> matches = new ArrayList<Reading>();
		for (Reading r : _Readings)
		{
			if (r.getDate().compareTo(start)>=0 && r.getDate().compareTo(end)<=0)
			{
				matches.add(r);
			}
		}
		return new Query(matches);
	}

	public Reading getMaxWeight()
	{
		Reading maxReading = Reading.NullReading;
		for (Reading r : _Readings)
		{
			if (r.getWeight() > maxReading.getWeight())
			{
				maxReading = r;
			}
		}
		return maxReading;
	}	
	
	public double getAverageWeight()
	{
		double avg = 0d;
		int count = _Readings.size();
		if (count==0)
		{
			return 0d;
		}
		for (Reading r : _Readings)
		{
			avg=avg+r.getWeight();
		}
		return avg/((double)count);
	}
	
	public Reading getFirstReading()
	{
		int count = _Readings.size();
		if (count==0)
		{
			return Reading.NullReading;
		}
		return _Readings.get(0);
	}
	public Reading getLatestReading()
	{
		int count = _Readings.size();
		if (count==0)
		{
			return Reading.NullReading;
		}
		sortByDate();
		return _Readings.get(count-1);
	}
	
	public Reading findReadingByDate(Date date)
	{
		//remove milliseconds
		long key = date.getTime()/1000L;
		for (Reading r : _Readings)
		{
			long match = r.getDate().getTime()/1000L;
			if (key==match)
			{
				return r;
			}
		}
		return Reading.NullReading;
		
	}

	private class DateComparator implements Comparator<Reading>
	{
		public int compare(Reading lhs, Reading rhs)
		{
			return lhs.getDate().compareTo(rhs.getDate());
		}
	}
	
}
