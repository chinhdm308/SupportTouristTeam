package dmc.supporttouristteam.Presenters.Chats;

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
import dmc.supporttouristteam.Models.GroupInfo;
import dmc.supporttouristteam.Models.User;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Utils.Common;
import dmc.supporttouristteam.Utils.Config;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder> {
    private ChatsContract.Presenter presenter;
    private List<GroupInfo> groupInfoList;
    private DatabaseReference usersRef;

    public ChatsAdapter(List<GroupInfo> groupInfoList, ChatsContract.Presenter presenter) {
        this.groupInfoList = groupInfoList;
        this.presenter = presenter;
        usersRef = FirebaseDatabase.getInstance().getReference(Config.RF_USERS);
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
        String userID;
        if (groupInfo.getNumberOfPeople() == 2) {
            List<String> chatList = groupInfo.getChatList();
            if (!chatList.get(0).equals(Common.currentUser.getUid())) {
                userID = chatList.get(0);
            } else {
                userID = chatList.get(1);
            }
            usersRef.child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    Glide.with(holder.itemView.getContext()).load(user.getProfileImg()).into(holder.photo);
                    holder.name.setText(user.getDisplayName());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            if (groupInfo.getImage().equals("default")) {
                Glide.with(holder.itemView.getContext()).load(R.drawable.user1).into(holder.photo);
            } else {
                Glide.with(holder.itemView.getContext()).load(groupInfo.getImage()).into(holder.photo);
            }
            holder.name.setText(groupInfo.getName());
        }
    }

    @Override
    public int getItemCount() {
        return groupInfoList.size();
    }

    class ChatsViewHolder extends RecyclerView.ViewHolder {
        CircleImageView photo;
        TextView name;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.item_chats_photo);
            name = itemView.findViewById(R.id.item_chats_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.doChatsItemClick(getAdapterPosition());
                }
            });
        }
    }
}
