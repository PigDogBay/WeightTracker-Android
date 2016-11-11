package com.pigdogbay.weighttrackerpro;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.pigdogbay.weightrecorder.utils.SharedDriveFragmentCode;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectToDriveFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks
{

    private SharedDriveFragmentCode sharedDriveFragmentCode;

    public ConnectToDriveFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connect_to_drive, container, false);
        view.findViewById(R.id.connectConnectBtn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sharedDriveFragmentCode.connect(ConnectToDriveFragment.this);
            }
        });
        TextView statusTextView = (TextView) view.findViewById(R.id.connectStatus);
        sharedDriveFragmentCode = new SharedDriveFragmentCode(getActivity());
        sharedDriveFragmentCode.setStatusTextView(statusTextView);
        return view;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        sharedDriveFragmentCode.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        sharedDriveFragmentCode.onConnected();
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        sharedDriveFragmentCode.onConnectionSuspended();
    }
}
