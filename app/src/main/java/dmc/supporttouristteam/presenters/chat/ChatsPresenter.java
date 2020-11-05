package dmc.supporttouristteam.presenters.chat;

import android.view.View;

public class ChatsPresenter {
    private ChatsView callback;
    public ChatsPresenter(ChatsView callback) {
        this.callback = callback;
    }

    public void setRecyclerChats() {
        callback.setRecyclerChats();
    }

    public void loadUserInfo(View view) {
        callback.loadUserInfo(view);
    }

    public void navigateToUserInfoActivity() {
        callback.navigateToUserInfoActivity();
    }

    public void navigateToAddGroupActivity() {
        callback.navigateToAddGroupActivity();
    }

    public void navigateToSearchActivity() {
        callback.navigateToSearchActivity();
    }
}
