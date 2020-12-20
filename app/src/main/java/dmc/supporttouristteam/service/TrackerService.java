package dmc.supporttouristteam.service;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmc.supporttouristteam.R;
import dmc.supporttouristteam.util.Common;

public class TrackerService extends Service {
//    private static final String TAG = TrackerService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildNotification();
        loginToFirebase();
    }

    //Create the persistent notification//
    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void buildNotification() {
        // Create Notification Channel
        String CHANNEL_ID = "dmc.supporttouristteam";
        String channelName = "dmc.supporttouristteam";

        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the persistent notification
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text))
                //Make this notification ongoing so it can’t be dismissed by the user//
                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.drawable.ic_tracker)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create Notification Channel
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        startForeground(1, notification);
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(Common.TAG, "received stop broadcast");
            // Unregister the BroadcastReceiver when the notification is tapped
            // Stop the service when the notification is tapped
            unregisterReceiver(stopReceiver);
            // Stop the Service
            stopSelf();
        }
    };

    private void loginToFirebase() {
        // Functionality coming next step
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.d(Common.TAG, "firebase auth success");
            requestLocationUpdates();
        }
    }

    //Initiate the request to track the device's location//
    private void requestLocationUpdates() {
        // Functionality coming next step
        LocationRequest request = new LocationRequest();
        // Specify how often your app should request the device’s location
        request.setInterval(10000);
        request.setFastestInterval(5000);
        // Get the most accurate location data available
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        // If the app currently has access to the location permission then request location updates
        if (permission == PackageManager.PERMISSION_GRANTED) {
            // MyRequest location updates and when an update is
            // received, store the location in Firebase
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Common.RF_PUBLIC_LOCATION);
                        Location location = locationResult.getLastLocation();
                        if (location != null) {
                            Log.d(Common.TAG, "location update: " + location.getLatitude() + " <> " + location.getLongitude());
                            ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(location);
                        }
                    }
                }
            }, null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(Common.TAG, "stop service : onDestroy");
        stopSelf();
    }
}
