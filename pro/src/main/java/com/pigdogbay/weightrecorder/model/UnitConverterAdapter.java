package com.pigdogbay.weightrecorder.model;

import com.pigdogbay.lib.usercontrols.INumberPickerValue;

public class UnitConverterAdapter implements INumberPickerValue{

	private double _Value = 0;
	private IUnitConverter _Converter;
	public UnitConverterAdapter(IUnitConverter converter)
	{
		_Converter = converter;
	}

	@Override
	public void increase() {
		_Value+=_Converter.getStepIncrement();
	}

	@Override
	public void decrease() {
		_Value-=_Converter.getStepIncrement();
		if (_Value<0){
			_Value=0;
		}
	}

	@Override
	public String getFormattedString() {
		return _Converter.getDisplayString(_Value);
	}

	@Override
	public double getValue() {
		return _Value;
	}

	@Override
	public void setValue(double value) {
		_Value = value;
		if (_Value<0){
			_Value=0;
		}
	}

	@Override
	public String getDisplayFormat() {
		return "";
	}

	@Override
	public void setDisplayFormat(String format) {

	}

	public double getValueInPrimaryUnits()
	{
		return _Converter.inverse(_Value);
	}
}
