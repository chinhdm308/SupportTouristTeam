package dmc.supporttouristteam.Presenters.Chats;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmc.supporttouristteam.Models.GroupInfo;
import dmc.supporttouristteam.Utils.Config;

public class ChatsInteractor implements CommonChats.Interactor {
    private CommonChats.OnOperationListener listener;
    private List<GroupInfo> groupInfoList;

    public ChatsInteractor(CommonChats.OnOperationListener listener) {
        this.listener = listener;
    }

    @Override
    public void performReadChatList(FirebaseUser currentUser) {
        groupInfoList = new ArrayList<>();
        DatabaseReference referenceGroupList = FirebaseDatabase.getInstance().getReference(Config.RF_GROUPS);
        referenceGroupList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupInfoList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroupInfo groupInfo = snapshot.getValue(GroupInfo.class);
                    List<String> chatList = groupInfo.getChatList();
                    if (chatList.indexOf(currentUser.getUid()) >= 0) {
                        groupInfoList.add(groupInfo);
                    }
                }
                listener.onReadChatList(groupInfoList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void preformChatsItemClick(int pos) {
        listener.onChatsItemClick(pos);
    }
}
