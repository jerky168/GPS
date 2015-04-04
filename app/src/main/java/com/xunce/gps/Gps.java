package com.xunce.gps;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by jk on 2015/4/3.
 * @author jk
 */
public class Gps {
    private Location location = null;
    private LocationManager locationManager = null;
    private Context context = null;

    public Gps(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        location = locationManager.getLastKnownLocation(getProvider());
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
    }

    private String getProvider() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.ACCURACY_LOW);

        return locationManager.getBestProvider(criteria,true);
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location l) {
            if (l != null) {
                location = l;
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {
            Location l = locationManager.getLastKnownLocation(s);
        }

        @Override
        public void onProviderDisabled(String s) {
            location = null;
        }
    };

    public Location getLocation() {
        return location;
    }

    public void closeLocation() {
        if (locationManager != null) {
            if (locationListener != null) {
                locationManager.removeUpdates(locationListener);
                locationListener = null;
            }
            locationManager = null;
        }
    }
}
