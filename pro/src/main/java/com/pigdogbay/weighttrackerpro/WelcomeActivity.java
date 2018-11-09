package com.pigdogbay.weighttrackerpro;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.pigdogbay.lib.mvp.BackgroundColorPresenter;
import com.pigdogbay.lib.mvp.IBackgroundColorView;
import com.pigdogbay.lib.utils.ActivityUtils;
import com.pigdogbay.weightrecorder.model.MainModel;

public class WelcomeActivity extends AppCompatActivity implements IBackgroundColorView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        setTitle(R.string.title_welcome);
        MainModel mainModel = new MainModel(this);
        BackgroundColorPresenter  bcp = new BackgroundColorPresenter(this,mainModel.createBackgroundColorModel());
        bcp.updateBackground();
        mainModel.close();

        WelcomeWizardFragment.WelcomePagerAdapter adapter = new WelcomeWizardFragment.WelcomePagerAdapter(getSupportFragmentManager(), this);
        ViewPager viewPager = findViewById(R.id.welcome_wizard_viewpager);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void setBackgroundColor(int id) {
        ActivityUtils.setBackground(this, R.id.welcome_wizard_viewpager, id);
    }
}
