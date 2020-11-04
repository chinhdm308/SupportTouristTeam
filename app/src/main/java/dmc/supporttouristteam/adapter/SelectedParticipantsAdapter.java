package dmc.supporttouristteam.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.models.User;

public class SelectedParticipantsAdapter extends RecyclerView.Adapter<SelectedParticipantsAdapter.SelectedParticipantsViewHolder> {
    List<User> selectedParticipantsList;

    public SelectedParticipantsAdapter(List<User> selectedParticipantsList) {
        this.selectedParticipantsList = selectedParticipantsList;
    }

    @NonNull
    @Override
    public SelectedParticipantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selected_participants, parent, false);
       return new SelectedParticipantsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedParticipantsViewHolder holder, int position) {
        User user = selectedParticipantsList.get(position);
        Glide.with(holder.itemView.getContext()).load(user.getProfileImg()).into(holder.photo);
        holder.name.setText(user.getDisplayName());
    }

    @Override
    public int getItemCount() {
        return selectedParticipantsList.size();
    }

    public class SelectedParticipantsViewHolder extends RecyclerView.ViewHolder {
        CircleImageView photo;
        TextView name;

        public SelectedParticipantsViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.item_selected_participants_photo);
            name = itemView.findViewById(R.id.item_selected_participants_name);
        }
    }
}
