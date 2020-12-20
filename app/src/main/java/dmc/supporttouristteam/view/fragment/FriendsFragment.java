package dmc.supporttouristteam.view.fragment;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmc.supporttouristteam.view.adapter.FriendAdapter;
import dmc.supporttouristteam.view.adapter.FriendRequestAdapter;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.util.Common;

public class FriendsFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerFriendRequest, recyclerFriend;
    private FriendRequestAdapter friendRequestAdapter;
    private List<String> friendRequestList, friendList;
    private FriendAdapter friendAdapter;

    private FirebaseUser currentUser;
    private Button buttonHideFriendRequests, buttonHideFriends;
    private TextView textNumberOfFriendRequest, textNumberOfFriend;
    private int numberOfFriendRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();

        registerEventRealtimeForFriendRequest();

        registerEventRealtimeForFriend();
    }

    private void registerEventRealtimeForFriend() {
        DatabaseReference friendRequestsRef = FirebaseDatabase.getInstance().getReference(Common.RF_USERS)
                .child(currentUser.getUid()).child(Common.ACCEPT_LIST);
        friendRequestsRef.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendList.clear();
                int numberOfFriend = 0;
                for (DataSnapshot data : snapshot.getChildren()) {
                    friendList.add(data.getKey());
                    numberOfFriend++;
                }
                textNumberOfFriend.setText("(" + numberOfFriend + ")");
                friendAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void registerEventRealtimeForFriendRequest() {
        DatabaseReference friendRequestsRef = FirebaseDatabase.getInstance().getReference(Common.RF_USERS)
                .child(currentUser.getUid()).child(Common.FRIEND_REQUEST);
        friendRequestsRef.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendRequestList.clear();
                numberOfFriendRequest = 0;
                for (DataSnapshot data : snapshot.getChildren()) {
                    friendRequestList.add(data.getKey());
                    numberOfFriendRequest++;
                }
                textNumberOfFriendRequest.setText("(" + numberOfFriendRequest + ")");
                friendRequestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init() {
        recyclerFriendRequest = getView().findViewById(R.id.recycler_friend_request);
        recyclerFriendRequest.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerFriendRequest.setHasFixedSize(true);
        friendRequestList = new ArrayList<>();
        friendRequestAdapter = new FriendRequestAdapter(friendRequestList);
        recyclerFriendRequest.setAdapter(friendRequestAdapter);

        recyclerFriend = getView().findViewById(R.id.recycler_friend);
        recyclerFriend.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerFriend.setHasFixedSize(true);
        friendList = new ArrayList<>();
        friendAdapter = new FriendAdapter(friendList);
        recyclerFriend.setAdapter(friendAdapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        buttonHideFriendRequests = getView().findViewById(R.id.button_hide_friend_requests);
        buttonHideFriendRequests.setOnClickListener(this);

        textNumberOfFriendRequest = getView().findViewById(R.id.text_number_of_friend_request);
        textNumberOfFriend = getView().findViewById(R.id.text_number_of_friend);

        buttonHideFriends = getView().findViewById(R.id.button_hide_friends);
        buttonHideFriends.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_hide_friend_requests:
                if (buttonHideFriendRequests.getText().toString().toUpperCase().equals("ẨN")) {
                    recyclerFriendRequest.setVisibility(View.GONE);
                    buttonHideFriendRequests.setText("HIỆN");
                } else {
                    recyclerFriendRequest.setVisibility(View.VISIBLE);
                    buttonHideFriendRequests.setText("ẨN");
                }
                break;
            case R.id.button_hide_friends:
                if (buttonHideFriends.getText().toString().toUpperCase().equals("ẨN")) {
                    recyclerFriend.setVisibility(View.GONE);
                    buttonHideFriends.setText("HIỆN");
                } else {
                    recyclerFriend.setVisibility(View.VISIBLE);
                    buttonHideFriends.setText("ẨN");
                }
                break;
        }
    }
}