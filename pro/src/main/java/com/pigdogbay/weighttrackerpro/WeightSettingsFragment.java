package com.pigdogbay.weighttrackerpro;

import com.pigdogbay.lib.usercontrols.NumberPicker;
import com.pigdogbay.weightrecorder.model.IUnitConverter;
import com.pigdogbay.weightrecorder.model.MainModel;
import com.pigdogbay.weightrecorder.model.UnitConverterAdapter;
import com.pigdogbay.weightrecorder.model.UnitConverterFactory;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;

public class WeightSettingsFragment extends Fragment implements OnClickListener{
	
	MainModel _MainModel;
	NumberPicker _NumberPicker; 
	UnitConverterAdapter _UnitConverterAdapter;
	int _WeightUnitsId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	_MainModel = new MainModel(getActivity());
    	_WeightUnitsId = _MainModel.getWeightUnitsId();
	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weight_settings, container, false);
    	((RadioButton)rootView.findViewById(R.id.rbWeightSettingKilograms)).setOnClickListener(this);
    	((RadioButton)rootView.findViewById(R.id.rbWeightSettingPounds)).setOnClickListener(this);
    	((RadioButton)rootView.findViewById(R.id.rbWeightSettingStones)).setOnClickListener(this);
    	_NumberPicker = (NumberPicker)rootView.findViewById(R.id.WeightSettingsGoal);
    	setupNumberPicker(_MainModel.getTargetWeightInKilograms());
    	setupRadioButtons(rootView);
        return rootView;
    }
    /* 
     * Persist any changes here
     */
    @Override
    public void onPause() {
    	super.onPause();
    	if (_WeightUnitsId!=_MainModel.getWeightUnitsId())
    	{
    		_MainModel.setWeightUnitsId(_WeightUnitsId);
    	}
    	if (_UnitConverterAdapter.getValue()!=_MainModel.getTargetWeight())
    	{
    		_MainModel.setTargetWeight(_UnitConverterAdapter.getValue());
    		//Allow well done message to be shown when goal is reached
    		_MainModel.setIsGoalCongratulationsEnabled(true);
    	}
    }
    void setupRadioButtons(View view)
    {
    	int radioButtonId = R.id.rbWeightSettingPounds; 
    	if (UnitConverterFactory.KILOGRAMS_TO_KILOGRAMS==_WeightUnitsId)
    	{
    		radioButtonId = R.id.rbWeightSettingKilograms;
    	}
    	else if (UnitConverterFactory.KILOGRAMS_TO_STONES==_WeightUnitsId)
    	{
    		radioButtonId = R.id.rbWeightSettingStones;
    		
    	}
    	RadioButton radioButton = (RadioButton) view.findViewById(radioButtonId);
    	radioButton.setChecked(true);
    }
    void setupNumberPicker(double targetWeightInKg)
    {
    	IUnitConverter converter = UnitConverterFactory.create(_WeightUnitsId);
    	_UnitConverterAdapter = new UnitConverterAdapter(converter);
    	_UnitConverterAdapter.setValue(converter.convert(targetWeightInKg));
    	_NumberPicker.getController().setNumberPickerValue(_UnitConverterAdapter);
    }

	@Override
	public void onClick(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        if (!checked){
        	return;
        }
        switch(view.getId()) {
        case R.id.rbWeightSettingKilograms:
        	_WeightUnitsId = UnitConverterFactory.KILOGRAMS_TO_KILOGRAMS;
            break;
        case R.id.rbWeightSettingPounds:
        	_WeightUnitsId = UnitConverterFactory.KILOGRAMS_TO_POUNDS;
            break;
        case R.id.rbWeightSettingStones:
        	_WeightUnitsId = UnitConverterFactory.KILOGRAMS_TO_STONES;
            break;
        default:
        	return;
        }
        setupNumberPicker(_UnitConverterAdapter.getValueInPrimaryUnits());
	}
}
