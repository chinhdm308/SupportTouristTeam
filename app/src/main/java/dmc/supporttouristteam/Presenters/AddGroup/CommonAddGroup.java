package dmc.supporttouristteam.Presenters.AddGroup;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import dmc.supporttouristteam.Models.User;

public interface CommonAddGroup {
    interface View {
        void setRecyclerParticipants(List<User> participantList);
        void setRecyclerSelectedParticipants();
        void addParticipant(int pos);
        void removeParticipant(int pos);
        void showCreateGroupBottomSheet();
        void showMessage(String message);
    }

    interface Presenter {
        void readParticipants(DatabaseReference reference, FirebaseUser currentUser);
    }

    interface Interactor {
        void readParticipants(DatabaseReference reference, FirebaseUser currentUser);
    }

    interface OnOperationListener {
        void onReadParticipants(List<User> userList);
    }
}
