package dmc.supporttouristteam.Presenters.Message;

import java.util.List;

import dmc.supporttouristteam.Models.Chat;
import dmc.supporttouristteam.Models.GroupInfo;
import dmc.supporttouristteam.Models.User;
import dmc.supporttouristteam.Utils.Common;

public class MessagePresenter implements MessageContract.Presenter, MessageContract.OnOperationListener {

    private MessageContract.View view;
    private MessageInteractor interactor;

    public MessagePresenter( MessageContract.View view) {
        this.view = view;
        this.interactor = new MessageInteractor(this);
    }

    @Override
    public void doSendMessage(String message) {
        if (!message.isEmpty()) {
            interactor.sendMessage(Common.currentUser.getUid(), "", message);
        }
    }

    @Override
    public void doLoadDataGroupInfo(GroupInfo groupInfo) {
        String uid;
        if (groupInfo != null) {
            if (groupInfo.getNumberOfPeople() == 2) {
                List<String> chatList = groupInfo.getChatList();
                if (!chatList.get(0).equals(Common.currentUser.getUid())) {
                    uid = chatList.get(0);
                } else {
                    uid = chatList.get(1);
                }
                interactor.loadDataGroupInfo(uid);
            } else {
                if (groupInfo.getImage().equals("default")) {
                    view.setImageGroup(null);
                } else {
                    view.setImageGroup(groupInfo.getImage());
                }
                view.setNameGroup(groupInfo.getName());
            }
        }
    }

    @Override
    public void doReadDataMessages() {
        interactor.readDataMessages();
    }

    @Override
    public void onSuccess(User user) {
        view.setImageGroup(user.getProfileImg());
        view.setNameGroup(user.getDisplayName());
    }

    @Override
    public void onSuccessDataMessage(List<Chat> chatList) {
        view.setMessageAdapter(chatList);
    }
}
