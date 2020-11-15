package dmc.supporttouristteam.Presenters.Chats;

import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import dmc.supporttouristteam.Models.GroupInfo;

public interface ChatsContract {
    interface View {
        void setRecyclerChats(List<GroupInfo> groupInfoList);
        void navigateToUserInfoActivity();
        void navigateToAddGroupActivity();
        void navigateToSearchActivity();
        void navigationToMessageActivity(int pos);
    }

    interface Presenter {
        void readChatList(FirebaseUser currentUser);
        void doChatsItemClick(int pos);
        void navigateToUserInfoActivity();
        void navigateToAddGroupActivity();
        void navigateToSearchActivity();
    }

    interface Interactor {
        void readChatList(FirebaseUser currentUser);
    }

    interface OnOperationListener {
        void onReadChatList(List<GroupInfo> groupInfoList);
    }
}
