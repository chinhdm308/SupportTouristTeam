package dmc.supporttouristteam.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.util.Common;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private List<String> friendList;
    private DatabaseReference usersRef;

    public FriendAdapter(List<String> friendList) {
        this.friendList = friendList;
        this.usersRef = FirebaseDatabase.getInstance().getReference(Common.RF_USERS);
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        String uid = friendList.get(position);

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

    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    class FriendViewHolder extends RecyclerView.ViewHolder {

        CircleImageView img;
        TextView name;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.item_friend_request_img);
            name = itemView.findViewById(R.id.item_friend_request_name);
        }
    }
}
