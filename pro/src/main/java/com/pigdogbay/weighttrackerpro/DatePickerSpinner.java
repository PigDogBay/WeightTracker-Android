package com.pigdogbay.weighttrackerpro;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;

public class DatePickerSpinner extends LinearLayout implements
		OnDateSetListener {
	/**
	 * Identifier for the state to save the selected index of
	 * the side spinner.
	 */
	private static String STATE_TIME = "Time";
	private static String STATE_TIME_ZONE = "TimeZone";

	/**
	 * Identifier for the state of the super class.
	 */
	private static String STATE_SUPER_CLASS = "SuperClass";

	private Button _DisplayButton;
	private Calendar _Calendar;

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
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.date_picker_spinner,this);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		_DisplayButton = (Button) this.findViewById(R.id.pickerSetBtn);
		_DisplayButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				displayButtonClicked();
			}
		});
		this.findViewById(R.id.pickerMinusBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				minusButtonClicked();
			}
		});

		this.findViewById(R.id.pickerPlusBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				plusButtonClicked();
			}
		});
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
	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putParcelable(STATE_SUPER_CLASS,super.onSaveInstanceState());
		bundle.putLong(STATE_TIME,_Calendar.getTimeInMillis());
		bundle.putString(STATE_TIME_ZONE,_Calendar.getTimeZone().getID());
		return bundle;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle){
			Bundle bundle = (Bundle)state;
			super.onRestoreInstanceState(bundle.getParcelable(STATE_SUPER_CLASS));
			_Calendar.setTimeZone(TimeZone.getTimeZone(bundle.getString(STATE_TIME_ZONE)));
			_Calendar.setTimeInMillis(bundle.getLong(STATE_TIME));
			updateDisplay();
		}else {
			super.onRestoreInstanceState(state);
		}
	}
	@Override
	protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
		// Makes sure that the state of the child views in the side
		// spinner are not saved since we handle the state in the
		// onSaveInstanceState.
		super.dispatchFreezeSelfOnly(container);
	}

	@Override
	protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
		// Makes sure that the state of the child views in the side
		// spinner are not restored since we handle the state in the
		// onSaveInstanceState.
		super.dispatchThawSelfOnly(container);
	}


}
