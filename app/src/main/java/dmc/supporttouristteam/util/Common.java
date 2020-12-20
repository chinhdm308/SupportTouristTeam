package dmc.supporttouristteam.util;

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
import android.net.Uri;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.JsonObject;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import dmc.supporttouristteam.data.model.GroupInfo;
import dmc.supporttouristteam.data.model.LovePlace;
import dmc.supporttouristteam.data.model.Place;
import dmc.supporttouristteam.R;

public class Common {

    public static final String RF_USERS = "USERS";
    public static final String RF_GROUPS = "GROUPS";
    public static final String RF_CHATS = "CHATS";
    public static final String RF_USER_PHOTOS = "USER_PHOTOS";
    public static final String RF_GROUP_PHOTOS = "GROUP_PHOTOS";
    public static final String RF_PUBLIC_LOCATION = "PUBLIC_LOCATION";
    public static final String RF_LOVE_PLACES = "LOVE_PLACES";
    public static final String RF_TOKENS = "TOKENS";

    public static final String EXTRA_GROUP_INFO = "EXTRA_GROUP_INFO";
    public static final String EXTRA_USER = "EXTRA_USER";

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    public static final int MY_REQUEST_CODE = 1;

    public static final String TAG = "DEBUG_APP_DMC";
    public static final String FROM_NAME = "FromName";
    public static final String ACCEPT_LIST = "AcceptList";
    public static final String FROM_UID = "FromUid";
    public static final String TO_UID = "ToUid";
    public static final String TO_NAME = "ToName";
    public static final String FRIEND_REQUEST = "FriendRequests";
    public static final String TO_EMAIL = "ToEmail";
    public static final String FROM_EMAIL = "FromEmail";
    public static final String FROM_IMG = "FromImg";
    public static final String TITLE = "TITLE";
    public static final String CONTENT = "CONTENT";
    public static final String CODE = "CODE";
    public static final String CODE_REQUEST_FRIEND = "REQUEST_FRIEND";
    public static final String CODE_WARNING_MESSAGE = "WARNING_MESSAGE";

    public static DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(RF_USERS);
    public static DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference(RF_GROUPS);
    public static DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference(RF_CHATS);
    public static DatabaseReference publicLocationRef = FirebaseDatabase.getInstance().getReference(RF_PUBLIC_LOCATION);
    public static DatabaseReference lovePlacesRef = FirebaseDatabase.getInstance().getReference(RF_LOVE_PLACES);
    public static DatabaseReference tokensRef = FirebaseDatabase.getInstance().getReference(RF_TOKENS);
    public static StorageReference userPhotosRef = FirebaseStorage.getInstance().getReference(RF_USER_PHOTOS);
    public static StorageReference groupPhotosRef = FirebaseStorage.getInstance().getReference(RF_GROUP_PHOTOS);

    public static GroupInfo groupClicked;

    public static LovePlace lovePlaceClicked;

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
            result = Bitmap.createBitmap(dp(context, 62), dp(context, 76), Bitmap.Config.ARGB_8888);
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

    public static byte[] downsizedImageBytes(Context context, Uri imageUri) {
        try {
            // 1. Convert uri to bitmap
            Bitmap fullBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            // 2. Instantiate the downsized image content as a byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            fullBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            return baos.toByteArray();
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
        return null;
    }
}
