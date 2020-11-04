package dmc.supporttouristteam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.models.Chat;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private Context mContext;
    private List<Chat> mData;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    public MessageAdapter(Context mContext, List<Chat> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_LEFT) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_msg_item_left, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_msg_item_right, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Chat chat = mData.get(position);
        holder.showMessage.setText(chat.getMessage());

        Glide.with(mContext).load(R.drawable.user1).into(holder.profileImage);

        if (position == mData.size() - 1) {
            if (chat.isSeen()) {
                holder.txtSeen.setText("Seen");
            } else {
                holder.txtSeen.setText("Delivered");
            }
        } else {
            holder.txtSeen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImage;
        TextView showMessage, txtSeen;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.image_user);
            showMessage = itemView.findViewById(R.id.show_message);
            txtSeen = itemView.findViewById(R.id.txt_seen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mData.get(position).getSender().equals(currentUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
