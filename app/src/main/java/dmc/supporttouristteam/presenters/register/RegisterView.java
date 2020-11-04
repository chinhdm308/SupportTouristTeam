package dmc.supporttouristteam.presenters.register;

public interface RegisterView {
    void showProgress();
    void hideProgress();
    void showRegisterButton();
    void hideRegisterButton();
    void navigateToLogin();
    void showMessage(int message);
}
