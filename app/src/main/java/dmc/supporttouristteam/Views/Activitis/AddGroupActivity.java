package dmc.supporttouristteam.Views.Activitis;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import dmc.supporttouristteam.Models.User;
import dmc.supporttouristteam.Presenters.AddGroup.AddGroupPresenter;
import dmc.supporttouristteam.Presenters.AddGroup.AddGroupContract;
import dmc.supporttouristteam.Presenters.AddGroup.ParticipantsAdapter;
import dmc.supporttouristteam.Presenters.AddGroup.SelectedParticipantsAdapter;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Utils.Config;
import dmc.supporttouristteam.Views.Fragments.CreateGroupBottomSheetFragment;

public class AddGroupActivity extends AppCompatActivity implements AddGroupContract.View {
    private EditText etSearch;
    private RecyclerView recyclerParticipants, recyclerSelectedParticipants;
    private ParticipantsAdapter participantsAdapter;
    private SelectedParticipantsAdapter selectedParticipantsAdapter;

    private List<User> participantList;
    private List<User> selectedParticipantList;

    private FirebaseUser currentUser;

    private AddGroupPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        getSupportActionBar().setTitle("Thêm thành viên");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        presenter = new AddGroupPresenter(this);
        presenter.readParticipants(FirebaseDatabase.getInstance().getReference(Config.RF_USERS), currentUser);
        setRecyclerSelectedParticipants(selectedParticipantList);

        search();
    }

    private void search() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                presenter.search(charSequence.toString(), participantList);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void setRecyclerParticipants(List<User> participantList) {
        this.participantList = participantList;
        participantsAdapter = new ParticipantsAdapter(participantList, presenter);
        recyclerParticipants.setAdapter(participantsAdapter);
    }

    @Override
    public void setRecyclerParticipantsAfterSearch(List<User> participantList) {
        participantsAdapter = new ParticipantsAdapter(participantList, presenter);
        recyclerParticipants.setAdapter(participantsAdapter);
    }

    @Override
    public void setRecyclerSelectedParticipants(List<User> selectedParticipantList) {
        selectedParticipantsAdapter = new SelectedParticipantsAdapter(selectedParticipantList);
        recyclerSelectedParticipants.setAdapter(selectedParticipantsAdapter);
    }

    @Override
    public void addParticipant(int pos) {
        selectedParticipantList.add(participantList.get(pos));
        selectedParticipantsAdapter.notifyDataSetChanged();
    }

    @Override
    public void removeParticipant(int pos) {
        selectedParticipantList.remove(participantList.get(pos));
        selectedParticipantsAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
            presenter.createGroup(AddGroupActivity.this, selectedParticipantList);
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
        CreateGroupBottomSheetFragment bottomSheetFragment = new CreateGroupBottomSheetFragment(selectedParticipantList);
        bottomSheetFragment.show(getSupportFragmentManager(), "Bottom Sheet");
    }

    private void init() {
        recyclerParticipants = findViewById(R.id.recycler_participants);
        recyclerParticipants.setHasFixedSize(true);
        recyclerParticipants.setLayoutManager(new LinearLayoutManager(this));

        recyclerSelectedParticipants = findViewById(R.id.recycler_selected_participants);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerSelectedParticipants.setLayoutManager(linearLayoutManager);

        etSearch = findViewById(R.id.et_search);

        selectedParticipantList = new ArrayList<>();
        participantList = new ArrayList<>();

        currentUser = Config.FB_AUTH.getCurrentUser();
    }
}