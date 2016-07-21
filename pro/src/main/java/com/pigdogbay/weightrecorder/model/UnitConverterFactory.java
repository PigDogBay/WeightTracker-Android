package com.pigdogbay.weightrecorder.model;


public class UnitConverterFactory
{
	/*
	 * http://en.wikipedia.org/wiki/US_customary_units#Units_of_mass 
	 * The pound avoirdupois, which forms the basis of the U.S. customary system of mass, 
	 * is defined as exactly 453.59237 grams by agreement between the U.S., the U.K. 
	 * and other English-speaking countries in 1959. 
	 */
	public static final double POUND_TO_KILOGRAM_FACTOR = 0.45359237d;
	/*
	 * http://en.wikipedia.org/wiki/Inch#Modern_standardisation 
	 * The current internationally accepted value for the imperial and US customary inch is exactly 25.4 millimetres.
	 */
	public static final double INCH_METRE_FACTOR = 0.0254D;
	
	// Numbers must match arrays.xml
	public final static int KILOGRAMS_TO_KILOGRAMS = 1;
	public final static int KILOGRAMS_TO_POUNDS = 2;
	public final static int KILOGRAMS_TO_STONES = 3;

	public final static int METRES_TO_METRES = 1;
	public final static int METRES_TO_CENTIMETRES = 2;
	public final static int METRES_TO_INCHES = 3;
	public final static int METRES_TO_FEET = 4;

	public static IUnitConverter create(int id)
	{
		switch (id)
		{
		case KILOGRAMS_TO_KILOGRAMS:
			return new FactorConverter("kg", 1.0d);
		case KILOGRAMS_TO_STONES:
			return new StonesConverter();
		default:
			return new FactorConverter("lb", 1d/POUND_TO_KILOGRAM_FACTOR,0.5d);
		}
	}

	public static IUnitConverter createLengthConverter(int id)
	{
		switch (id)
		{
		case METRES_TO_METRES:
			return new MetresConverter();
		case METRES_TO_CENTIMETRES:
			return new FactorConverter("cm", 100.0d,1D);
		case METRES_TO_FEET:
			return new FeetConverter();
		default:
			return new FactorConverter("in", 1d/INCH_METRE_FACTOR,1D);
		}
	}
}
class MetresConverter implements IUnitConverter{
	private static final String UNITS = "m";
	@Override
	public String getUnits() {
		return UNITS;
	}
	@Override
	public double convert(double value) {
		return value;
	}
	@Override
	public double inverse(double value) {
		return value;
	}
	@Override
	public double getStepIncrement() {
		return 0.01D;
	}
	@Override
	public String getDisplayString(double length) {
		return String.format("%.2f %s", length, UNITS);
	}
}

class FactorConverter implements IUnitConverter
{
	String units;
	double factor;
	double step=0.1d;
	
	public FactorConverter(String units, double factor)
	{
		this.units = units;
		this.factor = factor;
	}
	public FactorConverter(String units, double factor, double step)
	{
		this.units = units;
		this.factor = factor;
		this.step = step;
	}

	public String getUnits()
	{
		return units;
	}

	public double convert(double value)
	{
		return value * factor;
	}

	public double inverse(double value)
	{
		return value / factor;
	}

	/**
	 * NB Does not convert the weight
	 * @param weight in the user units
	 * @return string representation of the weight in user units
	 */
	public String getDisplayString(double weight)
	{
		return String.format("%.1f %s", weight, units);
	}

	public double getStepIncrement()
	{
		return step;
	}
}

class StonesConverter implements IUnitConverter
{
	private static final String STONE_UNITS = "st";
	private static final String POUND_UNITS = "lb";
	
	private static final double STONE_TO_POUND_FACTOR = 14d;
	private static final double STONE_KILOGRAM = UnitConverterFactory.POUND_TO_KILOGRAM_FACTOR*STONE_TO_POUND_FACTOR;
	public String getUnits()
	{
		return STONE_UNITS;
	}

	public double convert(double value)
	{
		return value/STONE_KILOGRAM;
	}

	public double inverse(double value)
	{
		return value*STONE_KILOGRAM;
	}

	/**
	 * NB Does not convert the weight
	 * @param weight in the user units
	 * @return string representation of the weight in user units
	 */
	public String getDisplayString(double weight)
	{
		//Convert to pounds
		weight = weight*STONE_TO_POUND_FACTOR;
		int intWeight = (int)Math.round(weight);
		int stones= intWeight/14;
		int pounds=intWeight - stones*14;
		return stones==0 ? 
				String.format("%d %s", pounds, POUND_UNITS):
				String.format("%d%s %d%s", stones, STONE_UNITS, pounds, POUND_UNITS);
	}

	public double getStepIncrement()
	{
		return 1.0d/14.0d;
	}
	
}
class FeetConverter implements IUnitConverter
{
	private static final String FEET_UNITS = "ft";
	private static final String INCHES_UNITS = "in";
	
	private static final double FEET_TO_INCHES_FACTOR = 12d;
	private static final double FEET_TO_METRES = UnitConverterFactory.INCH_METRE_FACTOR*FEET_TO_INCHES_FACTOR;
	public String getUnits()
	{
		return FEET_UNITS;
	}

	public double convert(double value)
	{
		return value/FEET_TO_METRES;
	}

	public double inverse(double value)
	{
		return value*FEET_TO_METRES;
	}

	/**
	 * NB Does not convert the weight
	 * @param length in the user units
	 * @return string representation of the weight in user units
	 */
	public String getDisplayString(double length)
	{
		//Convert to pounds
		length = length*FEET_TO_INCHES_FACTOR;
		int intLength = (int)Math.round(length);
		int feet= intLength/12;
		int inches=intLength - feet*12;
		return feet==0 ? 
				String.format("%d %s", inches, INCHES_UNITS):
				String.format("%d%s %d%s", feet, FEET_UNITS, inches, INCHES_UNITS);
	}

	public double getStepIncrement()
	{
		return 1.0d/FEET_TO_INCHES_FACTOR;
	}
	
}
