package com.pigdogbay.weightrecorder.model;

public interface IUnitConverter
{
	String getUnits();
	double convert(double value);
	double inverse(double value);
	double getStepIncrement();
	/**
	 * NB Does not convert the weight
	 * @param weight in the user units
	 * @return string representation of the weight in user units
	 */
	String getDisplayString(double weight);
}
