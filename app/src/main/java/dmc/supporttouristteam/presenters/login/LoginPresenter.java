package dmc.supporttouristteam.presenters.login;

import dmc.supporttouristteam.R;
import dmc.supporttouristteam.models.LoginModel;

public class LoginPresenter implements OnLoginFinishedListener{
    private LoginView loginView;
    private LoginModel loginModel;

    public LoginPresenter(LoginView loginView) {
        this.loginView = loginView;
        loginModel = new LoginModel(this);
    }

    public void login(String email, String password) {
        loginView.showProgress();
        loginView.hideLoginButton();
        loginModel.validateCredentials(email, password);
    }

    public void navigateToRegister() {
        loginView.navigateToRegister();
    }

    @Override
    public void onEmailError() {
        loginView.hideProgress();
        loginView.showLoginButton();
        loginView.showMessage(R.string.email_error);
    }

    @Override
    public void onPasswordError() {
        loginView.hideProgress();
        loginView.showLoginButton();
        loginView.showMessage(R.string.password_error);
    }

    @Override
    public void onSuccess() {
        loginView.navigateToHome();
    }

    @Override
    public void onFail() {
        loginView.hideProgress();
        loginView.showLoginButton();
        loginView.showMessage(R.string.login_failed);
    }
}
