package com.pigdogbay.weightrecorder.model;

public class UserSettings {
	
	/**
	 * Height in metres
	 */
	public double Height;
	/**
	 * Target weight in kilograms
	 */
	public double TargetWeight;
	public IUnitConverter WeightConverter, LengthConverter;
	public boolean ShowTrendLine, ShowTargetLine;
}
