package dmc.supporttouristteam.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Timestamp;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import dmc.supporttouristteam.Models.GroupInfo;

public class Common {
    public static FirebaseAuth FB_AUTH = FirebaseAuth.getInstance();
    public static GroupInfo groupClicked;
    public static FirebaseUser currentUser;


    public static String removeAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }

    public static String convertTimeStampToString(long time) {
        Date date = new Date(new Timestamp(time).getTime());
        return new SimpleDateFormat("dd-MM-yyyy HH:mm").format(date);
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
                .setNegativeButton("Không, Cảm ơn", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(activity, permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }
}
