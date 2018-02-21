package com.example.krishna1393.safetyforwomen;
import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import java.text.MessageFormat;


public class MyService extends Service {
    private final static int PERMISSION_REQUEST = 1;
    NotificationManager mnofification;
    public static final String BROADCAST_ACTION = "Hello World";
    final static String MY_ACTION = "MY_ACTION";
    private static final int TWO_MINUTES = 5000;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location lastlocation = null;
    Context context;
    Intent intent;

    @Override
    public void onCreate() {

        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
        context = this;


    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        checkPermission();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 0, listener);
        return START_STICKY;

    }


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            Log.v("", "no permissions");
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            return true;
        }
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;
        if (isSignificantlyNewer) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        }
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        locationManager.removeUpdates(listener);
        mnofification.cancelAll();
    }
    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }
    public class MyLocationListener implements LocationListener {
        public void onLocationChanged(final Location loc) {
            //Log.i("**********", "Location changed");
            if (isBetterLocation(loc, lastlocation)) {
                loc.getLatitude();
                loc.getLongitude();
                Intent intent = new Intent();
                intent.setAction(MY_ACTION);
                intent.putExtra("Latitude", loc.getLatitude());
                intent.putExtra("Longitude", loc.getLongitude());
                intent.putExtra("Provider", loc.getProvider());
                sendBroadcast(intent);
                String uri = formatLocation(loc, "https://maps.google.com/?q={0},{1}");
                showForegroundNotification(loc.getLatitude(), loc.getLongitude(), uri);
            }
        }
        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }
        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public String formatLocation(Location location, String format) {
            return MessageFormat.format(format,
                    location.getLatitude(), location.getLongitude());
        }
    }

    private void showForegroundNotification(Double latitude, Double logitude, String uri) {

        mnofification = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notifyID = 1;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_room_black_24dp)
                        .setContentTitle("Location")
                        .setContentText("latitude" + latitude + "longitude" + logitude);
        Intent notifintent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        PendingIntent pintent = PendingIntent.getActivity(this, 0, Intent.createChooser(notifintent, getString(R.string.view_location_via)), 0);
        mBuilder.setContentIntent(pintent);
        mnofification.notify(notifyID, mBuilder.build());

    }

}