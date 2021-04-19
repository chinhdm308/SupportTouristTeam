package dmc.supporttouristteam.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmc.supporttouristteam.data.model.fb.GroupInfo;
import dmc.supporttouristteam.data.model.fb.User;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.presenter.chats.ChatsContract;
import dmc.supporttouristteam.util.Common;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder> {
    private ChatsContract.Presenter presenter;
    private List<GroupInfo> groupInfoList;
    private DatabaseReference usersRef;
    private FirebaseUser currentUser;

    public ChatsAdapter(List<GroupInfo> groupInfoList, ChatsContract.Presenter presenter) {
        this.groupInfoList = groupInfoList;
        this.presenter = presenter;
        usersRef = FirebaseDatabase.getInstance().getReference(Common.RF_USERS);
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chats, parent, false);
        return new ChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position) {
        GroupInfo groupInfo = groupInfoList.get(position);
        if (groupInfo.getType() != 2) {
            holder.name.setText(groupInfo.getName());
            if (groupInfo.getImage().equals("default")) {
                Glide.with(holder.itemView.getContext()).load(R.drawable.user1).into(holder.photo);
            } else {
                Glide.with(holder.itemView.getContext()).load(groupInfo.getImage()).into(holder.photo);
            }
        } else {
            String userID = groupInfo.getChatList().get(0).equals(currentUser.getUid()) ? groupInfo.getChatList().get(1) : groupInfo.getChatList().get(0);
            usersRef.child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        Glide.with(holder.itemView.getContext()).load(user.getProfileImg()).into(holder.photo);
                        holder.name.setText(user.getDisplayName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        DatabaseReference lastMessageRef = FirebaseDatabase.getInstance().getReference(Common.RF_CHATS);
        Query lastM = lastMessageRef.child(groupInfo.getId()).orderByKey().limitToLast(1);
        lastM.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    for (DataSnapshot d : snapshot.getChildren()) {
                        holder.lastMessage.setText(d.child("message").getValue(String.class));
                    }
                } else {
                    holder.lastMessage.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return groupInfoList.size();
    }

    class ChatsViewHolder extends RecyclerView.ViewHolder {
        CircleImageView photo;
        TextView name, lastMessage;
        Button buttonShowLocation;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.item_chats_photo);
            name = itemView.findViewById(R.id.item_chats_name);
            lastMessage = itemView.findViewById(R.id.item_chats_last_message);
            buttonShowLocation = itemView.findViewById(R.id.item_chats_button_show_location);

            buttonShowLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.doShowLocation(getAdapterPosition());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.doChatsItemClick(getAdapterPosition());
                }
            });
        }
    }
}
