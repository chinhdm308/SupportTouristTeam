package dmc.supporttouristteam.Utils;

import com.google.firebase.auth.FirebaseAuth;

public class Config {
    public  static FirebaseAuth FB_AUTH = FirebaseAuth.getInstance();

    public static final String RF_USERS = "USERS";
    public static final String RF_GROUPS = "GROUPS";
    public static final String RF_CHATS = "CHATS";
    public static final String RF_PHOTOS = "USER_PHOTOS";
    public static final String RF_PUBLIC_LOCATION = "PUBLIC_LOCATION";

    public static final String EXTRA_GROUP_INFO = "EXTRA_GROUP_INFO";
    public static final String EXTRA_USER = "EXTRA_USER";

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    public static final int MY_REQUEST_CODE = 1;

    public static final String USER_UID_SAVE_KEY = "SaveUid";
}
