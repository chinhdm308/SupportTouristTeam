package dmc.supporttouristteam.view.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
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
import dmc.supporttouristteam.data.api.ApiUtils;
import dmc.supporttouristteam.data.model.fb.GroupInfo;
import dmc.supporttouristteam.data.model.fb_mes.MyRequest;
import dmc.supporttouristteam.data.model.fb_mes.MyResponse;
import dmc.supporttouristteam.data.model.fb.User;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.util.Common;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                DatabaseReference acceptList = FirebaseDatabase.getInstance()
                        .getReference(Common.RF_USERS)
                        .child(currentUser.getUid())
                        .child(Common.ACCEPT_LIST);

                acceptList.orderByKey().equalTo(user.getId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue() == null) {
                                    sendFriendRequest(user);
                                } else {
                                    Toast.makeText(UserInfoActivity.this, "Bạn và " + user.getDisplayName() + " đã là bạn bè", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
        dialog.show();
    }

    private void sendFriendRequest(User user) {
        DatabaseReference tokensRef = FirebaseDatabase.getInstance().getReference(Common.RF_TOKENS);
        tokensRef.orderByKey().equalTo(user.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() == null) {
                            Toast.makeText(getApplicationContext(), "token error", Toast.LENGTH_SHORT).show();
                        } else {
                            // Create myRequest
                            MyRequest myRequest = new MyRequest();

                            // Create data
                            Map<String, String> dataSend = new HashMap<>();
                            dataSend.put(Common.TITLE, "Yêu cầu kết bạn");
                            dataSend.put(Common.CONTENT, "Có một yêu cầu kết bạn từ " + currentUser.getDisplayName());
                            dataSend.put(Common.FROM_UID, currentUser.getUid());
                            dataSend.put(Common.TO_UID, user.getId());
                            dataSend.put(Common.CODE, Common.CODE_REQUEST_FRIEND);

                            myRequest.setTo(snapshot.child(user.getId()).getValue(String.class));
                            myRequest.setData(dataSend);

                            ApiUtils.start(ApiUtils.BASE_URL_FIREBASE).apiCall()
                                    .sendFriendRequestToUser(myRequest)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.code() == 200) {
                                                if (response.body().success == 1) {
                                                    Toast.makeText(getApplicationContext(), "Gửi yêu cầu thành công", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

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
        return (User) getIntent().getSerializableExtra(Common.EXTRA_USER);
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
        groupsRef = FirebaseDatabase.getInstance().getReference(Common.RF_GROUPS);
    }

}