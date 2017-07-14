package com.saran.tictoesampleapplication.location;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * @author saran.
 */
public class AppGoogleApiClient {

    private static GoogleApiClient mGoogleApiClient;

    private AppGoogleApiClient(Context context, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {
        buildGoogleApiClient(context, connectionCallbacks, connectionFailedListener);
    }

    public static GoogleApiClient getInstance(Context context, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {
        AppGoogleApiClient appGoogleApiClient = new AppGoogleApiClient(context, connectionCallbacks, connectionFailedListener);
        return appGoogleApiClient.getGoogleApiClient();
    }

    /**
     * Build Google API client.
     */
    private synchronized void buildGoogleApiClient(Context context, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {

        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(connectionCallbacks)
                    .addOnConnectionFailedListener(connectionFailedListener)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }
}
