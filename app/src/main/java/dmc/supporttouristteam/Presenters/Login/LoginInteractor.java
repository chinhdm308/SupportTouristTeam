package dmc.supporttouristteam.Presenters.Login;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import dmc.supporttouristteam.Utils.Config;

public class LoginInteractor implements LoginContract.Interactor {

    private LoginContract.OnOperationListener listener;

    public LoginInteractor(LoginContract.OnOperationListener listener) {
        this.listener = listener;
    }

    @Override
    public void login(String email, String password) {
        boolean error = false;
        if (TextUtils.isEmpty(email)) {
            listener.onEmailError();
            error = true;
        }
        if (TextUtils.isEmpty(password)) {
            listener.onPasswordError();
            error = true;
        }
        if (!error) {
            Config.FB_AUTH.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                listener.onSuccess();
                            } else {
                                listener.onFail();
                            }
                        }
                    });
        }
    }
}
