package dmc.supporttouristteam.Presenters.Chats;

import com.google.firebase.auth.FirebaseUser;

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
    public void readChatList(FirebaseUser currentUser) {
        interactor.performReadChatList(currentUser);
    }

    @Override
    public void chatsItemClick(int pos) {
        interactor.preformChatsItemClick(pos);
    }

    @Override
    public void navigateToUserInfoActivity() {
        view.navigateToUserInfoActivity();
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
        view.setRecyclerChats(groupInfoList);
    }

    @Override
    public void onChatsItemClick(int pos) {
//        Intent messageActivity = new Intent(getContext(), MessageActivity.class);
//        messageActivity.putExtra(Config.EXTRA_GROUP_INFO, groupInfoList.get(pos));
//        startActivity(messageActivity);
    }
}
