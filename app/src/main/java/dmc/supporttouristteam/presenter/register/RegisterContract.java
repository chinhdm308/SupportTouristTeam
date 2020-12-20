package dmc.supporttouristteam.presenter.register;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

public interface RegisterContract {
    interface View {
        void showProgress();

        void hideProgress();

        void showRegisterButton();

        void hideRegisterButton();

        void navigateToLogin();

        void showMessage(String message);
    }

    interface Presenter {
        void validateCredentials(String name, String email, String password, String confirmPassword, Uri pickedImgUri);

        void navigateToLogin();

        void checkAndRequestForPermission(Context context, Activity activity);
    }

    interface Interactor {
        void register(String name, String email, String password, Uri pickedImgUri);

        void openGallery(Activity activity);
    }

    interface OnOperationListener {
        void onSuccess(String message);

        void onFail(String message);
    }
}
