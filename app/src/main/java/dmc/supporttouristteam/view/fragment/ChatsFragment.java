package dmc.supporttouristteam.view.fragment;

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
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmc.supporttouristteam.data.model.fb.GroupInfo;
import dmc.supporttouristteam.view.adapter.ChatsAdapter;
import dmc.supporttouristteam.presenter.chats.ChatsContract;
import dmc.supporttouristteam.presenter.chats.ChatsPresenter;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.util.Common;
import dmc.supporttouristteam.view.activity.AddGroupActivity;
import dmc.supporttouristteam.view.activity.MessageActivity;
import dmc.supporttouristteam.view.activity.SearchActivity;
import dmc.supporttouristteam.view.activity.TrackingActivity;

public class ChatsFragment extends Fragment implements View.OnClickListener, ChatsContract.View {
    private CircleImageView photo;
    private TextView textName, textShow;
    private Button buttonAddGroup, buttonSearch;
    private RecyclerView recyclerChats;
    private ChatsAdapter chatsAdapter;
    private List<GroupInfo> groupInfoList;
    private ChatsPresenter presenter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        // load uer info
        Glide.with(view.getContext()).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                .placeholder(R.drawable.add_user_male_100).into(photo);
        textName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        presenter = new ChatsPresenter(this);
        presenter.doReadChatList();
    }

    private void init(View view) {
        photo = view.findViewById(R.id.image_user_photo);
        textName = view.findViewById(R.id.text_user_name);
        buttonAddGroup = view.findViewById(R.id.button_add_group);
        buttonSearch = view.findViewById(R.id.button_search);
        textShow = view.findViewById(R.id.text_show);

        buttonAddGroup.setOnClickListener(this);
        buttonSearch.setOnClickListener(this);

        recyclerChats = view.findViewById(R.id.recycler_chats);recyclerChats.setHasFixedSize(true);
        recyclerChats.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void setRecyclerChats(List<GroupInfo> groupInfoList) {
        this.groupInfoList = groupInfoList;
        chatsAdapter = new ChatsAdapter(groupInfoList, presenter);
        recyclerChats.setAdapter(chatsAdapter);
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
    public void navigationToMessageActivity(int pos) {
        Common.groupClicked = groupInfoList.get(pos);
        Intent messageActivity = new Intent(getContext(), MessageActivity.class);
        startActivity(messageActivity);
    }

    @Override
    public void navigationToTrackingActivity(int pos) {
        Common.groupClicked = groupInfoList.get(pos);
        Intent trackingActivity = new Intent(getContext(), TrackingActivity.class);
        trackingActivity.putExtra(Common.EXTRA_GROUP_INFO, Common.groupClicked);
        startActivity(trackingActivity);
    }

    @Override
    public void showRecyclerChats() {
        recyclerChats.setVisibility(View.VISIBLE);
        textShow.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideRecyclerChats() {
        recyclerChats.setVisibility(View.INVISIBLE);
        textShow.setVisibility(View.VISIBLE);
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
            case R.id.button_add_group:
                presenter.navigateToAddGroupActivity();
                break;
            case R.id.button_search:
                presenter.navigateToSearchActivity();
                break;
        }
    }
}