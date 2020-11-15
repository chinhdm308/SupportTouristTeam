package dmc.supporttouristteam.Presenters.Register;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Utils.Common;
import dmc.supporttouristteam.Utils.Config;

public class RegisterPresenter implements RegisterContract.Presenter, RegisterContract.OnOperationListener {
    private RegisterContract.View view;
    private RegisterInteractor interactor;

    public RegisterPresenter(RegisterContract.View view) {
        this.view = view;
        this.interactor = new RegisterInteractor(this);
    }

    @Override
    public void navigateToLogin() {
        view.navigateToLogin();
    }

    @Override
    public void checkAndRequestForPermission(Context context, Activity activity) {
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // show explanation
                Common.showExplanation(context, activity, "", "Bạn cần cấp quyền cho ứng dụng",
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Config.MY_REQUEST_CODE);
            } else {
                Common.requestPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Config.MY_REQUEST_CODE);
            }
        } else {
            interactor.openGallery(activity);
        }
    }

    @Override
    public void onSuccess(int message) {
        view.showRegisterButton();
        view.hideProgress();
        view.showMessage(message);
        view.navigateToLogin();
    }

    @Override
    public void onFail(int message) {
        view.showRegisterButton();
        view.hideProgress();
        view.showMessage(message);
    }

    @Override
    public void validateCredentials(String name, String email, String password, String confirmPassword, Uri pickedImgUri) {
        view.hideRegisterButton();
        view.showProgress();
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            onFail(R.string.fields_empty);

        } else {
            if (confirmPassword.equals(password)) {
                if (pickedImgUri != null) {
                    interactor.register(name, email, password, pickedImgUri);
                } else {
                    onFail(R.string.image_picked_empty);
                }
            } else {
                onFail(R.string.confirm_pass_incorrect);
            }
        }
    }
}
