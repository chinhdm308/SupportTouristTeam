package dmc.supporttouristteam.Views.Activitis;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmc.supporttouristteam.Models.GroupInfo;
import dmc.supporttouristteam.Models.User;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Utils.Common;
import dmc.supporttouristteam.Utils.Config;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView userPhoto;
    private TextView textEmail, textUsername;
    private Button buttonAddFriend, buttonChat;

    private User user;

    private FirebaseUser currentUser;
    private DatabaseReference groupsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        getSupportActionBar().setTitle("Thông tin người dùng");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        loadUserInfo();
    }

    private void loadUserInfo() {
        user = getDataToIntent();
        if (user != null) {
            Glide.with(this).load(user.getProfileImg()).into(userPhoto);
            textUsername.setText(user.getDisplayName());
            textEmail.setText(user.getEmail());
        }
    }

    private User getDataToIntent() {
        return (User) getIntent().getSerializableExtra(Config.EXTRA_USER);
    }

    private void init() {
        userPhoto = findViewById(R.id.image_user);
        textUsername = findViewById(R.id.text_name);
        textEmail = findViewById(R.id.text_email);


        buttonAddFriend = findViewById(R.id.button_add_friend);
        buttonAddFriend.setOnClickListener(this);

        buttonChat = findViewById(R.id.button_chat);
        buttonChat.setOnClickListener(this);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        groupsRef = FirebaseDatabase.getInstance().getReference(Config.RF_GROUPS);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_add_friend:
                showDialogRequest(user);
                break;
            case R.id.button_chat:
                createGroup();
                break;
        }
    }

    public void createGroup() {
        List<String> chatList = new ArrayList<>();
        chatList.add(currentUser.getUid());
        chatList.add(user.getId());
        final GroupInfo groupInfo = new GroupInfo("", "", "default", 2);
        groupInfo.setChatList(chatList);

        boolean check = false;
        for (GroupInfo i : Common.groupInfoList) {
            if (i.getType() == 2 && i.getChatList().containsAll(chatList)) {
                check = true;
                Common.groupClicked = i;
                Intent intentMessageActivity = new Intent(this, MessageActivity.class);
                startActivity(intentMessageActivity);
                finish();
            }
        }
        if (!check) {
            groupsRef.push().setValue(groupInfo, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    String key = ref.getKey();
                    ref.child("id").setValue(key);
                    groupInfo.setId(key);

                    Common.groupClicked = groupInfo;

                    Intent intentMessageActivity = new Intent(UserInfoActivity.this, MessageActivity.class);
                    startActivity(intentMessageActivity);
                    finish();

                }
            });
        }
    }

    private void showDialogRequest(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Yêu cầu kết bạn");
        builder.setMessage("Bạn có muốn gửi yêu cầu kết bạn đến " + user.getDisplayName());
        builder.setIcon(R.drawable.ic_account_circle);

        AlertDialog dialog = builder.create();
        dialog.setButton(Dialog.BUTTON_NEGATIVE, "Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setButton(Dialog.BUTTON_POSITIVE, "Gửi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO gửi yêu cầu kết bạn

            }
        });
        dialog.show();
    }
}