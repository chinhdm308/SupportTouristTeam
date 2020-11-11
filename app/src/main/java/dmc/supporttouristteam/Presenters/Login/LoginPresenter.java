package dmc.supporttouristteam.Presenters.Login;

import dmc.supporttouristteam.R;

public class LoginPresenter implements LoginContract.Presenter, LoginContract.OnOperationListener{
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
