package dmc.supporttouristteam.presenters.add_group;

import java.util.ArrayList;
import java.util.List;

import dmc.supporttouristteam.models.User;

public class AddGroupPresenter {
    private AddGroupView addGroupView;

    public AddGroupPresenter(AddGroupView addGroupView) {
        this.addGroupView = addGroupView;
    }

    public void setRecyclerParticipants() {
        addGroupView.setRecyclerParticipants();
    }

    public void setRecyclerSelectedParticipants() {
        addGroupView.setRecyclerSelectedParticipants();
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

    public void updatesSelectedParticipants() {

    }
}
