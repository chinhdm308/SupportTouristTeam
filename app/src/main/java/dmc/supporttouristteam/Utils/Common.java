package dmc.supporttouristteam.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import androidx.core.app.ActivityCompat;

import com.google.gson.JsonObject;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.sql.Timestamp;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import dmc.supporttouristteam.Models.GroupInfo;
import dmc.supporttouristteam.Models.Place;
import dmc.supporttouristteam.R;

public class Common {

    public static GroupInfo groupClicked;

    public static List<GroupInfo> groupInfoList;

    public static void openGallery(Activity activity) {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(activity);
    }

    public static Place parseJsonPlace(JsonObject object) {
        JsonObject location = object.getAsJsonObject("geometry").getAsJsonObject("location");
        Place place = new Place();
        place.setLat(location.get("lat").getAsDouble());
        place.setLng(location.get("lng").getAsDouble());
        place.setIcon(object.get("icon").getAsString());
        place.setName(object.get("name").getAsString());
        place.setPlace_id(object.get("place_id").getAsString());
        place.setVicinity(object.get("vicinity").getAsString());
        return place;
    }

    private static int dp(Context context, float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(context.getResources().getDisplayMetrics().density * value);
    }

    public static Bitmap createUserBitmap(Context context, Bitmap bm) {
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(dp(context,62), dp(context, 76), Bitmap.Config.ARGB_8888);
            result.eraseColor(Color.TRANSPARENT);
            Canvas canvas = new Canvas(result);
            Drawable drawable = context.getDrawable(R.drawable.livepin);
            drawable.setBounds(0, 0, dp(context, 62), dp(context, 76));
            drawable.draw(canvas);

            Paint roundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            RectF bitmapRect = new RectF();
            canvas.save();

            Bitmap bitmap = bm;

            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.avatar);
            }

            if (bitmap != null) {
                BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                Matrix matrix = new Matrix();
                float scale = dp(context, 52) / (float) bitmap.getWidth();
                matrix.postTranslate(dp(context, 5), dp(context, 5));
                matrix.postScale(scale, scale);
                roundPaint.setShader(shader);
                shader.setLocalMatrix(matrix);
                bitmapRect.set(dp(context, 5), dp(context, 5), dp(context, 52 + 5), dp(context, 52 + 5));
                canvas.drawRoundRect(bitmapRect, dp(context, 26), dp(context, 26), roundPaint);
            }
            canvas.restore();
            canvas.setBitmap(bitmap);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    // Xóa dấu trong tiếng việt
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
