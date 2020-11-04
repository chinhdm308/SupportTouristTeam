package dmc.supporttouristteam.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.callback.ParticipantsCallBack;
import dmc.supporttouristteam.models.User;

public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.ParticipantViewHolder> {

    List<User> userList;
    ParticipantsCallBack listener;

    public ParticipantsAdapter(List<User> userList, ParticipantsCallBack listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_participant, parent, false);
        return new ParticipantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantViewHolder holder, int position) {
        User user = userList.get(position);
        Glide.with(holder.itemView.getContext()).load(user.getProfileImg()).into(holder.photo);
        holder.name.setText(user.getDisplayName());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ParticipantViewHolder extends RecyclerView.ViewHolder {
        CircleImageView photo;
        TextView name;
        CheckBox checkBox;
        public ParticipantViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.item_participant_photo);
            name = itemView.findViewById(R.id.item_participant_name);
            checkBox = itemView.findViewById(R.id.item_participant_check_box);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    listener.onParticipantItemClick(getAdapterPosition(), b);
                }
            });
        }
    }
}
