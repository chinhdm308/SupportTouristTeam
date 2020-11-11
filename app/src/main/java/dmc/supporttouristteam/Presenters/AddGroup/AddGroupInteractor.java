package dmc.supporttouristteam.Presenters.AddGroup;

import android.content.Context;
import android.content.Intent;

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
import dmc.supporttouristteam.Views.Activitis.MessageActivity;

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
    public void search(String s, List<User> participantsList) {
        List<User> temp = new ArrayList<>();
        for (User user : participantsList) {
            if (user.getDisplayName().toLowerCase().contains(s.toLowerCase())) {
                temp.add(user);
            }
        }
        listener.onSearch(temp);
    }

    @Override
    public void createGroup(Context context, User user) {
        List<String> chatList = new ArrayList<>();
        chatList.add(Config.FB_AUTH.getCurrentUser().getUid());
        chatList.add(user.getId());
        final GroupInfo groupInfo = new GroupInfo("", "", "", 2);
        groupInfo.setChatList(chatList);
        DatabaseReference referenceGroupList = FirebaseDatabase.getInstance().getReference();
        referenceGroupList.child(Config.RF_GROUPS).push().setValue(groupInfo, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                String key = ref.getKey();
                ref.child("id").setValue(key);
                Intent messageActivity = new Intent(context, MessageActivity.class);
                messageActivity.putExtra(Config.EXTRA_GROUP_INFO, groupInfo);
                context.startActivity(messageActivity);
            }
        });
    }

}
