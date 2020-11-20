package dmc.supporttouristteam.Presenters.Chats;

import java.util.List;

import dmc.supporttouristteam.Models.GroupInfo;

public interface ChatsContract {
    interface View {
        void setRecyclerChats(List<GroupInfo> groupInfoList);
        void navigateToUserInfoActivity();
        void navigateToAddGroupActivity();
        void navigateToSearchActivity();
        void navigationToMessageActivity(int pos);
        void navigateToFindNearbyPlacesActivity();
    }

    interface Presenter {
        void doReadChatList();
        void doChatsItemClick(int pos);
        void navigateToUserInfoActivity();
        void navigateToAddGroupActivity();
        void navigateToSearchActivity();
        void navigateToFindNearbyPlacesActivity();
    }

    interface Interactor {
        void readChatList();
    }

    interface OnOperationListener {
        void onReadChatList(List<GroupInfo> groupInfoList);
    }
}
