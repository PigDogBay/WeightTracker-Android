package com.pigdogbay.weighttrackerpro;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.pigdogbay.lib.apprate.AppRate;
import com.pigdogbay.lib.mvp.BackgroundColorPresenter;
import com.pigdogbay.lib.mvp.IBackgroundColorView;
import com.pigdogbay.lib.utils.ActivityUtils;
import com.pigdogbay.weightrecorder.model.MainModel;
import com.pigdogbay.weightrecorder.model.Reading;
import com.pigdogbay.weightrecorder.model.SettingsUtils;
import com.pigdogbay.weightrecorder.utils.AdsVariation;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnSharedPreferenceChangeListener,IBackgroundColorView{
	public static final String TAG = "WeightTracker";

	BackgroundColorPresenter _BackgroundColorPresenter;
	//Hack fro ConnectToDriveFragment
	public int resultCode, requestCode, onActivityCount;

	private AdsVariation adsVariation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		adsVariation = new AdsVariation(this);
		adsVariation.setUp();

		MainModel mainModel = new MainModel(this);
		_BackgroundColorPresenter = new BackgroundColorPresenter(this,mainModel.createBackgroundColorModel());
		_BackgroundColorPresenter.updateBackground();
				
		//if app has been rotated, then skip this part as the existing fragment will have already been recreated
		if (getSupportFragmentManager().findFragmentById(R.id.root_layout)==null)
		{
			try {
				checkFirstTime(mainModel);
				checkRate();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			showHome();
		}
		mainModel.close();
	}
	@Override
    protected void onResume() {
		adsVariation.resume();
    	super.onResume();
		PreferenceManager.getDefaultSharedPreferences(this)
		.registerOnSharedPreferenceChangeListener(this);
    }
    @Override
    protected void onPause() {
		adsVariation.pause();
    	super.onPause();
		PreferenceManager.getDefaultSharedPreferences(this)
		.unregisterOnSharedPreferenceChangeListener(this);
    }
	private void checkRate() {
		try {
			new AppRate(this)
					.setMinDaysUntilPrompt(7)
					.setMinLaunchesUntilPrompt(5)
					// .setMinDaysUntilPrompt(0)
					// .setMinLaunchesUntilPrompt(0)
					.init();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void checkFirstTime(MainModel mainModel) {
		if (!mainModel.getIsFirstTime()) {
			SettingsUtils.setDefaultSettings(Locale.getDefault(), new MainModel(this));
			mainModel.setIsFirstTime(true);
			showWelcome();
		}
	}
	
	private AlertDialog.Builder createRateDialog() {
		return new AlertDialog.Builder(this)
				.setTitle(R.string.rate_dialog_title)
				.setMessage(R.string.rate_dialog_message)
				.setPositiveButton(R.string.rate_dialog_positive, null)
				.setNegativeButton(R.string.rate_dialog_negative, null)
				.setNeutralButton(R.string.rate_dialog_neutral, null);
	}

	@Override
	public void onBackPressed() {
		Fragment f= getSupportFragmentManager().findFragmentById(R.id.root_layout);
		String tag="";
		if (f!=null){
			tag = f.getTag();
		}
		if (HomeFragment.TAG.equals(tag))
		{
			super.onBackPressed();
		}
		else
		{
			navigateBack(tag);
		}
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Fragment f= getSupportFragmentManager().findFragmentById(R.id.root_layout);
		String tag="";
		if (f!=null){
			tag = f.getTag();
		}
		switch(item.getItemId())
		{
			case android.R.id.home:
				navigateBack(tag);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	public void navigateBack(String tag){
		if (HomeFragment.TAG.equals(tag))
		{
			//do nothing
		}
		else if (EditReadingFragment.TAG.equals(tag))
		{
			showList();
		}
		else if (ImportFragment.TAG.equals(tag))
		{
			showList();
		}
		else
		{
			showHome();
		}
	}
	private void replaceFragment(Fragment fragment, String tag) {
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.root_layout, fragment, tag)
				.commit();
	}
	
	
	public void showHome(){
		setTitle(getString(R.string.title_main));
		replaceFragment(new HomeFragment(), HomeFragment.TAG);
	}
	public void showAbout(){
		setTitle(getString(R.string.title_about));
		replaceFragment(new AboutFragment(), AboutFragment.TAG);
	}
	public void showWelcome(){
		requestCode=-1;
		resultCode=RESULT_CANCELED;
		onActivityCount=0;
		startActivity(new Intent(this,WelcomeActivity.class));
	}
	public void showNew(){
		setTitle(getString(R.string.title_new));
		replaceFragment(new NewReadingFragment(), NewReadingFragment.TAG);
	}
	public void showEdit(Reading reading){
		setTitle(getString(R.string.title_edit));
		EditReadingFragment frag = new EditReadingFragment();
		frag.setReadingToEdit(reading);
		replaceFragment(frag, EditReadingFragment.TAG);
	}
	public void showList(){
		setTitle(getString(R.string.title_list));
		replaceFragment(new ReadingListFragment(), ReadingListFragment.TAG);
	}
	public void showSettings(){
		setTitle(getString(R.string.title_settings));
		replaceFragment(new SettingsWizardFragment(), SettingsWizardFragment.TAG);
	}
	public void showChart(){
		setTitle(getString(R.string.title_chart));
		replaceFragment(new ChartFragment(), ChartFragment.TAG);
	}
	public void showReport(){
		setTitle(getString(R.string.title_report));
		replaceFragment(new ReportFragment(), ReportFragment.TAG);
	}
	public void showHelp(){
	    AboutFragment.showWebPage(this,R.string.user_guide_url);
	}
	public void showSync() {
		setTitle(getString(R.string.title_drive));
		replaceFragment(new DriveFragment(), DriveFragment.TAG);
	}

	public void showImport(){
		setTitle(getString(R.string.title_import));
		replaceFragment(new ImportFragment(), ImportFragment.TAG);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		if (key.equals(getString(R.string.code_pref_background_colour))){
			_BackgroundColorPresenter.updateBackground();
		}
	}	
	@Override
	public void setBackgroundColor(int id) {
		ActivityUtils.setBackground(this, R.id.root_layout, id);
	}

	/***
	 * onActivityResult does not work for fragment, do it myself!
     */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		onActivityCount++;
		this.requestCode = requestCode;
		this.resultCode = resultCode;
		Fragment f= getSupportFragmentManager().findFragmentById(R.id.root_layout);
		if (f!=null){
			f.onActivityResult(requestCode,resultCode,data);
		}
	}


}
