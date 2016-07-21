package com.pigdogbay.weightrecorder.model;

import java.util.Locale;

import android.util.Log;

public class SettingsUtils {

	public static void setDefaultSettings(Locale locale, MainModel model) { 
		if (Locale.UK.equals(locale)){
			setUKSettings(model);
		}
		else if (Locale.US.equals(locale) || Locale.CANADA.equals(locale)){
			setUSSettings(model);
		}
		else {
			setEUSettings(model);
		}
	}
	
	private static void setUKSettings(MainModel model)
	{
		model.setLengthUnitsId(UnitConverterFactory.METRES_TO_FEET);
		model.setWeightUnitsId(UnitConverterFactory.KILOGRAMS_TO_STONES);
		model.setTargetWeight(12.5D);
		model.setHeight(5.75D);
	}
	private static void setUSSettings(MainModel model)
	{
		model.setLengthUnitsId(UnitConverterFactory.METRES_TO_FEET);
		model.setWeightUnitsId(UnitConverterFactory.KILOGRAMS_TO_POUNDS);
		model.setTargetWeight(175D);
		model.setHeight(5.75D);
	}
	private static void setEUSettings(MainModel model)
	{
		model.setLengthUnitsId(UnitConverterFactory.METRES_TO_CENTIMETRES);
		model.setWeightUnitsId(UnitConverterFactory.KILOGRAMS_TO_KILOGRAMS);
		model.setTargetWeight(80D);
		model.setHeight(175D);
	}
}
