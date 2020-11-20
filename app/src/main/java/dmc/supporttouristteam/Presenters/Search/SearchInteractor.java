package dmc.supporttouristteam.Presenters.Search;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmc.supporttouristteam.Models.User;
import dmc.supporttouristteam.Utils.Common;

public class SearchInteractor implements SearchContract.Interactor {

    private SearchContract.OnOperationListener listener;
    private List<User> list;

    public SearchInteractor(SearchContract.OnOperationListener listener) {
        this.listener = listener;
        list = new ArrayList<>();
    }

    @Override
    public void readDataUsers(DatabaseReference userRef) {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot i : snapshot.getChildren()) {
                    User user = i.getValue(User.class);
                    if (!user.getId().equals(Common.currentUser.getUid())) {
                        list.add(user);
                    }
                }
                listener.onLoadDataUserSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
