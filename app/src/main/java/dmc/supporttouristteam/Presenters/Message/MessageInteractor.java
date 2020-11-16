package dmc.supporttouristteam.Presenters.Message;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmc.supporttouristteam.Models.Chat;
import dmc.supporttouristteam.Models.User;
import dmc.supporttouristteam.Utils.Common;
import dmc.supporttouristteam.Utils.Config;

public class MessageInteractor implements MessageContract.Interactor {

    private DatabaseReference chatsRef, usersRef;
    private MessageContract.OnOperationListener listener;

    public MessageInteractor(MessageContract.OnOperationListener listener) {
        this.listener = listener;

        this.chatsRef = FirebaseDatabase.getInstance().getReference(Config.RF_CHATS);
        this.usersRef = FirebaseDatabase.getInstance().getReference(Config.RF_USERS);
    }

    @Override
    public void sendMessage(String sender, String receiver, String message) {
        Chat chat = new Chat(sender, receiver, message, false);
        chatsRef.child(Common.groupClicked.getId()).push().setValue(chat);
    }

    @Override
    public void loadDataGroupInfo(String uid) {
        usersRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                listener.onSuccess(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void readDataMessages() {
        List<Chat> chatList = new ArrayList<>();
        chatsRef.child(Common.groupClicked.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    chatList.add(chat);
                }
                listener.onSuccessDataMessage(chatList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
