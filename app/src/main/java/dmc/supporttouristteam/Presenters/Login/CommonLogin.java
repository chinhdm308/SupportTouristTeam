package dmc.supporttouristteam.Presenters.Login;

public interface CommonLogin {
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
