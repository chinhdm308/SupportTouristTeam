package dmc.supporttouristteam.Presenters.Message;

import java.util.List;

import dmc.supporttouristteam.Models.Chat;
import dmc.supporttouristteam.Models.GroupInfo;
import dmc.supporttouristteam.Models.User;

public interface MessageContract {
    interface View {
        void setImageGroup(String img);
        void setNameGroup(String name);
        void setMessageAdapter(List<Chat> chatList);
    }

    interface Presenter {
        void doSendMessage(String message);
        void doLoadDataGroupInfo(GroupInfo groupInfo);
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
