package com.pigdogbay.weightrecorder.model;

import android.content.Context;

import com.pigdogbay.weightrecorder.R;

public class BMICalculator {
	
	public static final double SEVERLY_UNDERWEIGHT_UPPER_LIMIT = 16.5;
	public static final double UNDERWEIGHT_UPPER_LIMIT = 18.5;
	public static final double NORMAL_UPPER_LIMIT = 25;
	public static final double OVERWEIGHT_UPPER_LIMIT = 30;
	public static final double OBESE1_UPPER_LIMIT = 35;
	public static final double OBESE2_UPPER_LIMIT = 40;
	/**
	 * According to 
	 * http://en.wikipedia.org/wiki/List_of_the_heaviest_people
	 * 
	 * No one has gone above 200 kg/m^2
	 */
	public static final double MAX_BMI = 200;
	
	private UserSettings _UserSettings;
	public BMICalculator(UserSettings userSettings)
	{
		_UserSettings = userSettings;
	}
	
	/**
	 * @param weight in kilograms
	 * @return Body Mass Index
	 */
	public double calculateBMI(double weight)
	{
		double bmi = _UserSettings.Height;
		if (bmi!=0)
		{
			bmi = weight/(bmi*bmi);
		}
		return bmi;
	}
	public double calculateBMI(Reading reading)
	{
		return calculateBMI(reading.getWeight());
	}
	
	/**
	 * @param bmi
	 * @return weight in Kilograms
	 */
	public double calculateWeightFromBMI(double bmi)
	{
		return bmi*_UserSettings.Height*_UserSettings.Height;
	}	
	
	public static String getString(Context context, double bmi){
		if (bmi < BMICalculator.SEVERLY_UNDERWEIGHT_UPPER_LIMIT) {
			return context.getString(R.string.bmi_class_severely_underweight);
		}
		else if (bmi < BMICalculator.UNDERWEIGHT_UPPER_LIMIT) {
			return context.getString(R.string.bmi_class_underweight);
		}
		else if (bmi < BMICalculator.NORMAL_UPPER_LIMIT) {
			return context.getString(R.string.bmi_class_normal);
		}
		else if (bmi < BMICalculator.OVERWEIGHT_UPPER_LIMIT) {
			return context.getString(R.string.bmi_class_overweight);
		}
		else if (bmi < BMICalculator.OBESE1_UPPER_LIMIT) {
			return context.getString(R.string.bmi_class_obese1);
		}
		else if (bmi < BMICalculator.OBESE2_UPPER_LIMIT) {
			return context.getString(R.string.bmi_class_obese2);
		}
		return context.getString(R.string.bmi_class_obese3);
	}
}
