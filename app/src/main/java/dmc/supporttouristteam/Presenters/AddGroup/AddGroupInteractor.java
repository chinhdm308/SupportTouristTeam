package dmc.supporttouristteam.Presenters.AddGroup;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmc.supporttouristteam.Models.User;

public class AddGroupInteractor implements CommonAddGroup.Interactor {
    private CommonAddGroup.OnOperationListener listener;
    private List<User> participantsList;

    public AddGroupInteractor(CommonAddGroup.OnOperationListener listener) {
        this.listener = listener;
    }

    @Override
    public void readParticipants(DatabaseReference reference, FirebaseUser currentUser) {
        participantsList = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot i : snapshot.getChildren()) {
                    User user = i.getValue(User.class);
                    if (!user.getId().equals(currentUser.getUid())) {
                        participantsList.add(user);
                    }
                }
                listener.onReadParticipants(participantsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void search(String s, List<User> participantsList) {
        List<User> temp = new ArrayList<>();
        for (User user : participantsList) {
            if (user.getDisplayName().toLowerCase().contains(s.toLowerCase())) {
                temp.add(user);
            }
        }
        listener.onSearch(temp);
    }
}
