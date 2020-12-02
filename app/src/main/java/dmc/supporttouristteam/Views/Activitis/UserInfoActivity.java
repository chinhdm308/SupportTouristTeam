package dmc.supporttouristteam.Views.Activitis;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import dmc.supporttouristteam.Models.User;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Utils.Config;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView userPhoto;
    private TextView textEmail, textUsername;
    private Button buttonAddFriend;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        getSupportActionBar().setTitle("Thông tin người dùng");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        loadUserInfo();

        buttonAddFriend.setOnClickListener(this);
    }

    private void loadUserInfo() {
        user = getDataToIntent();
        if (user != null) {
            Glide.with(this).load(user.getProfileImg()).into(userPhoto);
            textUsername.setText(user.getDisplayName());
            textEmail.setText(user.getEmail());
        }
    }

    private User getDataToIntent() {
        return (User) getIntent().getSerializableExtra(Config.EXTRA_USER);
    }

    private void init() {
        userPhoto = findViewById(R.id.image_user);
        textUsername = findViewById(R.id.text_name);
        textEmail = findViewById(R.id.text_email);
        buttonAddFriend = findViewById(R.id.button_add_friend);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_add_friend) {
            showDialogRequest(user);
        }
    }

    private void showDialogRequest(User user) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyRequestDialog);
        alertDialog.setTitle("Yêu cầu kết bạn");
        alertDialog.setMessage("Bạn có muốn gửi yêu cầu kết bạn đến " + user.getDisplayName());
        alertDialog.setIcon(R.drawable.ic_account_circle);

        alertDialog.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.setPositiveButton("Gửi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }
}