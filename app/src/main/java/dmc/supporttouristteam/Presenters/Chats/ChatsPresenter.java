package dmc.supporttouristteam.Presenters.Chats;

import java.util.List;

import dmc.supporttouristteam.Models.GroupInfo;

public class ChatsPresenter implements ChatsContract.Presenter, ChatsContract.OnOperationListener {
    private ChatsContract.View view;
    private ChatsInteractor interactor;

    public ChatsPresenter(ChatsContract.View view) {
        this.view = view;
        this.interactor = new ChatsInteractor(this);
    }

    @Override
    public void doReadChatList() {
        interactor.readChatList();
    }

    @Override
    public void navigateToAddGroupActivity() {
        view.navigateToAddGroupActivity();
    }

    @Override
    public void navigateToSearchActivity() {
        view.navigateToSearchActivity();
    }

    @Override
    public void onReadChatList(List<GroupInfo> groupInfoList) {
        if (groupInfoList.size() > 0) {
            view.showRecyclerChats();
        } else {
            view.hideRecyclerChats();
        }
        view.setRecyclerChats(groupInfoList);
    }

    @Override
    public void doChatsItemClick(int pos) {
        view.navigationToMessageActivity(pos);
    }

    @Override
    public void doShowLocation(int pos) {
        view.navigationToTrackingActivity(pos);
    }
}
