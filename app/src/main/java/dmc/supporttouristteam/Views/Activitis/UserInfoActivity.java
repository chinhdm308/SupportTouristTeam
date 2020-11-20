package dmc.supporttouristteam.Views.Activitis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import dmc.supporttouristteam.Models.User;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Service.TrackerService;
import dmc.supporttouristteam.Utils.Common;
import dmc.supporttouristteam.Utils.Config;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private CircleImageView userPhoto;
    private TextView textEmail, textUsername;
    private Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        getSupportActionBar().setTitle("Thông tin người dùng");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        loadUserInfo();

        buttonLogout.setOnClickListener(this);
    }

    private void loadUserInfo() {
        User user = getDataToIntent();
        if (user != null) {
            buttonLogout.setVisibility(View.INVISIBLE);
            Glide.with(this).load(user.getProfileImg()).into(userPhoto);
            textUsername.setText(user.getDisplayName());
            textEmail.setText(user.getEmail());
        } else {
            buttonLogout.setVisibility(View.VISIBLE);
            Glide.with(this).load(Common.currentUser.getPhotoUrl()).into(userPhoto);
            textUsername.setText(Common.currentUser.getDisplayName());
            textEmail.setText(Common.currentUser.getEmail());
        }
    }

    private User getDataToIntent() {
        return (User) getIntent().getSerializableExtra(Config.EXTRA_USER);
    }

    private void init() {
        userPhoto = findViewById(R.id.image_user);
        textUsername = findViewById(R.id.txt_name);
        textEmail = findViewById(R.id.txt_email);
        buttonLogout = findViewById(R.id.button_logout);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_logout) {
            stopService(new Intent(getApplicationContext(), TrackerService.class));
            Common.FB_AUTH.signOut();
            startActivity(new Intent(UserInfoActivity.this, LoginActivity.class));
            finish();
        }
    }
}