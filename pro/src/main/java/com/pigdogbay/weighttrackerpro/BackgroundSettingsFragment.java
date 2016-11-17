package com.pigdogbay.weighttrackerpro;

import com.pigdogbay.lib.mvp.BackgroundColorModel;
import com.pigdogbay.weightrecorder.model.MainModel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.RadioButton;

public class BackgroundSettingsFragment extends Fragment implements OnClickListener{
	BackgroundColorModel _BackgroundColorModel;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainModel mainModel = new MainModel(getContext());
    	_BackgroundColorModel = mainModel.createBackgroundColorModel();
	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_background_setting, container, false);
    	dataExchangeToControls(rootView);
    	rootView.findViewById(R.id.BackgroundSettingSkyBlue).setOnClickListener(this);
    	rootView.findViewById(R.id.BackgroundSettingDarkPink).setOnClickListener(this);
    	rootView.findViewById(R.id.BackgroundSettingLightPink).setOnClickListener(this);
    	rootView.findViewById(R.id.BackgroundSettingTurquoise).setOnClickListener(this);
    	rootView.findViewById(R.id.BackgroundSettingYellow).setOnClickListener(this);
    	rootView.findViewById(R.id.BackgroundSettingGrey).setOnClickListener(this);
    	rootView.findViewById(R.id.BackgroundSettingWhite).setOnClickListener(this);
        return rootView;
    }

    @Override
	public void onClick(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        if (!checked){
        	return;
        }
        switch(view.getId()) {
        case R.id.BackgroundSettingSkyBlue:
        	_BackgroundColorModel.setBackgroundIndex(BackgroundColorModel.SKY_BLUE_INDEX);
            break;
        case R.id.BackgroundSettingDarkPink:
        	_BackgroundColorModel.setBackgroundIndex(BackgroundColorModel.DARK_PINK_INDEX);
            break;
        case R.id.BackgroundSettingLightPink:
        	_BackgroundColorModel.setBackgroundIndex(BackgroundColorModel.LIGHT_PINK_INDEX);
            break;
        case R.id.BackgroundSettingTurquoise:
        	_BackgroundColorModel.setBackgroundIndex(BackgroundColorModel.TURQUOISE_INDEX);
            break;
        case R.id.BackgroundSettingYellow:
        	_BackgroundColorModel.setBackgroundIndex(BackgroundColorModel.YELLOW_INDEX);
            break;
        case R.id.BackgroundSettingGrey:
        	_BackgroundColorModel.setBackgroundIndex(BackgroundColorModel.GREY_INDEX);
            break;
        case R.id.BackgroundSettingWhite:
        	_BackgroundColorModel.setBackgroundIndex(BackgroundColorModel.WHITE_INDEX);
            break;
        }
		
	}
    private void dataExchangeToControls(View view)
    {
    	int color = _BackgroundColorModel.getBackgroundIndex();
    	int radioButtonId = R.id.BackgroundSettingSkyBlue;
    	switch (color)
    	{
		case BackgroundColorModel.DARK_PINK_INDEX:
			radioButtonId = R.id.BackgroundSettingDarkPink;
			break;
		case BackgroundColorModel.LIGHT_PINK_INDEX:
			radioButtonId = R.id.BackgroundSettingLightPink;
			break;
		case BackgroundColorModel.TURQUOISE_INDEX:
			radioButtonId = R.id.BackgroundSettingTurquoise;
			break;
		case BackgroundColorModel.YELLOW_INDEX:
			radioButtonId = R.id.BackgroundSettingYellow;
			break;
		case BackgroundColorModel.GREY_INDEX:
			radioButtonId = R.id.BackgroundSettingGrey;
			break;
		case BackgroundColorModel.WHITE_INDEX:
			radioButtonId = R.id.BackgroundSettingWhite;
			break;
    	}
    	RadioButton radioButton = (RadioButton) view.findViewById(radioButtonId);
    	radioButton.setChecked(true);
    }
}
