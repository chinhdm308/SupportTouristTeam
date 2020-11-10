package dmc.supporttouristteam.Views.Activitis;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.NonNull;
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

import dmc.supporttouristteam.Presenters.Search.SearchAdapter;
import dmc.supporttouristteam.Models.User;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Utils.Config;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView recyclerSearch;
    private SearchAdapter searchAdapter;
    private EditText editTextSearch;
    private List<User> userList;
    private List<User> tmp;
    private FirebaseUser currentUser = Config.FB_AUTH.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().hide();
        init();
        initialize();
        setRecyclerSearch();
        search();
    }

    private void search() {
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tmp.clear();
                for (User user : userList) {
                    if (user.getDisplayName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        tmp.add(user);
                    }
                }
//                searchAdapter = new SearchAdapter(tmp, SearchActivity.this);
//                recyclerSearch.setAdapter(searchAdapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setRecyclerSearch() {
        recyclerSearch.setHasFixedSize(true);
        recyclerSearch.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Config.RF_USERS);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot i : snapshot.getChildren()) {
                    User user = i.getValue(User.class);
                    if (!user.getId().equals(currentUser.getUid())) {
                        userList.add(user);
                    }
                }
//                searchAdapter = new SearchAdapter(userList, SearchActivity.this);
                recyclerSearch.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init() {
        editTextSearch = findViewById(R.id.et_search);
        recyclerSearch = findViewById(R.id.recycler_search);
    }

//    @Override
//    public void onChatsItemClick(int pos) {
//        User user;
//        if (tmp.size() > 0) {
//            user = tmp.get(pos);
//        } else {
//            user = userList.get(pos);
//        }
//
//        Intent intentUserInfo = new Intent(SearchActivity.this, UserInfoActivity.class);
//        intentUserInfo.putExtra(Config.EXTRA_USER, user);
//        startActivity(intentUserInfo);
//        finish();
//    }

    private void initialize() {
        tmp = new ArrayList<>();
    }
}