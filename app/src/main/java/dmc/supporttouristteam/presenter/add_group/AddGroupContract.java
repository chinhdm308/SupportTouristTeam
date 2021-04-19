package dmc.supporttouristteam.presenter.add_group;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import dmc.supporttouristteam.data.model.fb.GroupInfo;
import dmc.supporttouristteam.data.model.fb.User;

public interface AddGroupContract {
    interface View {
        void setRecyclerParticipants(List<User> participantList);
        void setRecyclerParticipantsAfterSearch(List<User> participantList);
        void setRecyclerSelectedParticipants(List<User> selectedParticipantList);
        void addParticipant(User user);
        void removeParticipant(User user);
        void showCreateGroupBottomSheet();
        void showMessage(String message);
        void finishActivity();
        void navigationToMessageActivity(GroupInfo groupInfo);
    }

    interface Presenter {
        void doReadParticipants(DatabaseReference reference);
        void doSearch(String s, List<User> participantList);
        void doCreateGroup(List<User> selectedParticipantList);
        void doParticipantItemClick(User user, boolean isAdd);
        Interactor getInteractor();
    }

    interface Interactor {
        void readParticipants(DatabaseReference reference);
        void createGroup(String nameGroup, List<User> selectedParticipantList, int type);
    }

    interface OnOperationListener {
        void onReadParticipants(List<User> userList);
        void onSuccess(GroupInfo groupInfo);
    }
}
