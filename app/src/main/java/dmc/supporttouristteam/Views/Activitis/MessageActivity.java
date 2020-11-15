package dmc.supporttouristteam.Views.Activitis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmc.supporttouristteam.Models.Chat;
import dmc.supporttouristteam.Models.GroupInfo;
import dmc.supporttouristteam.Models.User;
import dmc.supporttouristteam.Presenters.Message.MessageAdapter;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Utils.Common;
import dmc.supporttouristteam.Utils.Config;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textGroupName;
    private ImageView buttonBack, buttonSend;
    private Button buttonShowLocation;
    private CircleImageView imageGroup;
    private EditText etMessage;
    private RecyclerView recyclerViewMessage;
    private MessageAdapter messageAdapter;
    private List<Chat> chatList;
    private GroupInfo groupInfo;
    private String Uid;
    private DatabaseReference usersRef, chatsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        getSupportActionBar().hide();

        init();

        loadData();

        buttonBack.setOnClickListener(this);
        buttonShowLocation.setOnClickListener(this);
        buttonSend.setOnClickListener(this);
    }

    private void loadData() {
        groupInfo = getDataToIntent();
        if (groupInfo != null) {
            if (groupInfo.getNumberOfPeople() == 2) {
                List<String> chatList = groupInfo.getChatList();
                if (!chatList.get(0).equals(Common.loggedUser.getId())) {
                    Uid = chatList.get(0);
                } else {
                    Uid = chatList.get(1);
                }
                usersRef.child(Uid).addValueEventListener(new ValueEventListener() {
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
            readMessage();
        }
    }

    private void sendMessage(String sender, String receiver, String message) {
        Chat chat = new Chat(sender, receiver, message, false);
        chatsRef.child(groupInfo.getId()).push().setValue(chat);
    }

    private void readMessage() {
        chatsRef.child(groupInfo.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    chatList.add(chat);
                }
                messageAdapter = new MessageAdapter(chatList);
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

        chatList = new ArrayList<>();

        usersRef = FirebaseDatabase.getInstance().getReference(Config.RF_USERS);
        chatsRef = FirebaseDatabase.getInstance().getReference(Config.RF_CHATS);
    }

    private GroupInfo getDataToIntent() {
        return (GroupInfo) getIntent().getSerializableExtra(Config.EXTRA_GROUP_INFO);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_back:
                finish();
                break;
            case R.id.button_show_location:
                Intent trackingActivity = new Intent(MessageActivity.this, TrackingActivity.class);
                trackingActivity.putExtra(Config.EXTRA_GROUP_INFO, groupInfo);
                startActivity(trackingActivity);
                break;
            case R.id.button_send:
                String msg = etMessage.getText().toString();
                if (!msg.isEmpty()) {
                    sendMessage(Common.loggedUser.getId(), "", msg);
                }
                etMessage.setText("");
                break;
        }
    }
}