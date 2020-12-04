package dmc.supporttouristteam.Presenters.Chats;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmc.supporttouristteam.Models.GroupInfo;
import dmc.supporttouristteam.Utils.Common;
import dmc.supporttouristteam.Utils.Config;

public class ChatsInteractor implements ChatsContract.Interactor {
    private ChatsContract.OnOperationListener listener;
    public static List<GroupInfo> groupInfoList;
    private DatabaseReference groupsRef;

    public ChatsInteractor(ChatsContract.OnOperationListener listener) {
        this.listener = listener;
        this.groupsRef = FirebaseDatabase.getInstance().getReference(Config.RF_GROUPS);
        this.groupInfoList = new ArrayList<>();
    }

    @Override
    public void readChatList() {
        groupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupInfoList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getValue() != null) {
                        GroupInfo groupInfo = snapshot.getValue(GroupInfo.class);
                        if (groupInfo.getChatList().contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            groupInfoList.add(groupInfo);
                        }
                    }
                }
                Common.groupInfoList = groupInfoList;
                listener.onReadChatList(groupInfoList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
