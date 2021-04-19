package dmc.supporttouristteam.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

import dmc.supporttouristteam.R;
import dmc.supporttouristteam.util.Common;
import dmc.supporttouristteam.view.activity.MessageActivity;

public class MyFCMService extends FirebaseMessagingService {

    private String TAG = "tagMyFCMService";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData() != null) {
            if (remoteMessage.getData().get(Common.CODE).equals(Common.CODE_REQUEST_FRIEND)) {
                sendNotification(remoteMessage);
                addFriendRequestToUserInfo(remoteMessage.getData());
            }

            if (remoteMessage.getData().get(Common.CODE).equals(Common.CODE_WARNING_MESSAGE))
                sendNotification(remoteMessage);

            if (remoteMessage.getData().get(Common.CODE).equals(Common.CODE_ADD_MEMBER_TO_GROUP)) {
                sendNotificationAddGroup(remoteMessage);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotificationAddGroup(RemoteMessage remoteMessage) {
        String CHANNEL_ID = "dmc.supporttouristteam";
        String channelName = "dmc.supporttouristteam";

        Map<String, String> data = remoteMessage.getData();
        String title = data.get(Common.TITLE);
        String content = data.get(Common.CONTENT);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra(Common.ID_GROUP, data.get(Common.ID_GROUP));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Create the persistent notification
        Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSound(defaultSound)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setSmallIcon(R.drawable.worldwide_location)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create Notification Channel
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(new Random().nextInt(), builder.build());
    }

    private void addFriendRequestToUserInfo(Map<String, String> data) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(Common.RF_USERS);
        usersRef.child(data.get(Common.TO_UID)).child(Common.FRIEND_REQUEST)
                .child(data.get(Common.FROM_UID)).setValue(data.get(Common.FROM_UID));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotification(RemoteMessage remoteMessage) {
        String CHANNEL_ID = "dmc.supporttouristteam";
        String channelName = "dmc.supporttouristteam";

        Map<String, String> data = remoteMessage.getData();
        String title = data.get(Common.TITLE);
        String content = data.get(Common.CONTENT);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Create the persistent notification
        Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSound(defaultSound)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setSmallIcon(R.drawable.worldwide_location)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create Notification Channel
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(new Random().nextInt(), builder.build());
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference tokensRef = FirebaseDatabase.getInstance().getReference(Common.RF_TOKENS);
            tokensRef.child(currentUser.getUid()).setValue(s);
        }

    }
}