package dmc.supporttouristteam.Presenters.Message;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import dmc.supporttouristteam.Models.Chat;
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
            interactor.sendMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(), "", message);
        }
    }

    @Override
    public void doLoadDataGroupInfo() {
        String uid;
        if (Common.groupClicked != null) {
            if (Common.groupClicked.getType() == 2) {
                List<String> chatList = Common.groupClicked.getChatList();
                if (!chatList.get(0).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    uid = chatList.get(0);
                } else {
                    uid = chatList.get(1);
                }
                interactor.loadDataGroupInfo(uid);
            } else {
                if (Common.groupClicked.getImage().equals("default")) {
                    view.setImageGroup(null);
                } else {
                    view.setImageGroup(Common.groupClicked.getImage());
                }
                view.setNameGroup(Common.groupClicked.getName());
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
