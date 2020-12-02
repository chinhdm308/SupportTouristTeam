package dmc.supporttouristteam.Views.Activitis;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import dmc.supporttouristteam.Presenters.Login.LoginContract;
import dmc.supporttouristteam.Presenters.Login.LoginPresenter;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Utils.Config;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, LoginContract.View {
    private static final int RC_SIGN_IN = 1;
    private TextInputEditText etEmail, etPassword;
    private Button buttonLogin;
    private TextView textCreateAccount;
    private ProgressBar progressBarLoadingLogin;
    private LoginPresenter presenter;

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle(getString(R.string.login));

        init();
    }

    private void init() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        textCreateAccount = findViewById(R.id.text_create_new_account);
        buttonLogin = findViewById(R.id.button_login);
        progressBarLoadingLogin = findViewById(R.id.progressBar_loading_login);

        buttonLogin.setOnClickListener(this);
        textCreateAccount.setOnClickListener(this);

        presenter = new LoginPresenter(this, LoginActivity.this, LoginActivity.this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set the dimensions of the sign-in button.
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);
    }

    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            navigateToHome();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_login:
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                // received handle login
                presenter.validateCredentials(email, password);
                break;
            case R.id.text_create_new_account:
                presenter.navigateToRegister();
                break;
            case R.id.sign_in_button:
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(Config.TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(Config.TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(Config.TAG, "signInWithCredential:success");
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                            updateUI(user);
                            FirebaseAuth.getInstance().signOut();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(Config.TAG, "signInWithCredential:failure", task.getException());
//                            Snackbar.make(mBinding.mainLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    public void showProgress() {
        progressBarLoadingLogin.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBarLoadingLogin.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showLoginButton() {
        buttonLogin.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoginButton() {
        buttonLogin.setVisibility(View.INVISIBLE);
    }

    @Override
    public void navigateToHome() {
        // save uid to storage to update location from background
        Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToRegister() {
        Intent activityRegister = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(activityRegister);
    }
}
