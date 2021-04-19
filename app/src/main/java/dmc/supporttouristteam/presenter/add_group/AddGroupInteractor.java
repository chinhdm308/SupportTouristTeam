package dmc.supporttouristteam.presenter.add_group;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

import dmc.supporttouristteam.data.api.ApiUtils;
import dmc.supporttouristteam.data.model.fb.GroupInfo;
import dmc.supporttouristteam.data.model.fb.User;
import dmc.supporttouristteam.data.model.fb_mes.MyRequest;
import dmc.supporttouristteam.data.model.fb_mes.MyResponse;
import dmc.supporttouristteam.util.Common;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddGroupInteractor implements AddGroupContract.Interactor {
    private static final String TAG = "tagAddGroupInteractor";
    private AddGroupContract.OnOperationListener listener;
    private List<User> participantsList;
    private FirebaseUser currentUser;
    private DatabaseReference groupsRef;

    public AddGroupInteractor(AddGroupContract.OnOperationListener listener) {
        this.listener = listener;
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        groupsRef = FirebaseDatabase.getInstance().getReference().child(Common.RF_GROUPS);
    }

    @Override
    public void readParticipants(DatabaseReference reference) {
        participantsList = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot i : snapshot.getChildren()) {
                    User user = i.getValue(User.class);
                    if (!user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
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
    public void createGroup(String nameGroup, List<User> selectedParticipantList, int type) {
        List<String> chatList = new ArrayList<>();
        chatList.add(currentUser.getUid());
        for (User i : selectedParticipantList) {
            chatList.add(i.getId());
        }
        final GroupInfo groupInfo = new GroupInfo("", nameGroup, "default", type);
        groupInfo.setChatList(chatList);

        if (type == 1) {
            groupsRef.push().setValue(groupInfo, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    String key = ref.getKey();
                    ref.child("id").setValue(key);
                    groupInfo.setId(key);
                    sendMessage(chatList, nameGroup, key);
                    listener.onSuccess(groupInfo);
                }
            });
        }
    }

    private void sendMessage(List<String> chatList, String nameGroup, String idGroup) {
        Common.tokensRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MyRequest myRequest = new MyRequest();
                Map<String, String> dataSend = new HashMap<>();
                dataSend.put(Common.CODE, Common.CODE_ADD_MEMBER_TO_GROUP);
                dataSend.put(Common.TITLE, "Thông báo");
                dataSend.put(Common.CONTENT, "Bạn vừa được thêm vào nhóm " + nameGroup);
                dataSend.put(Common.ID_GROUP, idGroup);

                for (DataSnapshot i : snapshot.getChildren()) {
                    String key = i.getKey();
                    if (chatList.contains(key) && !key.equals(currentUser.getUid())) {
                        myRequest.setTo(i.getValue(String.class));
                        myRequest.setData(dataSend);

                        ApiUtils.start(ApiUtils.BASE_URL_FIREBASE).apiCall()
                                .sendWarningMessage(myRequest)
                                .enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                        if (response.code() == 200) {
                                            if (response.body().success == 1) {
                                                Log.d(TAG, "Gửi thông báo đến các thành viên được thêm vào nhóm thành công");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {

                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}