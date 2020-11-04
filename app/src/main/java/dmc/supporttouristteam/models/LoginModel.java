package dmc.supporttouristteam.models;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import dmc.supporttouristteam.presenters.login.OnLoginFinishedListener;
import dmc.supporttouristteam.utils.Common;

public class LoginModel {
    private OnLoginFinishedListener callback;

    public LoginModel(OnLoginFinishedListener callback) {
        this.callback = callback;
    }

    public void validateCredentials(String email, String password) {
        // Mock login. I'm creating a handler to delay the answer a couple of seconds
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                boolean error = false;
//                if (TextUtils.isEmpty(email)) {
//                    callback.onEmailError();
//                    error = true;
//                }
//                if (TextUtils.isEmpty(password)) {
//                    callback.onPasswordError();
//                    error = true;
//                }
//                if (!error) {
//                    callback.onSuccess();
//                }
//            }
//        }, 2000);
        boolean error = false;
        if (TextUtils.isEmpty(email)) {
            callback.onEmailError();
            error = true;
        }
        if (TextUtils.isEmpty(password)) {
            callback.onPasswordError();
            error = true;
        }
        if (!error) {
            Common.FB_AUTH.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                callback.onSuccess();
                            } else {
                                callback.onFail();
                            }
                        }
                    });
        }
    }
}
