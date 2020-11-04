package dmc.supporttouristteam.presenters.login;

public interface LoginView {
    void showProgress();
    void hideProgress();
    void showLoginButton();
    void hideLoginButton();
//    void setUsernameError();
//    void setPasswordError();
    void navigateToHome();
//    void onLoginSuccess();
//    void onLoginFailed();
    void showMessage(int message);
    void navigateToRegister();
}
