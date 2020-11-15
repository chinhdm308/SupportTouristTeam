package dmc.supporttouristteam.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import dmc.supporttouristteam.Models.User;

public class Common {

    public static Date convertTimeStampToDate(long time) {
        return new Date(new Timestamp(time).getTime());
    }

    public static String getDateFormatted(Date date) {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm").format(date).toString();
    }

    public static User loggedUser;

    public static void reqPermission(Activity activity) {
        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(activity.getApplicationContext(), "You must  accept permission to use app", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    public static void requestPermission(Activity activity, String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(activity,
                new String[]{permissionName}, permissionRequestCode);
    }

    public static void showExplanation(Context context, Activity activity, String title, String message,
                                 String permission,
                                 int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Chấp nhận", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(activity, permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }
}
