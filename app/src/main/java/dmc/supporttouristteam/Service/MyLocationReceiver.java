package dmc.supporttouristteam.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.LocationResult;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmc.supporttouristteam.Utils.Common;
import dmc.supporttouristteam.Utils.Config;

public class MyLocationReceiver extends BroadcastReceiver {
    public static final String ACTION = "dmc.supporttouristteam.UPDATE_LOCATION";

    DatabaseReference publicLocationRef;
    String uid;

    public MyLocationReceiver() {
        publicLocationRef = FirebaseDatabase.getInstance().getReference(Config.RF_PUBLIC_LOCATION);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        uid = Common.currentUser.getUid();
        if (intent != null) {
            final String action = intent.getAction();
            if (action.equals(ACTION)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    Location location = result.getLastLocation();
                    publicLocationRef.child(Common.currentUser.getUid())
                            .setValue(location, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    ref.child("id").setValue(uid);
                                }
                            });
                }
            }
        }
    }
}
