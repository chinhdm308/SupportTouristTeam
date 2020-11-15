package dmc.supporttouristteam.Presenters.AddGroup;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import dmc.supporttouristteam.Models.GroupInfo;
import dmc.supporttouristteam.Models.User;

public interface AddGroupContract {
    interface View {
        void setRecyclerParticipants(List<User> participantList);
        void setRecyclerParticipantsAfterSearch(List<User> participantList);
        void setRecyclerSelectedParticipants(List<User> selectedParticipantList);
        void addParticipant(int pos);
        void removeParticipant(int pos);
        void showCreateGroupBottomSheet();
        void showMessage(String message);
        void finishActivity();
        void navigationToMessageActivity(GroupInfo groupInfo);
    }

    interface Presenter {
        void readParticipants(DatabaseReference reference, FirebaseUser currentUser);
        void doSearch(String s, List<User> participantList);
        void createGroup(List<User> selectedParticipantList);
        void onParticipantItemClick(int pos, boolean isAdd);
        Interactor getInteractor();
    }

    interface Interactor {
        void readParticipants(DatabaseReference reference, FirebaseUser currentUser);
        void createGroup(String nameGroup, List<User> selectedParticipantList);
    }

    interface OnOperationListener {
        void onReadParticipants(List<User> userList);
        void onSuccess(GroupInfo groupInfo);
    }
}
