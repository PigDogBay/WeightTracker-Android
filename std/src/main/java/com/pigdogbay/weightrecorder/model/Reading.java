package com.pigdogbay.weightrecorder.model;

import java.util.Date;

public class Reading
{
	public static final Reading NullReading = new Reading(0,0,new Date(0),"");
	
	int _ID;
	//Weight in kilograms
	double _Weight;
	Date _Date;
	String _Comment;
	
	public Reading()
	{
		_ID = 0;
		_Weight = 0f;
		_Date = new Date();
		_Comment = "";
	}
	public Reading(double weight, Date date, String comment)
	{
		//ID will be assigned by the database
		 _ID=0;
		_Weight = weight;
		_Date = date;
		_Comment=comment;
	}
	public Reading(int id, double weight, Date date, String comment)
	{
		_ID = id;
		_Weight = weight;
		_Date = date;
		_Comment=comment;
	}
    public int getID()
    {
        return _ID;
    }
    public void setID(int id)
    {
        _ID = id;
    }	
	public double getWeight()
	{
		return _Weight;
	}
	public void setWeight(double weight)
	{
		_Weight = weight;
	}
	public Date getDate()
	{
		return _Date;
	}
	public void setDate(Date date)
	{
		_Date = date;
	}
	public String getComment()
	{
		return _Comment;
	}
	public void setComment(String comment)
	{
		_Comment=comment;
	}
	
	@Override
	public String toString()
	{
		return String.format("%d,  %f, %s, %s",_ID,_Weight,_Date.toString(),_Comment);
	}
			
}
