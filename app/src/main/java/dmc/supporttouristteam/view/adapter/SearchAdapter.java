package dmc.supporttouristteam.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmc.supporttouristteam.data.model.fb.User;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.presenter.search.SearchContract;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private List<User> userList;
    private SearchContract.Presenter presenter;

    public SearchAdapter(List<User> userList, SearchContract.Presenter presenter) {
        this.userList = userList;
        this.presenter = presenter;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_chats, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        User user = userList.get(position);
        Glide.with(holder.itemView.getContext()).load(user.getProfileImg()).into(holder.photo);
        holder.name.setText(user.getDisplayName());
        holder.button.setVisibility(View.INVISIBLE);
        holder.lastMessage.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        CircleImageView photo;
        TextView name, lastMessage;
        Button button;
        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.item_chats_photo);
            name = itemView.findViewById(R.id.item_chats_name);
            lastMessage = itemView.findViewById(R.id.item_chats_last_message);
            button = itemView.findViewById(R.id.item_chats_button_show_location);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.doUserItemClick(userList.get(getAdapterPosition()));
                }
            });
        }
    }
}
