package dmc.supporttouristteam.Presenters.Chats;

import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import dmc.supporttouristteam.Models.GroupInfo;

public interface CommonChats {
    interface View {
        void setRecyclerChats(List<GroupInfo> groupInfoList);
        void navigateToUserInfoActivity();
        void navigateToAddGroupActivity();
        void navigateToSearchActivity();
    }

    interface Presenter {
        void readChatList(FirebaseUser currentUser);
        void chatsItemClick(int pos);
        void navigateToUserInfoActivity();
        void navigateToAddGroupActivity();
        void navigateToSearchActivity();
    }

    interface Interactor {
        void performReadChatList(FirebaseUser currentUser);
        void preformChatsItemClick(int pos);
    }

    interface OnOperationListener {
        void onReadChatList(List<GroupInfo> groupInfoList);
        void onChatsItemClick(int pos);
    }
}
