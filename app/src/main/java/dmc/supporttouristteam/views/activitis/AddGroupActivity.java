package dmc.supporttouristteam.views.activitis;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmc.supporttouristteam.R;
import dmc.supporttouristteam.adapter.ParticipantsAdapter;
import dmc.supporttouristteam.adapter.SelectedParticipantsAdapter;
import dmc.supporttouristteam.callback.ParticipantsCallBack;
import dmc.supporttouristteam.models.GroupInfo;
import dmc.supporttouristteam.models.User;
import dmc.supporttouristteam.services.CommonService;
import dmc.supporttouristteam.utils.Common;
import dmc.supporttouristteam.views.fragment.CreateGroupBottomSheetFragment;

public class AddGroupActivity extends AppCompatActivity implements ParticipantsCallBack {
    private EditText etSearch;
    private RecyclerView recyclerParticipants, recyclerSelectedParticipants;
    private ParticipantsAdapter participantsAdapter;
    private SelectedParticipantsAdapter selectedParticipantsAdapter;

    private List<User> participantsList;
    private List<User> selectedParticipantsList;

    private FirebaseUser currentUser = Common.FB_AUTH.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        getSupportActionBar().setTitle("Add Participants");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mapping();

        setRecyclerParticipants();

        setRecyclerSelectedParticipants();

        search();
    }

    private void search() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<User> tmp = new ArrayList<>();
                for (User user : participantsList) {
                    if (user.getDisplayName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        tmp.add(user);
                    }
                }
                participantsAdapter = new ParticipantsAdapter(tmp, AddGroupActivity.this);
                recyclerParticipants.setAdapter(participantsAdapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setRecyclerParticipants() {
        recyclerParticipants.setHasFixedSize(true);
        recyclerParticipants.setLayoutManager(new LinearLayoutManager(this));
        participantsList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.RF_USERS);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot i : snapshot.getChildren()) {
                    User user = i.getValue(User.class);
                    if (!user.getId().equals(currentUser.getUid())) {
                        participantsList.add(user);
                    }
                }
                participantsAdapter = new ParticipantsAdapter(participantsList, AddGroupActivity.this);
                recyclerParticipants.setAdapter(participantsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setRecyclerSelectedParticipants() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerSelectedParticipants.setLayoutManager(linearLayoutManager);
        selectedParticipantsList = new ArrayList<>();
        selectedParticipantsAdapter = new SelectedParticipantsAdapter(selectedParticipantsList);
        recyclerSelectedParticipants.setAdapter(selectedParticipantsAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onParticipantItemClick(int pos, boolean isAdd) {
        if (isAdd) {
            selectedParticipantsList.add(participantsList.get(pos));
        } else {
            selectedParticipantsList.remove(participantsList.get(pos));
        }
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
            if (selectedParticipantsList.size() == 0) {
                CommonService.showMessage(AddGroupActivity.this, "You have not choose a participant");
            }
            if (selectedParticipantsList.size() == 1) {
                createNewGroup(selectedParticipantsList.get(0));
            }
            if (selectedParticipantsList.size() > 1) {
                CreateGroupBottomSheetFragment bottomSheetFragment = new CreateGroupBottomSheetFragment(selectedParticipantsList);
                bottomSheetFragment.show(getSupportFragmentManager(), "Bottom Sheet");
            }
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void createNewGroup(User user) {
        List<String> chatList = new ArrayList<>();
        chatList.add(currentUser.getUid());
        chatList.add(user.getId());
        final GroupInfo groupInfo = new GroupInfo("", "", "", 2);
        groupInfo.setChatList(chatList);
        DatabaseReference referenceGroupList = FirebaseDatabase.getInstance().getReference();
        referenceGroupList.child(Common.RF_GROUPS).push().setValue(groupInfo, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                String key = ref.getKey();
                ref.child("id").setValue(key);
                Intent messageActivity = new Intent(AddGroupActivity.this, MessageActivity.class);
                messageActivity.putExtra(Common.EXTRA_GROUP_INFO, groupInfo);
                startActivity(messageActivity);
                finish();
            }
        });
    }

    private void initialize() {

    }

    private void mapping() {
        recyclerParticipants = findViewById(R.id.recycler_participants);
        recyclerSelectedParticipants = findViewById(R.id.recycler_selected_participants);
        etSearch = findViewById(R.id.et_search);
    }
}