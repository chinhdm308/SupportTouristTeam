package dmc.supporttouristteam.presenter.login;

public interface LoginContract {
    interface View {
        void showProgress();
        void hideProgress();
        void showLoginButton();
        void hideLoginButton();
        void navigateToHome();
        void showMessage(String message);
        void navigateToRegister();
    }

    interface Presenter {
        void validateCredentials(String username, String password);
        void navigateToRegister();
        void checkAndRequestForPermission(String email, String password);
    }

    interface Interactor {
        void login(String email, String password);
    }

    interface OnOperationListener {
        void onSuccess();
        void onFail();
    }
}
