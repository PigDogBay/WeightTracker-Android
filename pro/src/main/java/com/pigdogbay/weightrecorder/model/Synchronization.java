package com.pigdogbay.weightrecorder.model;

import java.util.ArrayList;
import java.util.List;

public class Synchronization {

	Query _Query;
	public List<Reading> _Readings;
	public Synchronization(List<Reading> current)
	{
		_Readings = current;
		_Query = new Query(current);
	}
	public void Merge(List<Reading> newReadings)
	{
		List<Reading> unmatchedReadings = new ArrayList<Reading>(); 
		for(Reading r : newReadings)
		{
			Reading match = _Query.findReadingByDate(r.getDate());
			if (Reading.NullReading==match)
			{
				unmatchedReadings.add(r);
			}
			else
			{
				match._Comment = r._Comment;
				match._Weight = r._Weight;
			}
		}
		_Readings.addAll(unmatchedReadings);
	}
}
