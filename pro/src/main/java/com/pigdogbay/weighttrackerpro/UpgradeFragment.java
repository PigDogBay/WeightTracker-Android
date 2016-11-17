package com.pigdogbay.weighttrackerpro;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.pigdogbay.weightrecorder.utils.SharedDriveFragmentCode;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpgradeFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks {
    public static final String TAG = "upgrade";
    private SharedDriveFragmentCode sharedDriveFragmentCode;
    private Button connectBtn;


    public UpgradeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (supportActionBar != null)
        {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        View view = inflater.inflate(R.layout.fragment_upgrade, container, false);
        connectBtn = (Button) view.findViewById(R.id.upgradeConnectBtn);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect();
            }
        });
        TextView statusTextView = (TextView) view.findViewById(R.id.upgradeStatus);
        sharedDriveFragmentCode = new SharedDriveFragmentCode(getActivity());
        sharedDriveFragmentCode.setStatusTextView(statusTextView);

        return view;
    }
    private void connect() {
        if (!sharedDriveFragmentCode.isConnected()) {
            sharedDriveFragmentCode.connect(this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        sharedDriveFragmentCode.save();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        sharedDriveFragmentCode.onActivityResult(requestCode,resultCode, this);
    }
}
