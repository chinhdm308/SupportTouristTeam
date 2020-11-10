package dmc.supporttouristteam.Views.Activitis;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;
import dmc.supporttouristteam.Presenters.Register.CommonRegister;
import dmc.supporttouristteam.Presenters.Register.RegisterPresenter;
import dmc.supporttouristteam.R;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, CommonRegister.View {
    private CircleImageView imageUser;
    private TextInputEditText etDisplayName, etEmail, etPassword, etConfirmPassword;
    private ProgressBar loadingRegisterProgress;
    private Button buttonRegister;
    private TextView textReadyToLogin;

    private Uri pickedImgUri = null;

    private RegisterPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle(getString(R.string.register));
        init();
        presenter = new RegisterPresenter(this);
        imageUser.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
        textReadyToLogin.setOnClickListener(this);
    }

    private void init() {
        imageUser = findViewById(R.id.image_user);
        etDisplayName = findViewById(R.id.et_display_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        loadingRegisterProgress = findViewById(R.id.progressBar_loading_register);
        buttonRegister = findViewById(R.id.button_register);
        textReadyToLogin = findViewById(R.id.text_ready_to_login);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                pickedImgUri = result.getUri();
                imageUser.setImageURI(pickedImgUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_register:
                final String name = etDisplayName.getText().toString();
                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();
                final String confirmPass = etConfirmPassword.getText().toString();
                // received handle register
                presenter.validateCredentials(name, email, password, confirmPass, pickedImgUri);
                break;
            case R.id.text_ready_to_login:
                presenter.navigateToLogin();
                break;
            case R.id.image_user:
                presenter.checkAndRequestForPermission(this, this);
                break;
        }
    }

    @Override
    public void showProgress() {
        loadingRegisterProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        loadingRegisterProgress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showRegisterButton() {
        buttonRegister.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRegisterButton() {
        buttonRegister.setVisibility(View.INVISIBLE);
    }

    @Override
    public void navigateToLogin() {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void showMessage(int message) {
        Toast.makeText(this, getString(message), Toast.LENGTH_SHORT).show();
    }
}