package dmc.supporttouristteam.Presenters.Message;

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
import dmc.supporttouristteam.Models.Chat;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Utils.Common;
import dmc.supporttouristteam.Utils.Config;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Chat> mData;
    private DatabaseReference usersRef;

    public MessageAdapter(List<Chat> mData) {
        this.mData = mData;
        this.usersRef = FirebaseDatabase.getInstance().getReference(Config.RF_USERS);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == Config.MSG_TYPE_LEFT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_item_left, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_item_right, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Chat chat = mData.get(position);
        holder.showMessage.setText(chat.getMessage());
        if (!chat.getSender().equals(Common.loggedUser.getId())) {
            usersRef.child(chat.getSender()).child("profileImg").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null)
                        Glide.with(holder.itemView.getContext())
                                .load(snapshot.getValue().toString())
                                .into(holder.profileImage);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImage;
        TextView showMessage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.image_user);
            showMessage = itemView.findViewById(R.id.show_message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position).getSender().equals(Common.loggedUser.getId())) {
            return Config.MSG_TYPE_RIGHT;
        } else {
            return Config.MSG_TYPE_LEFT;
        }
    }
}
