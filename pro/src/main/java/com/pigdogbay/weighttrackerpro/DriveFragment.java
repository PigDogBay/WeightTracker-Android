package com.pigdogbay.weighttrackerpro;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.pigdogbay.weightrecorder.utils.SharedDriveFragmentCode;

public class DriveFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks
{
    public static final String TAG = "drive";

    private TextView statusTextView;
    private SharedDriveFragmentCode sharedDriveFragmentCode;


    public DriveFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drive, container, false);
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (supportActionBar != null)
        {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        setHasOptionsMenu(true);
        wireUpControls(view);
        sharedDriveFragmentCode = new SharedDriveFragmentCode(getActivity());
        sharedDriveFragmentCode.setStatusTextView(statusTextView);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_drive, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case (R.id.menu_drive_delete):
                sharedDriveFragmentCode.deleteFolder();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void wireUpControls(View view)
    {
        view.findViewById(R.id.driveSaveBtn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sharedDriveFragmentCode.save();
            }
        });
        view.findViewById(R.id.driveRestoreBtn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sharedDriveFragmentCode.restore();
            }
        });
        view.findViewById(R.id.driveQueryBtn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sharedDriveFragmentCode.query();
            }
        });
        statusTextView = (TextView) view.findViewById(R.id.driveStatus);
    }


    @Override
    public void onResume()
    {
        super.onResume();
        sharedDriveFragmentCode.connect(this);
    }

    @Override
    public void onPause()
    {
        sharedDriveFragmentCode.disconnect();
        super.onPause();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        sharedDriveFragmentCode.onConnectionSuspended();
        sharedDriveFragmentCode.query();
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        sharedDriveFragmentCode.onConnectionSuspended();
    }

}
