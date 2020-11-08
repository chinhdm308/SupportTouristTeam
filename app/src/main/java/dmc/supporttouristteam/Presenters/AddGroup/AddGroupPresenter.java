package dmc.supporttouristteam.Presenters.AddGroup;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import dmc.supporttouristteam.Models.User;

public class AddGroupPresenter implements CommonAddGroup.Presenter, CommonAddGroup.OnOperationListener {
    private CommonAddGroup.View view;
    private AddGroupInteractor interactor;

    public AddGroupPresenter(CommonAddGroup.View view) {
        this.view = view;
        this.interactor = new AddGroupInteractor(this);
    }

    public List<User> getParticipants(String s, List<User> participantsList) {
        List<User> tmp = new ArrayList<>();
        for (User user : participantsList) {
            if (user.getDisplayName().toLowerCase().contains(s.toLowerCase())) {
                tmp.add(user);
            }
        }
        return tmp;
    }

    public void updatesSelectedParticipants(int pos, boolean isAdd) {
        if (isAdd) {
            view.addParticipant(pos);
        } else {
            view.removeParticipant(pos);
        }
    }

    public void confirmNewGroup(int size) {
        if (size == 0) {
            view.showMessage("Chưa có thành viên nào");
        }
        if (size == 1) {
//            createNewGroup(selectedParticipantsList.get(0));
        }
        if (size > 1) {
            view.showCreateGroupBottomSheet();
        }
    }

    @Override
    public void readParticipants(DatabaseReference reference, FirebaseUser currentUser) {
        interactor.readParticipants(reference, currentUser);
    }

    @Override
    public void onReadParticipants(List<User> userList) {
        view.setRecyclerParticipants(userList);
    }
}
