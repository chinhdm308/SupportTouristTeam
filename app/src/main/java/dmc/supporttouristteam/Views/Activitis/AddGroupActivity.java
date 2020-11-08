package dmc.supporttouristteam.Views.Activitis;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import dmc.supporttouristteam.Adapter.ParticipantsAdapter;
import dmc.supporttouristteam.Adapter.SelectedParticipantsAdapter;
import dmc.supporttouristteam.Models.GroupInfo;
import dmc.supporttouristteam.Models.User;
import dmc.supporttouristteam.Presenters.AddGroup.AddGroupPresenter;
import dmc.supporttouristteam.Presenters.AddGroup.CommonAddGroup;
import dmc.supporttouristteam.Presenters.ParticipantsCallBack;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Utils.Config;
import dmc.supporttouristteam.Views.Fragments.CreateGroupBottomSheetFragment;

public class AddGroupActivity extends AppCompatActivity implements ParticipantsCallBack, CommonAddGroup.View {
    private EditText etSearch;
    private RecyclerView recyclerParticipants, recyclerSelectedParticipants;
    private ParticipantsAdapter participantsAdapter;
    private SelectedParticipantsAdapter selectedParticipantsAdapter;

    private List<User> participantsList;
    private List<User> selectedParticipantsList;

    private FirebaseUser currentUser;

    private AddGroupPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        getSupportActionBar().setTitle("Thêm thành viên");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentUser = Config.FB_AUTH.getCurrentUser();

        mapping();

        presenter = new AddGroupPresenter(this);

        search();
    }

    private void search() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<User> tmp = presenter.getParticipants(charSequence.toString(), participantsList);
                participantsAdapter = new ParticipantsAdapter(tmp, AddGroupActivity.this);
                recyclerParticipants.setAdapter(participantsAdapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void setRecyclerParticipants(List<User> participantList) {
        recyclerParticipants.setHasFixedSize(true);
        recyclerParticipants.setLayoutManager(new LinearLayoutManager(this));
        participantsAdapter = new ParticipantsAdapter(participantList, AddGroupActivity.this);
        recyclerParticipants.setAdapter(participantsAdapter);
    }

    @Override
    public void setRecyclerSelectedParticipants() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerSelectedParticipants.setLayoutManager(linearLayoutManager);
        selectedParticipantsList = new ArrayList<>();
        selectedParticipantsAdapter = new SelectedParticipantsAdapter(selectedParticipantsList);
        recyclerSelectedParticipants.setAdapter(selectedParticipantsAdapter);
    }

    @Override
    public void addParticipant(int pos) {
        selectedParticipantsList.add(participantsList.get(pos));
    }

    @Override
    public void removeParticipant(int pos) {
        selectedParticipantsList.remove(participantsList.get(pos));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onParticipantItemClick(int pos, boolean isAdd) {
        presenter.updatesSelectedParticipants(pos, isAdd);
        selectedParticipantsAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.top_menu_next) {
            presenter.confirmNewGroup(selectedParticipantsList.size());
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(AddGroupActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showCreateGroupBottomSheet() {
        CreateGroupBottomSheetFragment bottomSheetFragment = new CreateGroupBottomSheetFragment(selectedParticipantsList);
        bottomSheetFragment.show(getSupportFragmentManager(), "Bottom Sheet");
    }

    private void createNewGroup(User user) {
        List<String> chatList = new ArrayList<>();
        chatList.add(currentUser.getUid());
        chatList.add(user.getId());
        final GroupInfo groupInfo = new GroupInfo("", "", "", 2);
        groupInfo.setChatList(chatList);
        DatabaseReference referenceGroupList = FirebaseDatabase.getInstance().getReference();
        referenceGroupList.child(Config.RF_GROUPS).push().setValue(groupInfo, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                String key = ref.getKey();
                ref.child("id").setValue(key);
                Intent messageActivity = new Intent(AddGroupActivity.this, MessageActivity.class);
                messageActivity.putExtra(Config.EXTRA_GROUP_INFO, groupInfo);
                startActivity(messageActivity);
                finish();
            }
        });
    }

    private void mapping() {
        recyclerParticipants = findViewById(R.id.recycler_participants);
        recyclerSelectedParticipants = findViewById(R.id.recycler_selected_participants);
        etSearch = findViewById(R.id.et_search);
    }
}