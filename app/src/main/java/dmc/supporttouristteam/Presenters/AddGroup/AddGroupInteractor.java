package dmc.supporttouristteam.Presenters.AddGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
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
import dmc.supporttouristteam.Utils.Common;
import dmc.supporttouristteam.Utils.Config;

public class AddGroupInteractor implements AddGroupContract.Interactor {
    private AddGroupContract.OnOperationListener listener;
    private List<User> participantsList;
    private FirebaseUser currentUser;
    private DatabaseReference groupsRef;

    public AddGroupInteractor(AddGroupContract.OnOperationListener listener) {
        this.listener = listener;
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        groupsRef = FirebaseDatabase.getInstance().getReference().child(Config.RF_GROUPS);
    }

    @Override
    public void readParticipants(DatabaseReference reference) {
        participantsList = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot i : snapshot.getChildren()) {
                    User user = i.getValue(User.class);
                    if (!user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
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
    public void createGroup(String nameGroup, List<User> selectedParticipantList, int type) {
        List<String> chatList = new ArrayList<>();
        chatList.add(currentUser.getUid());
        for (User i : selectedParticipantList) {
            chatList.add(i.getId());
        }
        final GroupInfo groupInfo = new GroupInfo("", nameGroup, "default", type);
        groupInfo.setChatList(chatList);

        if (type == 2) {
            boolean check = false;
            for (GroupInfo i : Common.groupInfoList) {
                if (i.getType() == 2 && i.getChatList().containsAll(chatList)) {
                    check = true;
                    listener.onSuccess(i);
                    return;
                }
            }
            if (!check) {
                groupsRef.push().setValue(groupInfo, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        String key = ref.getKey();
                        ref.child("id").setValue(key);
                        groupInfo.setId(key);
                        listener.onSuccess(groupInfo);
                    }
                });
            }
        }

        if (type == 1) {
            groupsRef.push().setValue(groupInfo, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    String key = ref.getKey();
                    ref.child("id").setValue(key);
                    groupInfo.setId(key);
                    listener.onSuccess(groupInfo);
                }
            });
        }
    }

}