package dmc.supporttouristteam.Presenters.Login;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginInteractor implements LoginContract.Interactor {

    private LoginContract.OnOperationListener listener;

    public LoginInteractor(LoginContract.OnOperationListener listener) {
        this.listener = listener;
    }

    @Override
    public void login(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            listener.onSuccess();
                        } else {
                            listener.onFail();
                        }
                    }
                });
    }
}
