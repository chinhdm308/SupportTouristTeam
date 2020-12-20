package dmc.supporttouristteam.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.util.Common;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHolder> {

    private List<String> friendRequestList;
    private FirebaseUser currentUser;
    private DatabaseReference usersRef;

    public FriendRequestAdapter(List<String> friendRequestList) {
        this.friendRequestList = friendRequestList;
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        this.usersRef = FirebaseDatabase.getInstance().getReference(Common.RF_USERS);
    }

    @NonNull
    @Override
    public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_request, parent, false);
        return new FriendRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position) {
        String uid = friendRequestList.get(position);

        usersRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.name.setText(snapshot.child("displayName").getValue(String.class));
                Glide.with(holder.itemView.getContext())
                        .load(snapshot.child("profileImg").getValue(String.class))
                        .into(holder.img);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.buttonDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFriendRequest(uid);
            }
        });

        holder.buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAcceptList(uid);
                deleteFriendRequest(uid);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendRequestList.size();
    }

    class FriendRequestViewHolder extends RecyclerView.ViewHolder {

        CircleImageView img;
        TextView name;
        ImageView buttonAccept, buttonDecline;

        public FriendRequestViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.item_friend_request_img);
            name = itemView.findViewById(R.id.item_friend_request_name);
            buttonAccept = itemView.findViewById(R.id.item_friend_request_accept);
            buttonDecline = itemView.findViewById(R.id.item_friend_request_decline);
        }
    }

    private void addAcceptList(String uid) {
        usersRef.child(currentUser.getUid()).child(Common.ACCEPT_LIST)
                .child(uid).setValue(uid);

        usersRef.child(uid).child(Common.ACCEPT_LIST)
                .child(currentUser.getUid()).setValue(currentUser.getUid());
    }

    private void deleteFriendRequest(String uid) {
        usersRef.child(currentUser.getUid()).child(Common.FRIEND_REQUEST)
                .child(uid).removeValue();
    }
}
