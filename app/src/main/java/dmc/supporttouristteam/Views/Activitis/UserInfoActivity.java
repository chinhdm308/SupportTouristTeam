package dmc.supporttouristteam.Views.Activitis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Models.User;
import dmc.supporttouristteam.Utils.Config;

public class UserInfoActivity extends AppCompatActivity {
    private CircleImageView userPhoto;
    private TextView textEmail, textUsername;
    private Button buttonLogout;
    private FirebaseUser currentUser = Config.FB_AUTH.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        getSupportActionBar().setTitle("User Information");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mapping();

        User user = getDataToIntent();

        if (getDataToIntent() == null) {
            loadData("Tên: " + currentUser.getDisplayName(),
                    currentUser.getPhotoUrl().toString(),
                    "Email: " + currentUser.getEmail());
        } else {
            loadData("Tên: " + user.getDisplayName(),
                    user.getProfileImg(),
                    "Email: " + user.getEmail());
        }

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Config.FB_AUTH.signOut();
                startActivity(new Intent(UserInfoActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void loadData(String username, String photo, String email) {
        Glide.with(this).load(photo).into(userPhoto);
        textUsername.setText(username);
        textEmail.setText(email);
    }

    private User getDataToIntent() {
        User user = (User) getIntent().getSerializableExtra(Config.EXTRA_USER);
        return (user != null) ? user : null;
    }

    private void mapping() {
        userPhoto = findViewById(R.id.image_user);
        textUsername = findViewById(R.id.text_user_name);
        textEmail = findViewById(R.id.text_user_email);
        buttonLogout = findViewById(R.id.button_logout);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }
}