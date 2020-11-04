package dmc.supporttouristteam.presenters.login;

public interface OnLoginFinishedListener {
    public void onEmailError();
    public void onPasswordError();
    public void onSuccess();
    public void onFail();
}