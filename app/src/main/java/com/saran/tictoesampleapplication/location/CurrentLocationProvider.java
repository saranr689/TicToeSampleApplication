package com.saran.tictoesampleapplication.location;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * @author saran.
 */

public class CurrentLocationProvider implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final int REQUEST_CODE_LOCATION_PERMISSION = 32133;
    public static final int REQUEST_CODE_LOCATION_SETTINGS = 32136;
    private static final String TAG = CurrentLocationProvider.class.getSimpleName();
    private final GoogleApiClient mGoogleApiClient;
    private Context mContext;
    private Location mLastLocation;
    private OnCurrentLocationReceivedListener mCurrentLocationReceivedListener;


    public CurrentLocationProvider(Context context) {
        mGoogleApiClient = AppGoogleApiClient.getInstance(context, this, this);
        mGoogleApiClient.connect();
        this.mContext = context;
    }

    /**
     * Get current location after permissions granted.
     *
     * @param onCurrentLocationReceivedListener
     */
    public void getCurrentLocation(OnCurrentLocationReceivedListener onCurrentLocationReceivedListener) throws SecurityException {

        this.mCurrentLocationReceivedListener = onCurrentLocationReceivedListener;
        try {
            if (mGoogleApiClient.isConnected()) {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                if (mLastLocation == null) {
                    startLocationUpdates();
                } else {
                    mCurrentLocationReceivedListener.onLocationReceived(mLastLocation);
                }
            } else {
                mGoogleApiClient.connect();
            }
        } catch (Exception ex) {
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (mLastLocation == null) {
            getCurrentLocation(mCurrentLocationReceivedListener);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    /**
     * Start location updates while moving.
     */
    private void startLocationUpdates() throws SecurityException {
        //checkGpsSettings();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, getLocationRequest(), CurrentLocationProvider.this);
    }

    private LocationRequest getLocationRequest() {
        try {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setNumUpdates(1);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            return mLocationRequest;

        } catch (Exception ex) {
            // Failed to start location updates
        }
        return null;
    }

    public void checkGpsSettings(final OnGPSSettingsChangeListener onGPSSettingsChangeListener){
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(getLocationRequest());

        PendingResult<LocationSettingsResult> locationSettingsResultPendingResult = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        locationSettingsResultPendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                Status status = locationSettingsResult.getStatus();
                LocationSettingsStates locationSettingsStates = locationSettingsResult.getLocationSettingsStates();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        onGPSSettingsChangeListener.onGPSEnabled();
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult((Activity) mContext,REQUEST_CODE_LOCATION_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                }
            }
        });
    }

    /**
     * Stop location updates
     */
    private void stopLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        } catch (Exception ex) {
            // Failed to stop location updates
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(mCurrentLocationReceivedListener != null){
            mCurrentLocationReceivedListener.onLocationReceived(location);
        }
    }

    public interface OnCurrentLocationReceivedListener {
        void onLocationReceived(Location location);
    }

    public interface OnGPSSettingsChangeListener {
        void onGPSEnabled();
    }
}
