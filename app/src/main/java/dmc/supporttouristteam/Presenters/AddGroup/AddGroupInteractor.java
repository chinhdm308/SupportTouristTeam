package dmc.supporttouristteam.Presenters.AddGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmc.supporttouristteam.Models.GroupInfo;
import dmc.supporttouristteam.Models.User;
import dmc.supporttouristteam.Utils.Config;

public class AddGroupInteractor implements AddGroupContract.Interactor {
    private AddGroupContract.OnOperationListener listener;
    private List<User> participantsList;

    public AddGroupInteractor(AddGroupContract.OnOperationListener listener) {
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
    public void createGroup(String nameGroup, List<User> selectedParticipantList) {
        List<String> chatList = new ArrayList<>();
        chatList.add(Config.FB_AUTH.getCurrentUser().getUid());
        for (User i : selectedParticipantList) {
            chatList.add(i.getId());
        }
        final GroupInfo groupInfo = new GroupInfo("", nameGroup, "default", chatList.size());
        groupInfo.setChatList(chatList);
        DatabaseReference referenceGroupList = FirebaseDatabase.getInstance().getReference();
        referenceGroupList.child(Config.RF_GROUPS).push().setValue(groupInfo, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                String key = ref.getKey();
                ref.child("id").setValue(key);
                listener.onSuccess(groupInfo);
            }
        });
    }

}
