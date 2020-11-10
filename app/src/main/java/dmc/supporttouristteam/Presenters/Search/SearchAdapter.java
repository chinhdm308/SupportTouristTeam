package dmc.supporttouristteam.Presenters.Search;

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
import dmc.supporttouristteam.Models.User;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SeearchViewHolder> {

    private List<User> userList;

    public SearchAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public SeearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_chats, parent, false);
        return new SeearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeearchViewHolder holder, int position) {
        User user = userList.get(position);
        Glide.with(holder.itemView.getContext()).load(user.getProfileImg()).into(holder.photo);
        holder.name.setText(user.getDisplayName());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class SeearchViewHolder extends RecyclerView.ViewHolder {
        CircleImageView photo;
        TextView name;
        public SeearchViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = (CircleImageView) itemView.findViewById(R.id.item_chats_photo);
            name = (TextView) itemView.findViewById(R.id.item_chats_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
