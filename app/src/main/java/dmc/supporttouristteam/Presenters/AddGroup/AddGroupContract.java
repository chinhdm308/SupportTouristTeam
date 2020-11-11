package dmc.supporttouristteam.Presenters.AddGroup;

import android.content.Context;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

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
    }

    interface Presenter {
        void readParticipants(DatabaseReference reference, FirebaseUser currentUser);
        void search(String s, List<User> participantList);
        void createGroup(Context context, List<User> selectedParticipantList);
        void onParticipantItemClick(int pos, boolean isAdd);
    }

    interface Interactor {
        void readParticipants(DatabaseReference reference, FirebaseUser currentUser);
        void search(String s, List<User> participantList);
        void createGroup(Context context, User user);
    }

    interface OnOperationListener {
        void onReadParticipants(List<User> userList);
        void onSearch(List<User> participantList);
    }
}
