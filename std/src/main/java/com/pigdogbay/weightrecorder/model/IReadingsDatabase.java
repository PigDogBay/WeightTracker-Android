package com.pigdogbay.weightrecorder.model;

import java.util.List;

public interface IReadingsDatabase {

	public abstract void addReading(Reading reading);
	public abstract void addReadings(List<Reading> readings);
	public abstract void mergeReadings(List<Reading> readings);
	public abstract Reading getReading(int id);
	public abstract List<Reading> getAllReadings();
	public abstract void deleteReading(Reading reading);
	public abstract int getReadingsCount();
	public abstract int updateReading(Reading reading);
	public abstract void deleteAllReadings();
	public abstract void close();

}