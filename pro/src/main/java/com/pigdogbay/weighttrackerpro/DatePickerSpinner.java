package com.pigdogbay.weighttrackerpro;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;

public class DatePickerSpinner extends LinearLayout implements
		OnDateSetListener {
	Button _DisplayButton;
	Calendar _Calendar;

	public Calendar getCalendar() {
		return _Calendar;
	}

	public void setCalendar(Calendar calendar) {
		_Calendar = calendar;
		updateDisplay();
	}

	public DatePickerSpinner(Context context) {
		this(context, null);
	}

	public DatePickerSpinner(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		this.setOrientation(HORIZONTAL);

		Button minusButton = new Button(context);
		minusButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				minusButtonClicked();
			}
		});
		minusButton.setText("-");
		minusButton.setMinEms(2);
		minusButton.setTextAppearance(context,
				android.R.style.TextAppearance_Large);
		minusButton.setTypeface(null, Typeface.BOLD);
		LinearLayout.LayoutParams minBtnLayParams = new LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		Button plusButton = new Button(context);
		plusButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				plusButtonClicked();
			}
		});
		plusButton.setText("+");
		plusButton.setMinEms(2);
		plusButton.setTextAppearance(context,
				android.R.style.TextAppearance_Large);
		plusButton.setTypeface(null, Typeface.BOLD);

		_DisplayButton = new Button(context);
		_DisplayButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				displayButtonClicked();
			}
		});
		_DisplayButton.setMinEms(6);
		_DisplayButton.setTextAppearance(context,
				android.R.style.TextAppearance_Large);

		addView(minusButton, minBtnLayParams);
		addView(_DisplayButton, minBtnLayParams);
		addView(plusButton, minBtnLayParams);
		setCalendar(Calendar.getInstance());
	}

	private void updateDisplay() {
		String display = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM).format(_Calendar.getTime());
		_DisplayButton.setText(display);
	}

	private void minusButtonClicked() {
		_Calendar.add(Calendar.DAY_OF_MONTH, -1);
		updateDisplay();
	}

	private void plusButtonClicked() {
		_Calendar.add(Calendar.DAY_OF_MONTH, 1);
		updateDisplay();
	}

	private void displayButtonClicked() {
		DatePickerDialog dialog = new DatePickerDialog(getContext(), this,
				_Calendar.get(Calendar.YEAR), _Calendar.get(Calendar.MONTH),
				_Calendar.get(Calendar.DAY_OF_MONTH));
		dialog.show();
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		_Calendar.set(year, monthOfYear, dayOfMonth);
		updateDisplay();
	}

}
