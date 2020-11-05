package dmc.supporttouristteam.views.activitis;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;

import dmc.supporttouristteam.R;
import dmc.supporttouristteam.presenters.login.LoginPresenter;
import dmc.supporttouristteam.presenters.login.LoginView;
import dmc.supporttouristteam.services.CommonService;
import dmc.supporttouristteam.utils.Common;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, LoginView {
    private TextInputEditText etEmail, etPassword;
    private Button buttonLogin;
    private TextView textCreateAccount;
    private ProgressBar progressBarLoadingLogin;
    private FirebaseUser currentUser = Common.FB_AUTH.getCurrentUser();
    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle(getString(R.string.login));
        mapping();
        buttonLogin.setOnClickListener(this);
        textCreateAccount.setOnClickListener(this);
        loginPresenter = new LoginPresenter(this);
    }

    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                CommonService.showMessage(LoginActivity.this, getString(R.string.required_permission));
                finish();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, 1);
            }
        } else {
            if (currentUser != null) {
                navigateToHome();
            }
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
                loginPresenter.login(email, password);
                break;
            case R.id.text_create_new_account:
                loginPresenter.navigateToRegister();
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
