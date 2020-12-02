package dmc.supporttouristteam.Presenters.Chats;

import java.util.List;

import dmc.supporttouristteam.Models.GroupInfo;

public interface ChatsContract {
    interface View {
        void setRecyclerChats(List<GroupInfo> groupInfoList);
        void navigateToAddGroupActivity();
        void navigateToSearchActivity();
        void navigationToMessageActivity(int pos);
        void showRecyclerChats();
        void hideRecyclerChats();
        void navigationToTrackingActivity(int pos);
    }

    interface Presenter {
        void doReadChatList();
        void doChatsItemClick(int pos);
        void navigateToAddGroupActivity();
        void navigateToSearchActivity();
        void doShowLocation(int pos);
    }

    interface Interactor {
        void readChatList();
    }

    interface OnOperationListener {
        void onReadChatList(List<GroupInfo> groupInfoList);
    }
}
