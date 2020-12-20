package dmc.supporttouristteam.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import dmc.supporttouristteam.util.Common;

public class MyFirebaseIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        String refreshToken = FirebaseInstanceId.getInstance().getToken();

        if (currentUser != null) {
            updateToken(currentUser, refreshToken);
        }
    }

    private void updateToken(FirebaseUser currentUser, String refreshToken) {
        DatabaseReference tokensRef = FirebaseDatabase.getInstance().getReference(Common.RF_TOKENS);
        tokensRef.child(currentUser.getUid()).setValue(refreshToken);
    }
}