package dmc.supporttouristteam.view.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmc.supporttouristteam.view.adapter.MemberListAdapter;
import dmc.supporttouristteam.data.model.fb.User;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.util.Common;

public class ChatInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView imageChat;
    private TextView textChatName, textShowMember, textRequest, textExitChat, textEditImage, textEditName, textAddMember;
    private CardView cardview1, cardview2;

    private FirebaseUser currentUser;

    private Uri pickedImgUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_info);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        loadDataInfoChat();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_edit_image:
                checkAndRequestForPermission();
                break;
            case R.id.text_edit_name:
                showAlertDialogEditNameChat();
                break;
            case R.id.text_show_member:
                showMember();
                break;
            case R.id.text_add_member:
                addMember();
                break;
            case R.id.text_request:

                break;
            case R.id.text_exit_chat:
                removeChat();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                pickedImgUri = result.getUri();
                imageChat.setImageURI(pickedImgUri);

                showAlertDialog();


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void loadDataInfoChat() {
        if (Common.groupClicked != null) {
            if (Common.groupClicked.getType() == 2) {
                cardview1.setVisibility(View.GONE);
                cardview2.setVisibility(View.GONE);

                textExitChat.setText("Xóa cuộc trò chuyện");

                List<String> chatList = Common.groupClicked.getChatList();
                String userID = chatList.get(0).equals(currentUser.getUid()) ? chatList.get(1) : chatList.get(0);

                Common.usersRef.child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            Glide.with(getApplicationContext()).load(user.getProfileImg()).into(imageChat);
                            textChatName.setText(user.getDisplayName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                if (Common.groupClicked.getImage().equals("default")) {
                    Glide.with(getApplicationContext()).load(R.drawable.user1).into(imageChat);
                } else {
                    Glide.with(getApplicationContext()).load(Common.groupClicked.getImage()).into(imageChat);
                }
                textChatName.setText(Common.groupClicked.getName());
            }
        }
    }

    private void init() {
        imageChat = findViewById(R.id.image_chat);
        textChatName = findViewById(R.id.text_chat_name);
        textShowMember = findViewById(R.id.text_show_member);
        textExitChat = findViewById(R.id.text_exit_chat);
        textEditName = findViewById(R.id.text_edit_name);
        textEditImage = findViewById(R.id.text_edit_image);
        textRequest = findViewById(R.id.text_request);
        textAddMember = findViewById(R.id.text_add_member);
        cardview1 = findViewById(R.id.cardview_1);
        cardview2 = findViewById(R.id.cardview_2);

        textShowMember.setOnClickListener(this);
        textExitChat.setOnClickListener(this);
        textEditName.setOnClickListener(this);
        textEditImage.setOnClickListener(this);
        textRequest.setOnClickListener(this);
        textAddMember.setOnClickListener(this);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void checkAndRequestForPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // show explanation
                Common.showExplanation(this, this, "", "Bạn cần cấp quyền cho ứng dụng để có trải nghiệm tốt hơn",
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Common.MY_REQUEST_CODE);
            } else {
                Common.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, Common.MY_REQUEST_CODE);
            }
        } else {
            Common.openGallery(ChatInfoActivity.this);
        }
    }

    private void updateImageGroup() {
        if (!Common.groupClicked.getImage().equals("default")) {
            StorageReference groupPhotosRef = FirebaseStorage.getInstance()
                    .getReferenceFromUrl(Common.groupClicked.getImage());
            groupPhotosRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // File deleted successfully
                    Log.d(Common.TAG, "onSuccess: deleted file");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                    Log.d(Common.TAG, "onFailure: did not delete file");
                }
            });
        }

        // need to upload user photo to firebase storage and get url
        StorageReference imageFilePath = Common.groupPhotosRef.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putBytes(Common.downsizedImageBytes(ChatInfoActivity.this, pickedImgUri))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // image uploaded successfully - now we can get our image url
                        imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Common.groupsRef.child(Common.groupClicked.getId()).child("image")
                                        .setValue(uri.toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Common.groupClicked.setImage(uri.toString());
                                                    Toast.makeText(ChatInfoActivity.this, "Thay đổi thành công", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(ChatInfoActivity.this, "Thay đổi thất bại", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });
                    }
                });
    }

    private void removeChat() {
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle(textExitChat.getText().toString());
        alert.setMessage("Bạn muốn " + textExitChat.getText().toString().toLowerCase() + " này không ?");
        alert.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Common.groupClicked.getType() == 1) {
                    Common.groupClicked.getChatList().remove(currentUser.getUid());
                    if (Common.groupClicked.getChatList().size() == 0) {
                        FirebaseDatabase.getInstance().getReference(Common.RF_CHATS).child(Common.groupClicked.getId()).removeValue();
                        Common.groupsRef.child(Common.groupClicked.getId()).removeValue();
                    } else {
                        Common.groupsRef.child(Common.groupClicked.getId()).setValue(Common.groupClicked);
                    }

                } else {
                    FirebaseDatabase.getInstance().getReference(Common.RF_CHATS).child(Common.groupClicked.getId()).removeValue();
                    Common.groupsRef.child(Common.groupClicked.getId()).removeValue();
                }
                Common.groupClicked = null;
                finish();
            }
        });
        alert.setButton(Dialog.BUTTON_NEGATIVE, "Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    private void addMember() {
        List<User> userList = new ArrayList<>();
        Common.usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        User user = data.getValue(User.class);
                        if (!Common.groupClicked.getChatList().contains(user.getId())) {
                            userList.add(user);
                        }
                    }

                    showAlertDialogUserList(userList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showMember() {
        List<User> userList = new ArrayList<>();
        Common.usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        User user = data.getValue(User.class);
                        if (Common.groupClicked.getChatList().contains(user.getId())) {
                            userList.add(user);
                        }
                    }
                    showAlertDialogMemberList(userList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showAlertDialogUserList(List<User> userList) {
        final Dialog dialog1 = new Dialog(ChatInfoActivity.this);
        dialog1.setContentView(R.layout.dialog_member_listview);
        if (dialog1.getWindow() != null) {
            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // this is optional
        }

        ListView listView = dialog1.findViewById(R.id.lv_members);
        TextView text = dialog1.findViewById(R.id.text_popup_title);
        text.setText("Người dùng:");

        ArrayAdapter arrayAdapter = new MemberListAdapter(ChatInfoActivity.this, R.layout.item_member, userList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            // TODO : Listen to click callbacks at the position
            AlertDialog alert = new AlertDialog.Builder(this).create();
            alert.setTitle("Thêm thành viên");
            alert.setMessage("Bạn muốn thêm " + userList.get(position).getDisplayName() + " vào nhóm không ?");
            alert.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Common.groupClicked.getChatList().add(userList.get(position).getId());
                    Common.groupsRef.child(Common.groupClicked.getId()).setValue(Common.groupClicked);
                    dialog1.dismiss();
                }
            });
            alert.setButton(Dialog.BUTTON_NEGATIVE, "Hủy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alert.show();

        });
        dialog1.show();
    }

    private void showAlertDialogMemberList(List<User> userList) {
        Dialog dialog = new Dialog(ChatInfoActivity.this);
        dialog.setContentView(R.layout.dialog_member_listview);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // this is optional
        }

        ListView listView = dialog.findViewById(R.id.lv_members);
        TextView text = dialog.findViewById(R.id.text_popup_title);
        text.setText("Thành viên:");

        ArrayAdapter arrayAdapter = new MemberListAdapter(ChatInfoActivity.this, R.layout.item_member, userList);
        listView.setAdapter(arrayAdapter);
        dialog.show();
    }

    private void showAlertDialog() {
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle("Thay đổi ảnh nhóm");
        alert.setMessage("Bạn có muốn thay đổi ảnh nhóm không ?");
        alert.setCancelable(false);
        alert.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateImageGroup();
            }
        });
        alert.setButton(Dialog.BUTTON_NEGATIVE, "Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Common.groupClicked.getImage().equals("default")) {
                    Glide.with(getApplicationContext()).load(R.drawable.user1).into(imageChat);
                } else {
                    Glide.with(getApplicationContext()).load(Common.groupClicked.getImage()).into(imageChat);
                }
                dialog.dismiss();
            }
        });

        alert.show();
    }

    private void showAlertDialogEditNameChat() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_edit_account, viewGroup, false);

        EditText edtContent = dialogView.findViewById(R.id.edt_content);
        TextView text = dialogView.findViewById(R.id.text_request_edit);

        text.setText("Thay đổi tên nhóm");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String getText = edtContent.getText().toString();
                Common.groupsRef.child(Common.groupClicked.getId()).child("name")
                        .setValue(getText)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ChatInfoActivity.this, "Thay đổi thành công", Toast.LENGTH_SHORT).show();
                                    textChatName.setText(getText);
                                    Common.groupClicked.setName(getText);
                                    alertDialog.dismiss();
                                }
                            }
                        });
            }
        });
        alertDialog.show();
    }

}
