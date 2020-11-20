package dmc.supporttouristteam.Presenters.AddGroup;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import dmc.supporttouristteam.Models.GroupInfo;
import dmc.supporttouristteam.Models.User;
import dmc.supporttouristteam.Utils.Common;

public class AddGroupPresenter implements AddGroupContract.Presenter, AddGroupContract.OnOperationListener {
    private AddGroupContract.View view;
    private AddGroupInteractor interactor;

    public AddGroupPresenter(AddGroupContract.View view) {
        this.view = view;
        this.interactor = new AddGroupInteractor(this);
    }

    @Override
    public void doReadParticipants(DatabaseReference reference) {
        interactor.readParticipants(reference);
    }

    @Override
    public void doSearch(String s, List<User> participantList) {
        List<User> temp = new ArrayList<>();
        for (User user : participantList) {
            if (Common.removeAccent(user.getDisplayName().toLowerCase()).contains(Common.removeAccent(s.toLowerCase()))) {
                temp.add(user);
            }
        }
        view.setRecyclerParticipantsAfterSearch(temp);
    }

    @Override
    public void doCreateGroup(List<User> selectedParticipantList) {
        if (selectedParticipantList.size() == 0) {
            view.showMessage("Chưa có thành viên nào");
        }
        if (selectedParticipantList.size() == 1) {
            interactor.createGroup("", selectedParticipantList);
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
    public void onSuccess(GroupInfo groupInfo) {
        view.navigationToMessageActivity(groupInfo);
        view.finishActivity();
    }

    @Override
    public void doParticipantItemClick(User user, boolean isAdd) {
        if (isAdd) {
            view.addParticipant(user);
        } else {
            view.removeParticipant(user);
        }
    }

    @Override
    public AddGroupContract.Interactor getInteractor() {
        return this.interactor;
    }
}