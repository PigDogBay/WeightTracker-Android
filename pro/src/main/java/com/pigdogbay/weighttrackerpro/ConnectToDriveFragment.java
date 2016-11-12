package com.pigdogbay.weighttrackerpro;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
public class ConnectToDriveFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks {

    private SharedDriveFragmentCode sharedDriveFragmentCode;
    private Button connectBtn;
    private TextView infoText;

    public ConnectToDriveFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connect_to_drive, container, false);
        connectBtn = (Button) view.findViewById(R.id.connectConnectBtn);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect();
            }
        });
        TextView statusTextView = (TextView) view.findViewById(R.id.connectStatus);
        infoText = (TextView) view.findViewById(R.id.connectInformationText);
        sharedDriveFragmentCode = new SharedDriveFragmentCode(getActivity());
        sharedDriveFragmentCode.setStatusTextView(statusTextView);
        return view;
    }

    private void connect() {
        if (sharedDriveFragmentCode.isConnected()) {
            sharedDriveFragmentCode.restore();
        } else {
            sharedDriveFragmentCode.connect(ConnectToDriveFragment.this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedDriveFragmentCode.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        sharedDriveFragmentCode.onConnected();
        connectBtn.setText(R.string.connect_restore_button);
        infoText.setText(R.string.connect_restore_help_text);

    }

    @Override
    public void onConnectionSuspended(int i) {
        sharedDriveFragmentCode.onConnectionSuspended();
    }
}
