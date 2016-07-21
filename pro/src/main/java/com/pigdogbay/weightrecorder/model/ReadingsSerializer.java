package com.pigdogbay.weightrecorder.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ReadingsSerializer
{
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String ANDROID_DATE_FORMAT = "yyyy-MM-dd kk:mm:ss";
	private static SimpleDateFormat _SimpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);

	public static List<Reading> parse(String text)
	{
		String[] lines = text.split("\n");
		return parseReadings(lines);
	}

	private static List<Reading> parseReadings(String[] lines)
	{
		ArrayList<Reading> readings = new ArrayList<Reading>();
		for (String line : lines)
		{
			Reading reading = parseLine(line);
			if (reading != null)
			{
				readings.add(reading);
			}
		}
		return readings;
	}

	private static Reading parseLine(String line)
	{
		if (line == null || "".equals(line))
		{
			return null;
		}
		String[] parts = line.split(",");
		try
		{
			double weight = Double.parseDouble(parts[0]);
			Date date = _SimpleDateFormat.parse(parts[1]);
			String comment = "";
			if (parts.length>2)
			{
				comment=parts[2];
				//strip quotes
				if (comment.startsWith("\""))
				{
					comment = comment.substring(1);
				}
				if (comment.endsWith("\""))
				{
					comment = comment.substring(0,comment.length()-1);
				}
			}
			return new Reading(weight,date,comment);

		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	public static String format(List<Reading> readings)
	{
		StringBuilder builder = new StringBuilder();
		for (Reading r : readings)
		{
			builder.append(String.format(Locale.US,"%.2f", r.getWeight()));
			builder.append(",");
			builder.append(_SimpleDateFormat.format(r.getDate()));
			builder.append(",\"");
			builder.append(r.getComment());
			builder.append("\"\n");
		}
		return builder.toString();
	}
}
