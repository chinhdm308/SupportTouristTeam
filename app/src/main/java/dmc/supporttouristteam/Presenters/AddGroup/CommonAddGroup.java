package dmc.supporttouristteam.Presenters.AddGroup;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import dmc.supporttouristteam.Models.User;

public interface CommonAddGroup {
    interface View {
        void setRecyclerParticipants(List<User> participantList);
        void setRecyclerParticipantsAfterSearch(List<User> participantList);
        void setRecyclerSelectedParticipants(List<User> selectedParticipantsList);
        void addParticipant(int pos);
        void removeParticipant(int pos);
        void showCreateGroupBottomSheet();
        void showMessage(String message);
    }

    interface Presenter {
        void readParticipants(DatabaseReference reference, FirebaseUser currentUser);
        void search(String s, List<User> participantsList);
    }

    interface Interactor {
        void readParticipants(DatabaseReference reference, FirebaseUser currentUser);
        void search(String s, List<User> participantsList);
    }

    interface OnOperationListener {
        void onReadParticipants(List<User> userList);
        void onSearch(List<User> participantsList);
    }
}
