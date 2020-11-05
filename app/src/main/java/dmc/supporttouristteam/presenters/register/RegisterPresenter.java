package dmc.supporttouristteam.presenters.register;

import android.net.Uri;

import dmc.supporttouristteam.models.RegisterModel;

public class RegisterPresenter implements OnRegisterListener{
    private RegisterView registerView;
    private RegisterModel registerModel;

    public RegisterPresenter(RegisterView registerView) {
        this.registerView = registerView;
        this.registerModel = new RegisterModel(this);
    }

    public void register(String name, String email, String password, String confirmPassword, Uri pickedImgUri) {
        registerView.hideRegisterButton();
        registerView.showProgress();
        registerModel.handleRegister(name, email, password, confirmPassword, pickedImgUri);
    }

    public void navigateToRegister() {
        registerView.navigateToLogin();
    }

    public void checkAndRequestForPermission() {
        registerView.checkAndRequestForPermission();
    }

    @Override
    public void onSuccess(int message) {
        registerView.showRegisterButton();
        registerView.hideProgress();
        registerView.showMessage(message);
        registerView.navigateToLogin();
    }

    @Override
    public void onFail(int message) {
        registerView.showRegisterButton();
        registerView.hideProgress();
        registerView.showMessage(message);
    }
}
