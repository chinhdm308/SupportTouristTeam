package dmc.supporttouristteam.presenter.login;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import dmc.supporttouristteam.util.Common;

public class LoginPresenter implements LoginContract.Presenter, LoginContract.OnOperationListener {
    private LoginContract.View view;
    private LoginInteractor interactor;
    private Context context;
    private Activity activity;

    public LoginPresenter(LoginContract.View view, Context context, Activity activity) {
        this.view = view;
        this.interactor = new LoginInteractor(this);
        this.context = context;
        this.activity = activity;
    }

    @Override
    public void navigateToRegister() {
        view.navigateToRegister();
    }

    @Override
    public void checkAndRequestForPermission(String email, String password) {
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // show explanation
                Common.showExplanation(context, activity, "", "Bạn cần cấp quyền cho ứng dụng để có trải nghiệm tốt hơn",
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Common.MY_REQUEST_CODE);
            } else {
                Common.requestPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION, Common.MY_REQUEST_CODE);
            }
        } else {
            if (email.isEmpty()) {
                view.showMessage("Email trống");
            } else if (password.isEmpty()) {
                view.showMessage("Mật khẩu trống");
            } else {
                view.showProgress();
                view.hideLoginButton();
                interactor.login(email, password);
            }
        }
    }

    @Override
    public void onSuccess() {
        view.navigateToHome();
    }

    @Override
    public void onFail() {
        view.hideProgress();
        view.showLoginButton();
        view.showMessage("Đăng nhập thất bại");
    }

    @Override
    public void validateCredentials(String email, String password) {
        checkAndRequestForPermission(email, password);
    }
}
