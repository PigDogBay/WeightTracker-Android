package com.pigdogbay.weighttrackerpro;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.pigdogbay.lib.usercontrols.CustomNumberPicker;
import com.pigdogbay.weightrecorder.model.IUnitConverter;
import com.pigdogbay.weightrecorder.model.Reading;
import com.pigdogbay.weightrecorder.model.UnitConverterAdapter;
import com.pigdogbay.weightrecorder.model.UnitConverterFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class EditFragment extends Fragment
{
	protected static final int RESULT_WEIGHT_SPEECH = 1;
	protected static final int RESULT_COMMENT_SPEECH = 2;

	UnitConverterAdapter _UnitConverterAdapter;
	CustomNumberPicker _NumberPicker;

	private EditText _EditTextComment;
	private IUnitConverter _WeightConverter = UnitConverterFactory.create(UnitConverterFactory.KILOGRAMS_TO_KILOGRAMS); 
	private DatePickerSpinner _DatePickerSpinner;

	protected void setWeightConvert(IUnitConverter weightConverter)
	{
		_WeightConverter = weightConverter;
		_UnitConverterAdapter = new UnitConverterAdapter(_WeightConverter);
		_NumberPicker.getController().setNumberPickerValue(_UnitConverterAdapter);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// Views and contexts have now been created
		super.onActivityCreated(savedInstanceState);
		_NumberPicker = (CustomNumberPicker)getView().findViewById(R.id.WeightSpinner);

		_EditTextComment = (EditText) getView()
				.findViewById(R.id.EditFragmentComment);
		_DatePickerSpinner = (DatePickerSpinner) getView()
				.findViewById(R.id.EditFragmentDatePickerSpinner);
		getView().findViewById(R.id.EditFragmentCommentSpeak)
				.setOnClickListener(new OnClickListener()
				{
					public void onClick(View v)
					{
						startCommentSpeechToText();

					}
				});
	}

	protected void setReading(Reading reading)
	{
		_EditTextComment.setText(reading.getComment());
		_NumberPicker.setValue(_WeightConverter.convert(reading.getWeight()));
		Calendar cal = Calendar.getInstance();
		cal.setTime(reading.getDate());
		_DatePickerSpinner.setCalendar(cal);
	}

	protected Reading getReading()
	{
		Reading reading = new Reading();
		// convert to kilograms
		double weight = _WeightConverter.inverse(_NumberPicker.getValue());
		reading.setWeight(weight);
		reading.setDate(getDateTime());
		reading.setComment(_EditTextComment.getText().toString());
		return reading;
	}

	private Date getDateTime()
	{
		Calendar cal = _DatePickerSpinner.getCalendar();
		return cal.getTime();
	}

	protected void hideKeyboard()
	{
		try
		{
			Context context = getActivity();
			InputMethodManager imm = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm != null)
			{
				imm.hideSoftInputFromWindow(_EditTextComment.getWindowToken(),
						0);
			}
		}
		catch (Exception e)
		{
		}
	}

	private void startCommentSpeechToText()
	{
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				getString(R.string.edit_speak_prompt_comment));
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
		try
		{
			startActivityForResult(intent, RESULT_COMMENT_SPEECH);
			_EditTextComment.setText("");
		}
		catch (ActivityNotFoundException a)
		{
			Toast.makeText(getActivity(), getString(R.string.edit_no_speech),
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK || null == data)
		{
			return;
		}
		List<String> text = data
				.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
		if (text.size() == 0)
		{
			return;
		}
		switch (requestCode)
		{
		case RESULT_COMMENT_SPEECH:
			_EditTextComment.setText(text.get(0));
			break;
		}
	}

	protected void setWeight(double weight)
	{
		_NumberPicker.setValue(weight);
	}

	protected double getWeight()
	{
		return _NumberPicker.getValue();
	}

}
