package dmc.supporttouristteam.Views.Fragments;

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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmc.supporttouristteam.Presenters.Chats.ItemChatsAdapter;
import dmc.supporttouristteam.Models.GroupInfo;
import dmc.supporttouristteam.Presenters.Chats.ChatsPresenter;
import dmc.supporttouristteam.Presenters.Chats.CommonChats;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Utils.Config;
import dmc.supporttouristteam.Views.Activitis.AddGroupActivity;
import dmc.supporttouristteam.Views.Activitis.SearchActivity;
import dmc.supporttouristteam.Views.Activitis.UserInfoActivity;

public class ChatsFragment extends Fragment implements View.OnClickListener, CommonChats.View {
    private CircleImageView photo;
    private TextView textName;
    private Button buttonAddGroup, buttonSearch;
    private RecyclerView recyclerChats;
    private ItemChatsAdapter itemChatsAdapter;
    private List<GroupInfo> groupInfoList;
    private FirebaseUser currentUser;
    private ChatsPresenter presenter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        currentUser = Config.FB_AUTH.getCurrentUser();
        // load uer info
        Glide.with(view.getContext()).load(currentUser.getPhotoUrl())
                .placeholder(R.drawable.add_user_male_100).into(photo);
        textName.setText(currentUser.getDisplayName());

        photo.setOnClickListener(this);
        buttonAddGroup.setOnClickListener(this);
        buttonSearch.setOnClickListener(this);

        presenter = new ChatsPresenter(this);
        presenter.readChatList(currentUser);
    }

    private void init(View view) {
        photo = view.findViewById(R.id.image_user_photo);
        textName = view.findViewById(R.id.text_user_name);
        buttonAddGroup = view.findViewById(R.id.button_add_group);
        buttonSearch = view.findViewById(R.id.button_search);
        recyclerChats = view.findViewById(R.id.recycler_chats);
    }

    @Override
    public void setRecyclerChats(List<GroupInfo> groupInfoList) {
        recyclerChats.setHasFixedSize(true);
        recyclerChats.setLayoutManager(new LinearLayoutManager(getContext()));
        itemChatsAdapter = new ItemChatsAdapter(groupInfoList, presenter);
        recyclerChats.setAdapter(itemChatsAdapter);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_user_photo:
                presenter.navigateToUserInfoActivity();
                break;
            case R.id.button_add_group:
                presenter.navigateToAddGroupActivity();
                break;
            case R.id.button_search:
                break;
        }
    }
}