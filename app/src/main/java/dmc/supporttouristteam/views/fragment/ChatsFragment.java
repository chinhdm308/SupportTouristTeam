package dmc.supporttouristteam.views.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.adapter.ItemChatsAdapter;
import dmc.supporttouristteam.callback.ChatsCallBack;
import dmc.supporttouristteam.models.GroupInfo;
import dmc.supporttouristteam.presenters.chat.ChatsPresenter;
import dmc.supporttouristteam.presenters.chat.ChatsView;
import dmc.supporttouristteam.utils.Common;
import dmc.supporttouristteam.views.activitis.AddGroupActivity;
import dmc.supporttouristteam.views.activitis.MessageActivity;
import dmc.supporttouristteam.views.activitis.SearchActivity;
import dmc.supporttouristteam.views.activitis.UserInfoActivity;

public class ChatsFragment extends Fragment implements ChatsCallBack, View.OnClickListener, ChatsView {
    private CircleImageView photo;
    private TextView textName;
    private Button buttonAddGroup, buttonSearch;
    private RecyclerView recyclerChats;
    private ItemChatsAdapter itemChatsAdapter;
    private List<GroupInfo> groupInfoList;
    private FirebaseUser currentUser = Common.FB_AUTH.getCurrentUser();
    private ChatsPresenter chatsPresenter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapping(view);
        loadUserInfo(view);
        photo.setOnClickListener(this);
        buttonAddGroup.setOnClickListener(this);
        buttonSearch.setOnClickListener(this);
        chatsPresenter = new ChatsPresenter(this);
        chatsPresenter.setRecyclerChats();
    }

    @Override
    public void setRecyclerChats() {
        recyclerChats.setHasFixedSize(true);
        recyclerChats.setLayoutManager(new LinearLayoutManager(getContext()));
        groupInfoList = new ArrayList<>();
        DatabaseReference referenceGroupList = FirebaseDatabase.getInstance().getReference(Common.RF_GROUPS);
        referenceGroupList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupInfoList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroupInfo groupInfo = snapshot.getValue(GroupInfo.class);
                    List<String> chatList = groupInfo.getChatList();
                    if (chatList.indexOf(currentUser.getUid()) >= 0) {
                        groupInfoList.add(groupInfo);
                        itemChatsAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        itemChatsAdapter = new ItemChatsAdapter(groupInfoList, this);
        recyclerChats.setAdapter(itemChatsAdapter);
    }

    @Override
    public void loadUserInfo(View view) {
        Glide.with(view.getContext()).load(currentUser.getPhotoUrl())
                .placeholder(R.drawable.add_user_male_100).into(photo);
        textName.setText(currentUser.getDisplayName());
    }

    @Override
    public void navigateToUserInfoActivity() {
        startActivity(new Intent(getContext(), UserInfoActivity.class));
    }

    @Override
    public void navigateToAddGroupActivity() {
        startActivity(new Intent(getContext(), AddGroupActivity.class));
    }

    @Override
    public void navigateToSearchActivity() {
        startActivity(new Intent(getContext(), SearchActivity.class));
    }

    private void mapping(View view) {
        photo = view.findViewById(R.id.image_user_photo);
        textName = view.findViewById(R.id.text_user_name);
        buttonAddGroup = view.findViewById(R.id.button_add_group);
        buttonSearch = view.findViewById(R.id.button_search);
        recyclerChats = view.findViewById(R.id.recycler_chats);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onChatsItemClick(int pos) {
        Intent messageActivity = new Intent(getContext(), MessageActivity.class);
        messageActivity.putExtra(Common.EXTRA_GROUP_INFO, groupInfoList.get(pos));
        startActivity(messageActivity);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_user_photo:
                chatsPresenter.navigateToUserInfoActivity();
                break;
            case R.id.button_add_group:
                chatsPresenter.navigateToAddGroupActivity();
                break;
            case R.id.button_search:
                chatsPresenter.navigateToSearchActivity();
                break;
        }
    }
}