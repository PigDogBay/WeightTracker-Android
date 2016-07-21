package com.pigdogbay.weightrecorder.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class DummyData
{
	public static List<Reading> GetReadings()
	{
		List<Reading> readings = new ArrayList<Reading>();
		readings.add(new Reading(95.5f,new Date(112,Calendar.DECEMBER,2,8,5),""));
		readings.add(new Reading(95.8f,new Date(112,Calendar.DECEMBER,3,8,5),""));
		readings.add(new Reading(95.3f,new Date(112,Calendar.DECEMBER,5,8,5),""));
		readings.add(new Reading(94.8f,new Date(112,Calendar.DECEMBER,6,8,5),""));
		readings.add(new Reading(95.1f,new Date(112,Calendar.DECEMBER,9,8,5),""));
		readings.add(new Reading(96.4f,new Date(112,Calendar.DECEMBER,10,8,5),""));
		readings.add(new Reading(94.4f,new Date(112,Calendar.DECEMBER,11,8,5),""));
		readings.add(new Reading(95.1f,new Date(112,Calendar.DECEMBER,12,8,5),""));
		readings.add(new Reading(95.3f,new Date(112,Calendar.DECEMBER,16,8,5),""));
		readings.add(new Reading(95.0f,new Date(112,Calendar.DECEMBER,17,8,5),""));
		readings.add(new Reading(94.9f,new Date(112,Calendar.DECEMBER,18,8,5),""));
		readings.add(new Reading(94.0f,new Date(112,Calendar.DECEMBER,19,8,5),""));
		readings.add(new Reading(94.5f,new Date(112,Calendar.DECEMBER,21,11,0),""));
		readings.add(new Reading(94.6f,new Date(112,Calendar.DECEMBER,22,11,0),""));
		readings.add(new Reading(93.7f,new Date(112,Calendar.DECEMBER,23,11,0),"pigged out on hot dogs after the pub"));
		readings.add(new Reading(94.8f,new Date(112,Calendar.DECEMBER,24,11,0),""));
		readings.add(new Reading(94.4f,new Date(112,Calendar.DECEMBER,26,11,0),""));
		readings.add(new Reading(94.7f,new Date(112,Calendar.DECEMBER,28,11,0),""));
		return readings;
	}
	
	public static List<Reading> CreateReadings(int number)
	{
		List<Reading> readings = new ArrayList<Reading>();
		Date date = new Date();
		double weight = 70f;
		
		for (int i=0; i<number;i++)
		{
			readings.add(new Reading(weight,date,""));
			weight = weight +0.05f;
			date = new Date(date.getTime()-24*60*60*1000);
		}
		return readings;
	}
	
	public static List<Reading> createRandomData(int period)
	{
		List<Reading> readings = new ArrayList<Reading>();
		Random rand = new Random(42L);
		Date today = new Date();
		for (int i = period; i>=0 ;i--)
		{
			Date date = new Date(today.getTime()-24L*60L*60L*1000L*((long)i));
			double weight = 80f+((double)i)*0.15f-(rand.nextDouble()-1f)/2;
			readings.add(new Reading(weight,date,""));
		}
		
		return readings;
		
	}
} 

