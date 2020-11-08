package dmc.supporttouristteam.Utils;

import com.google.firebase.auth.FirebaseAuth;

public class Config {
    public  static FirebaseAuth FB_AUTH = FirebaseAuth.getInstance();

    public static final String RF_USERS = "USERS";
    public static final String RF_GROUPS = "GROUPS";
    public static final String RF_CHATS = "CHATS";
    public static final String RF_PHOTOS = "USER_PHOTOS";

    public static final String BUNDLE_GROUP_INFO = "BUNDLE_GROUP_INFO";
    public static final String EXTRA_GROUP_INFO = "EXTRA_GROUP_INFO";

    public static final String BUNDLE_USER = "BUNDLE_USER";
    public static final String EXTRA_USER = "EXTRA_USER";

    public static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1;
}