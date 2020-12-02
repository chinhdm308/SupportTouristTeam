package dmc.supporttouristteam.Views.Activitis;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmc.supporttouristteam.Models.Chat;
import dmc.supporttouristteam.Models.GroupInfo;
import dmc.supporttouristteam.Presenters.Message.MessageAdapter;
import dmc.supporttouristteam.Presenters.Message.MessageContract;
import dmc.supporttouristteam.Presenters.Message.MessagePresenter;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Utils.Common;
import dmc.supporttouristteam.Utils.Config;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener, MessageContract.View, ChildEventListener {
    private TextView textGroupName;
    private ImageView buttonBack, buttonSend;
    private Button buttonShowLocation, buttonShowInfo;
    private CircleImageView imageGroup;
    private EditText etMessage;

    private RecyclerView recyclerViewMessage;
    private MessageAdapter messageAdapter;

    private MessagePresenter presenter;

    private DatabaseReference groupsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        getSupportActionBar().hide();

        init();

        presenter.doLoadDataGroupInfo();
        presenter.doReadDataMessages();
    }

    @Override
    public void setMessageAdapter(List<Chat> chatList) {
        messageAdapter = new MessageAdapter(chatList);
        recyclerViewMessage.setAdapter(messageAdapter);
    }

    private void init() {
        textGroupName = findViewById(R.id.text_group_name);
        buttonBack = findViewById(R.id.button_back);
        buttonSend = findViewById(R.id.button_send);
        buttonShowLocation = findViewById(R.id.button_show_location);
        buttonShowInfo = findViewById(R.id.button_show_info);
        imageGroup = findViewById(R.id.image_group);
        etMessage = findViewById(R.id.et_message);
        recyclerViewMessage = findViewById(R.id.recycler_message);

        recyclerViewMessage.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewMessage.setLayoutManager(linearLayoutManager);

        presenter = new MessagePresenter(this);

        buttonBack.setOnClickListener(this);
        buttonShowLocation.setOnClickListener(this);
        buttonSend.setOnClickListener(this);
        buttonShowInfo.setOnClickListener(this);

        groupsRef = FirebaseDatabase.getInstance().getReference(Config.RF_GROUPS);
        groupsRef.addChildEventListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_back:
                finish();
                break;
            case R.id.button_show_location:
                Intent trackingActivity = new Intent(MessageActivity.this, TrackingActivity.class);
                trackingActivity.putExtra(Config.EXTRA_GROUP_INFO, Common.groupClicked);
                startActivity(trackingActivity);
                break;
            case R.id.button_send:
                String msg = etMessage.getText().toString();
                presenter.doSendMessage(msg);
                etMessage.setText("");
                break;
            case R.id.button_show_info:
                Intent intent = new Intent(MessageActivity.this, InfoChatActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void setImageGroup(String img) {
        if (img != null) {
            Glide.with(MessageActivity.this).load(img).into(imageGroup);
        } else {
            Glide.with(MessageActivity.this).load(R.drawable.user1).into(imageGroup);
        }
    }

    @Override
    public void setNameGroup(String name) {
        textGroupName.setText(name);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Common.groupClicked != null)
            presenter.doLoadDataGroupInfo();
        else
            finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(Config.TAG, "onStop");
        groupsRef.removeEventListener(this);
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
        if (snapshot.exists()) {
            GroupInfo groupInfo = snapshot.getValue(GroupInfo.class);

            if (groupInfo.getId().equals(Common.groupClicked.getId())) {

                AlertDialog alert = new AlertDialog.Builder(MessageActivity.this).create();
                alert.setMessage("Cuộc trò chuyện này đã bị xóa");
                alert.setCancelable(false);
                alert.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Common.groupClicked = null;
                        finish();
                    }
                });

                alert.show();
            }
        }
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}