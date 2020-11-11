package dmc.supporttouristteam.Presenters.AddGroup;

import android.content.Context;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import dmc.supporttouristteam.Models.User;

public class AddGroupPresenter implements AddGroupContract.Presenter, AddGroupContract.OnOperationListener {
    private AddGroupContract.View view;
    private AddGroupInteractor interactor;

    public AddGroupPresenter(AddGroupContract.View view) {
        this.view = view;
        this.interactor = new AddGroupInteractor(this);
    }

    @Override
    public void readParticipants(DatabaseReference reference, FirebaseUser currentUser) {
        interactor.readParticipants(reference, currentUser);
    }

    @Override
    public void search(String s, List<User> participantList) {
        interactor.search(s, participantList);
    }

    @Override
    public void createGroup(Context context, List<User> selectedParticipantList) {
        if (selectedParticipantList.size() == 0) {
             view.showMessage("Chưa có thành viên nào");
        }
        if (selectedParticipantList.size() == 1) {
            interactor.createGroup(context, selectedParticipantList.get(0));
        }
        if (selectedParticipantList.size() > 1) {
            view.showCreateGroupBottomSheet();
        }
    }

    @Override
    public void onReadParticipants(List<User> userList) {
        view.setRecyclerParticipants(userList);
    }

    @Override
    public void onSearch(List<User> participantList) {
        view.setRecyclerParticipantsAfterSearch(participantList);
    }

    @Override
    public void onParticipantItemClick(int pos, boolean isAdd) {
        if (isAdd) {
            view.addParticipant(pos);
        } else {
            view.removeParticipant(pos);
        }
    }
}
