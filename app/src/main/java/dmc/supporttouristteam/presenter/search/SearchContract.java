package dmc.supporttouristteam.presenter.search;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import dmc.supporttouristteam.data.model.fb.User;

public interface SearchContract {
    interface View {
        void setRecyclerSearch(List<User> userList);

        void setRecyclerSearchAfter(List<User> tmp);

        void navigationToUserInfoActivity(User user);
    }

    interface Presenter {
        void doSetRecyclerSearch(DatabaseReference userRef);

        void doSearchUser(String s, List<User> userList);

        void doUserItemClick(User user);
    }

    interface Interactor {
        void readDataUsers(DatabaseReference userRef);
    }

    interface OnOperationListener {

        void onLoadDataUserSuccess(List<User> userList);
    }
}
