package dmc.supporttouristteam.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmc.supporttouristteam.Models.User;
import dmc.supporttouristteam.R;

public class MemberListAdapter extends ArrayAdapter<User> {

    private Context context;
    private List<User> userList;

    public MemberListAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource);
        userList = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_member, parent, false);


        CircleImageView profilePic = rowView.findViewById(R.id.iv_user_profile_image);
        TextView userName = rowView.findViewById(R.id.tv_user_name);


        User user = userList.get(position);

        userName.setText(user.getDisplayName());
        Glide.with(parent.getContext()).load(user.getProfileImg()).into(profilePic);

        return rowView;
    }

    @Override
    public int getCount() {
        return userList.size();
    }
}
