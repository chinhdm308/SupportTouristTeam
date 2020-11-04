package dmc.supporttouristteam.views.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import dmc.supporttouristteam.R;
import dmc.supporttouristteam.models.GroupInfo;
import dmc.supporttouristteam.models.User;
import dmc.supporttouristteam.utils.Common;
import dmc.supporttouristteam.views.activitis.MessageActivity;

public class CreateGroupBottomSheetFragment extends BottomSheetDialogFragment {
    private EditText etGroupName;
    private Button buttonCancel, buttonCreate;
    private List<User> selectedParticipantsList;
    private FirebaseUser currentUser = Common.FB_AUTH.getCurrentUser();

    public CreateGroupBottomSheetFragment(List<User> selectedParticipantsList) {
        this.selectedParticipantsList = selectedParticipantsList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etGroupName = view.findViewById(R.id.et_group_name);
        buttonCancel = view.findViewById(R.id.button_cancel);
        buttonCreate = view.findViewById(R.id.button_create);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etGroupName.getText().toString().isEmpty()) {
                    StringBuilder name = new StringBuilder();
                    for (User i : selectedParticipantsList) {
                        String[] tmp = i.getDisplayName().split(" ");
                        name.append(tmp[tmp.length - 1] + ",");
                    }
                    name.deleteCharAt(name.length() - 1);
                    createNewGroup(name.toString());
                } else {
                    createNewGroup(etGroupName.getText().toString());
                }
            }
        });
    }

    private void createNewGroup(String nameGroup) {
        List<String> chatList = new ArrayList<>();
        chatList.add(currentUser.getUid());
        for (User i : selectedParticipantsList) {
            chatList.add(i.getId());
        }
        final GroupInfo groupInfo = new GroupInfo("", nameGroup, "default", chatList.size());
        groupInfo.setChatList(chatList);
        DatabaseReference referenceGroupList = FirebaseDatabase.getInstance().getReference();
        referenceGroupList.child(Common.RF_GROUPS).push().setValue(groupInfo, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                String key = ref.getKey();
                ref.child("id").setValue(key);
                Intent messageActivity = new Intent(getContext(), MessageActivity.class);
                messageActivity.putExtra(Common.EXTRA_GROUP_INFO, groupInfo);
                startActivity(messageActivity);
                getActivity().finish();
            }
        });
    }
}
