package dmc.supporttouristteam.presenters.chat;

import android.view.View;

public interface ChatsView {
    void setRecyclerChats();
    void loadUserInfo(View view);
    void navigateToUserInfoActivity();
    void navigateToAddGroupActivity();
    void navigateToSearchActivity();
}
