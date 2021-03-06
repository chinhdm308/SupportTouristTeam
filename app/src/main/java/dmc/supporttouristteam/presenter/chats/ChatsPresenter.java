package dmc.supporttouristteam.presenter.chats;

import java.util.Collections;
import java.util.List;

import dmc.supporttouristteam.data.model.fb.GroupInfo;

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
        List<GroupInfo> l = groupInfoList;
        Collections.reverse(l);
        view.setRecyclerChats(l);
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
