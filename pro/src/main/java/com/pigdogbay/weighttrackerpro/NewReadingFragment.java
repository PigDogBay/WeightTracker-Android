package com.pigdogbay.weighttrackerpro;

import com.pigdogbay.lib.utils.ActivityUtils;
import com.pigdogbay.weightrecorder.model.IUnitConverter;
import com.pigdogbay.weightrecorder.model.MainModel;
import com.pigdogbay.weightrecorder.model.Reading;
import com.pigdogbay.weightrecorder.utils.AutoBackup;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class NewReadingFragment extends EditFragment {
    public static final String TAG = "new";
    private MainModel _MainModel;
    private CheckBox _ChkBxStayOnScreen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _MainModel = new MainModel(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_reading, container, false);
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (supportActionBar!=null){supportActionBar.setDisplayHomeAsUpEnabled(true);}
        setHasOptionsMenu(true);

        rootView.findViewById(R.id.AddReadingBtnAdd)
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        onEnterClick();
                    }
                });
        _ChkBxStayOnScreen = (CheckBox) rootView.findViewById(R.id.AddReadingStayOnScreen);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadPreferences();
    }

    private void onEnterClick() {
        Reading reading = getReading();
        _MainModel.getDatabase().addReading(reading);
        savePreferences();
        checkAutoBackup();
        Toast.makeText(getActivity(), getString(R.string.addreading_added),
                Toast.LENGTH_SHORT).show();
        if (_ChkBxStayOnScreen.isChecked()) {
            return;
        }
        hideKeyboard();
        //Show well done message if target weight has been reached for the first time
        showCongrats(reading);
        ((MainActivity) getActivity()).navigateBack(TAG);
    }

    private void checkAutoBackup() {
        if (AutoBackup.isBackupDue(getActivity())) {
            AutoBackup.backUpReadings(getActivity());
        }
    }

    private void showCongrats(Reading reading) {
        if (_MainModel.getIsGoalCongratulationsEnabled()) {
            //add a delta when comparing doubles as <= does not work
            double targetWeight = _MainModel.getUserSettings().TargetWeight + 0.01D;
            if (reading.getWeight() < targetWeight) {
                //don't show again until target weight is changed
                _MainModel.setIsGoalCongratulationsEnabled(false);
                ActivityUtils.showInfoDialog(getActivity(), R.string.addreading_reached_goal_title, R.string.addreading_reached_goal_message, R.string.ok);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _MainModel.close();
    }

    private void loadPreferences() {
        IUnitConverter weightConverter = _MainModel.getWeightConverter();
        double lastWeight = _MainModel.getPreferencesHelper().getDouble(R.string.code_pref_last_weight_key, MainModel.DEFAULT_TARGET_WEIGHT);
        lastWeight = weightConverter.convert(lastWeight);
        setWeightConvert(weightConverter);
        setWeight(lastWeight);
    }

    private void savePreferences() {
        double lastWeight = getWeight();
        IUnitConverter weightConverter = _MainModel.getWeightConverter();
        lastWeight = weightConverter.inverse(lastWeight);
        _MainModel.getPreferencesHelper().setDouble(R.string.code_pref_last_weight_key, lastWeight);
    }

}
