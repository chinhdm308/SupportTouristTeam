package dmc.supporttouristteam.services;

import android.content.Context;
import android.widget.Toast;

public class CommonService {
    public static void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
