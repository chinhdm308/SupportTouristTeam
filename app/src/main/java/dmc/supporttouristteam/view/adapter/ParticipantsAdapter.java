package dmc.supporttouristteam.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmc.supporttouristteam.data.model.fb.User;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.presenter.add_group.AddGroupContract;

public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.ParticipantViewHolder> {
    private List<User> userList, userStatusList;
    private AddGroupContract.Presenter presenter;

    public ParticipantsAdapter(List<User> userList, AddGroupContract.Presenter presenter, List<User> userStatusList) {
        this.userList = userList;
        this.presenter = presenter;
        this.userStatusList = userStatusList;
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
        if (userStatusList.contains(user)) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }
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
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean b = checkBox.isChecked();
                    presenter.doParticipantItemClick(userList.get(getAdapterPosition()), b);
                }
            });
        }
    }
}