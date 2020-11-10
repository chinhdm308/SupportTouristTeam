package dmc.supporttouristteam.Views.Activitis;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Presenters.Message.MessageAdapter;
import dmc.supporttouristteam.Models.Chat;
import dmc.supporttouristteam.Models.GroupInfo;
import dmc.supporttouristteam.Models.User;
import dmc.supporttouristteam.Utils.Config;

public class MessageActivity extends AppCompatActivity {
    private TextView textGroupName;
    private ImageView buttonBack, buttonSend, buttonShowLocation;
    private CircleImageView imageGroup;
    private EditText etMessage;
    private RecyclerView recyclerViewMessage;
    private MessageAdapter messageAdapter;
    private List<Chat> chatList;
    private ValueEventListener seenListener;
    private GroupInfo groupInfo;
    private FirebaseUser currentUser = Config.FB_AUTH.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        getSupportActionBar().hide();

        init();

        initialize();

        loadData();

        readMessage();

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent locationTeamActivity = new Intent(MessageActivity.this, LocationTeamActivity.class);
//                startActivity(locationTeamActivity);
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = etMessage.getText().toString();
                if (!msg.isEmpty()) {
                    sendMessage(currentUser.getUid(), "", msg);
                } else {
//                    CommonService.showMessage(MessageActivity.this, "You can't send empty message");
                }
                etMessage.setText("");
            }
        });
    }

    private void loadData() {
        groupInfo = getDataToIntent();
        if (groupInfo != null) {
            if (groupInfo.getNumberOfPeople() == 2) {
                List<String> chatList = groupInfo.getChatList();
                chatList.remove(currentUser.getUid());
                String userID = chatList.get(0);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Config.RF_USERS).child(userID);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Glide.with(MessageActivity.this).load(user.getProfileImg()).into(imageGroup);
                        textGroupName.setText(user.getDisplayName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                if (groupInfo.getImage().equals("default")) {
                    Glide.with(MessageActivity.this).load(R.drawable.user1).into(imageGroup);
                } else {
                    Glide.with(MessageActivity.this).load(groupInfo.getImage()).into(imageGroup);
                }
                textGroupName.setText(groupInfo.getName());
            }
        }
    }

    private void seenMessage(final String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Config.RF_CHATS);
        seenListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getSender().equals(userId) && chat.getReceiver().equals(currentUser.getUid())) {
                        Map<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, String receiver, String message) {
        Chat chat = new Chat(sender, receiver, message, false);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(Config.RF_CHATS).child(groupInfo.getId()).push().setValue(chat);
    }

    private void readMessage() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Config.RF_CHATS)
                .child(groupInfo.getId());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    chatList.add(chat);
                }
                messageAdapter = new MessageAdapter(MessageActivity.this, chatList);
                recyclerViewMessage.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init() {
        textGroupName = findViewById(R.id.text_group_name);
        buttonBack = findViewById(R.id.button_back);
        buttonSend = findViewById(R.id.button_send);
        buttonShowLocation = findViewById(R.id.button_show_location);
        imageGroup = findViewById(R.id.image_group);
        etMessage = findViewById(R.id.et_message);
        recyclerViewMessage = findViewById(R.id.recycler_message);

        recyclerViewMessage.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewMessage.setLayoutManager(linearLayoutManager);
    }

    private void initialize() {
        chatList = new ArrayList<>();
    }

    private GroupInfo getDataToIntent() {
        GroupInfo groupInfo = (GroupInfo) getIntent().getSerializableExtra(Config.EXTRA_GROUP_INFO);
        return (groupInfo != null) ? groupInfo : null;
    }
}