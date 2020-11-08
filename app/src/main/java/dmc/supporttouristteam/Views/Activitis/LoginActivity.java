package dmc.supporttouristteam.Views.Activitis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;

import dmc.supporttouristteam.Presenters.Login.CommonLogin;
import dmc.supporttouristteam.Presenters.Login.LoginPresenter;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Utils.Config;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, CommonLogin.View {
    private TextInputEditText etEmail, etPassword;
    private Button buttonLogin;
    private TextView textCreateAccount;
    private ProgressBar progressBarLoadingLogin;
    private FirebaseUser currentUser = Config.FB_AUTH.getCurrentUser();
    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle(getString(R.string.login));
        mapping();
        buttonLogin.setOnClickListener(this);
        textCreateAccount.setOnClickListener(this);
        presenter = new LoginPresenter(this);
    }

    protected void onStart() {
        super.onStart();
        if (currentUser != null) {
            navigateToHome();
        }
    }

    private void mapping() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        textCreateAccount = findViewById(R.id.text_create_new_account);
        buttonLogin = findViewById(R.id.button_login);
        progressBarLoadingLogin = findViewById(R.id.progressBar_loading_login);
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
        }
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
        Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }

    @Override
    public void showMessage(int message) {
        Toast.makeText(this, getString(message), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToRegister() {
        Intent activityRegister = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(activityRegister);
    }
}
