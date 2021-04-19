package dmc.supporttouristteam.presenter.message;

import android.content.Intent;

import java.util.List;

import dmc.supporttouristteam.data.model.fb.Chat;
import dmc.supporttouristteam.data.model.fb.User;

public interface MessageContract {
    interface View {
        void setImageGroup(String img);
        void setNameGroup(String name);
        void setMessageAdapter(List<Chat> chatList);
    }

    interface Presenter {
        void doSendMessage(String message);
        void doLoadDataGroupInfo(Intent intent);
        void doReadDataMessages();
    }

    interface Interactor {
        void sendMessage(String sender, String receiver, String message);
        void loadDataGroupInfo(String uid);
        void readDataMessages();
    }

    interface OnOperationListener {
        void onSuccess(User user);

        void onSuccessDataMessage(List<Chat> chatList);
    }
}
