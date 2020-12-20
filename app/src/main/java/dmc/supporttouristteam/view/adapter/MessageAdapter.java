package dmc.supporttouristteam.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmc.supporttouristteam.data.model.Chat;
import dmc.supporttouristteam.data.model.User;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.util.Common;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Chat> mData;
    private DatabaseReference usersRef;

    public MessageAdapter(List<Chat> mData) {
        this.mData = mData;
        this.usersRef = FirebaseDatabase.getInstance().getReference(Common.RF_USERS);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == Common.MSG_TYPE_LEFT) {
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
        if (!chat.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            usersRef.child(chat.getSender()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        User user = snapshot.getValue(User.class);
                        holder.txtName.setText(user.getDisplayName());
                        Glide.with(holder.itemView.getContext())
                                .load(user.getProfileImg())
                                .into(holder.profileImage);
                    }
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
        TextView showMessage, txtName;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.image_user);
            showMessage = itemView.findViewById(R.id.show_message);
            txtName = itemView.findViewById(R.id.text_name);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return Common.MSG_TYPE_RIGHT;
        } else {
            return Common.MSG_TYPE_LEFT;
        }
    }
}
