package dmc.supporttouristteam.presenter.search;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import dmc.supporttouristteam.data.model.fb.User;
import dmc.supporttouristteam.util.Common;

public class SearchPresenter implements SearchContract.Presenter, SearchContract.OnOperationListener {
    private SearchContract.View view;
    private SearchInteractor interactor;

    public SearchPresenter(SearchContract.View view) {
        this.view = view;
        this.interactor = new SearchInteractor(this);
    }

    @Override
    public void doSetRecyclerSearch(DatabaseReference userRef) {
        interactor.readDataUsers(userRef);
    }

    @Override
    public void doSearchUser(String s, List<User> userList) {
        List<User> tmp = new ArrayList<>();
        for (User user : userList) {
            if (Common.removeAccent(user.getDisplayName().toLowerCase())
                    .contains(Common.removeAccent(s.toString().toLowerCase()))) {
                tmp.add(user);
            }
        }
        view.setRecyclerSearchAfter(tmp);
    }

    @Override
    public void doUserItemClick(User user) {
        view.navigationToUserInfoActivity(user);
    }

    @Override
    public void onLoadDataUserSuccess(List<User> userList) {
        view.setRecyclerSearch(userList);
    }
}
