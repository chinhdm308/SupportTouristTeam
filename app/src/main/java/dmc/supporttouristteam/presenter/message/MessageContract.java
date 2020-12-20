package dmc.supporttouristteam.presenter.message;

import java.util.List;

import dmc.supporttouristteam.data.model.Chat;
import dmc.supporttouristteam.data.model.User;

public interface MessageContract {
    interface View {
        void setImageGroup(String img);
        void setNameGroup(String name);
        void setMessageAdapter(List<Chat> chatList);
    }

    interface Presenter {
        void doSendMessage(String message);
        void doLoadDataGroupInfo();
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
