package dmc.supporttouristteam.presenter.message;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import dmc.supporttouristteam.data.model.fb.Chat;
import dmc.supporttouristteam.data.model.fb.GroupInfo;
import dmc.supporttouristteam.data.model.fb.User;
import dmc.supporttouristteam.util.Common;

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
    public void doLoadDataGroupInfo(Intent intent) {
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
        } else {
            String key = intent.getStringExtra(Common.ID_GROUP);
            Common.groupsRef.child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        GroupInfo groupInfo = snapshot.getValue(GroupInfo.class);
                        if (groupInfo.getType() == 2) {
                            List<String> chatList = groupInfo.getChatList();
                            if (!chatList.get(0).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                interactor.loadDataGroupInfo(chatList.get(0));
                            } else {
                                interactor.loadDataGroupInfo(chatList.get(1));
                            }
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
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
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
