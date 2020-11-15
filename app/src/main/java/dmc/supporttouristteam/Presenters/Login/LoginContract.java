package dmc.supporttouristteam.Presenters.Login;

import android.app.Activity;
import android.content.Context;

public interface LoginContract {
    interface View {
        void showProgress();
        void hideProgress();
        void showLoginButton();
        void hideLoginButton();
        void navigateToHome();
        void showMessage(int message);
        void navigateToRegister();
    }

    interface Presenter {
        void validateCredentials(String username, String password);
        void navigateToRegister();
        void checkAndRequestForPermission(Context context, Activity activity);
    }

    interface Interactor {
        void login(String email, String password);
    }

    interface OnOperationListener {
        void onEmailError();
        void onPasswordError();
        void onSuccess();
        void onFail();
    }
}
