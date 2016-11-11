package com.pigdogbay.weighttrackerpro;


import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.pigdogbay.weighttrackerpro.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectToDriveFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private static final int REQUEST_CODE_RESOLUTION = 3;
    private static final int MAX_RETRIES = 3;

    private int resolutionCounter;
    private boolean isTaskRunning;
    private GoogleApiClient googleApiClient;
    private TextView statusTextView;


    public ConnectToDriveFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connect_to_drive, container, false);
        view.findViewById(R.id.connectConnectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect();
            }
        });
        statusTextView = (TextView) view.findViewById(R.id.connectStatus);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
    }

    private void connect() {
        statusTextView.setText(R.string.drive_status_connecting);
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addOnConnectionFailedListener(this)
                    .addConnectionCallbacks(this)
                    .build();
        }
        if (!googleApiClient.isConnecting()) {
            googleApiClient.connect();
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        statusTextView.setText(R.string.drive_status_disconnected);
        resolutionCounter++;
        if (resolutionCounter > MAX_RETRIES) {
            return;
        }
        // Called whenever the API client fails to connect.
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), result.getErrorCode(), 0).show();
            return;
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an
        // authorization
        // dialog is displayed to the user.
        try {
            result.startResolutionForResult(getActivity(), REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        statusTextView.setText(R.string.connect_status_connected);
    }

    @Override
    public void onConnectionSuspended(int i) {
        statusTextView.setText(R.string.drive_status_disconnected);

    }
}
