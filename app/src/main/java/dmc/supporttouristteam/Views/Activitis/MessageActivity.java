package dmc.supporttouristteam.Views.Activitis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmc.supporttouristteam.Models.Chat;
import dmc.supporttouristteam.Models.GroupInfo;
import dmc.supporttouristteam.Presenters.Message.MessageAdapter;
import dmc.supporttouristteam.Presenters.Message.MessageContract;
import dmc.supporttouristteam.Presenters.Message.MessagePresenter;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Utils.Common;
import dmc.supporttouristteam.Utils.Config;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener, MessageContract.View {
    private TextView textGroupName;
    private ImageView buttonBack, buttonSend;
    private Button buttonShowLocation;
    private CircleImageView imageGroup;
    private EditText etMessage;
    private RecyclerView recyclerViewMessage;
    private MessageAdapter messageAdapter;
    private MessagePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        getSupportActionBar().hide();

        init();

        presenter.doLoadDataGroupInfo(getDataToIntent());
        presenter.doReadDataMessages();

        buttonBack.setOnClickListener(this);
        buttonShowLocation.setOnClickListener(this);
        buttonSend.setOnClickListener(this);
    }

    @Override
    public void setMessageAdapter(List<Chat> chatList) {
        messageAdapter = new MessageAdapter(chatList);
        recyclerViewMessage.setAdapter(messageAdapter);
    }

    private void init() {
        textGroupName = findViewById(R.id.text_group_name);
        buttonBack = findViewById(R.id.button_back);
        buttonSend = findViewById(R.id.button_send);
        buttonShowLocation = findViewById(R.id.button_show_location);
        imageGroup = findViewById(R.id.image_group);
        etMessage = findViewById(R.id.et_message);
        recyclerViewMessage = findViewById(R.id.recycler_message);

        recyclerViewMessage.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewMessage.setLayoutManager(linearLayoutManager);

        presenter = new MessagePresenter(this);
    }

    private GroupInfo getDataToIntent() {
        return (GroupInfo) getIntent().getSerializableExtra(Config.EXTRA_GROUP_INFO);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_back:
                finish();
                break;
            case R.id.button_show_location:
                Intent trackingActivity = new Intent(MessageActivity.this, TrackingActivity.class);
                trackingActivity.putExtra(Config.EXTRA_GROUP_INFO, Common.groupClicked);
                startActivity(trackingActivity);
                break;
            case R.id.button_send:
                String msg = etMessage.getText().toString();
                presenter.doSendMessage(msg);
                etMessage.setText("");
                break;
        }
    }

    @Override
    public void setImageGroup(String img) {
        if (img != null) {
            Glide.with(MessageActivity.this).load(img).into(imageGroup);
        } else {
            Glide.with(MessageActivity.this).load(R.drawable.user1).into(imageGroup);
        }
    }

    @Override
    public void setNameGroup(String name) {
        textGroupName.setText(name);
    }
}