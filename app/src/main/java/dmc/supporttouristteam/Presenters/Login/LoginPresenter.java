package dmc.supporttouristteam.Presenters.Login;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Utils.Common;
import dmc.supporttouristteam.Utils.Config;

public class LoginPresenter implements LoginContract.Presenter, LoginContract.OnOperationListener {
    private LoginContract.View view;
    private LoginInteractor interactor;

    public LoginPresenter(LoginContract.View view) {
        this.view = view;
        this.interactor = new LoginInteractor(this);
    }

    @Override
    public void navigateToRegister() {
        view.navigateToRegister();
    }

    @Override
    public void checkAndRequestForPermission(Context context, Activity activity) {
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // show explanation
                Common.showExplanation(context, activity, "", "Bạn cần cấp quyền cho ứng dụng",
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Config.MY_REQUEST_CODE);
            } else {
                Common.requestPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION, Config.MY_REQUEST_CODE);
            }
        } else {

        }
    }

    @Override
    public void onEmailError() {
        view.hideProgress();
        view.showLoginButton();
        view.showMessage(R.string.email_error);
    }

    @Override
    public void onPasswordError() {
        view.hideProgress();
        view.showLoginButton();
        view.showMessage(R.string.password_error);
    }

    @Override
    public void onSuccess() {
        view.navigateToHome();
    }

    @Override
    public void onFail() {
        view.hideProgress();
        view.showLoginButton();
        view.showMessage(R.string.login_failed);
    }

    @Override
    public void validateCredentials(String email, String password) {
        view.showProgress();
        view.hideLoginButton();
        interactor.login(email, password);
    }
}
