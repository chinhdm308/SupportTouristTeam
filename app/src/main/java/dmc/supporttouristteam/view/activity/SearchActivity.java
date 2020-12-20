package dmc.supporttouristteam.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import dmc.supporttouristteam.data.model.User;
import dmc.supporttouristteam.view.adapter.SearchAdapter;
import dmc.supporttouristteam.presenter.search.SearchContract;
import dmc.supporttouristteam.presenter.search.SearchPresenter;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.util.Common;

public class SearchActivity extends AppCompatActivity implements SearchContract.View {
    private RecyclerView recyclerSearch;
    private SearchAdapter searchAdapter;
    private EditText editTextSearch;
    private List<User> userList;
    private DatabaseReference userRef;
    private SearchPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setTitle("Tìm kiếm người dùng");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        presenter.doSetRecyclerSearch(userRef);

        search();
    }

    private void search() {
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                presenter.doSearchUser(charSequence.toString(), userList);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void init() {
        editTextSearch = findViewById(R.id.edt_search);

        recyclerSearch = findViewById(R.id.recycler_search);
        recyclerSearch.setHasFixedSize(true);
        recyclerSearch.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();

        presenter = new SearchPresenter(this);

        userRef = FirebaseDatabase.getInstance().getReference(Common.RF_USERS);
    }

    @Override
    public void setRecyclerSearch(List<User> userList) {
        this.userList = userList;
        searchAdapter = new SearchAdapter(userList, presenter);
        recyclerSearch.setAdapter(searchAdapter);
    }

    @Override
    public void setRecyclerSearchAfter(List<User> tmp) {
        searchAdapter = new SearchAdapter(tmp, presenter);
        recyclerSearch.setAdapter(searchAdapter);
    }

    @Override
    public void navigationToUserInfoActivity(User user) {
        Intent intent = new Intent(getApplicationContext(), UserInfoActivity.class);
        intent.putExtra(Common.EXTRA_USER, user);
        startActivity(intent);
    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        finish();
        return super.onNavigateUp();
    }
}