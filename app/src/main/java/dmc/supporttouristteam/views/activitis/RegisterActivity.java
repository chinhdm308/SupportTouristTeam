package dmc.supporttouristteam.views.activitis;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.presenters.register.RegisterPresenter;
import dmc.supporttouristteam.presenters.register.RegisterView;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, RegisterView {
    private CircleImageView imageUser;
    private TextInputEditText etDisplayName, etEmail, etPassword, etConfirmPassword;
    private ProgressBar loadingRegisterProgress;
    private Button buttonRegister;
    private TextView textReadyToLogin;

    private Uri pickedImgUri = null;

    private RegisterPresenter registerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle(getString(R.string.register));
        mapping();
        registerPresenter = new RegisterPresenter(this);
        imageUser.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
        textReadyToLogin.setOnClickListener(this);
    }

    private void mapping() {
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
                registerPresenter.register(name, email, password, confirmPass, pickedImgUri);
                break;
            case R.id.text_ready_to_login:
                registerPresenter.navigateToRegister();
                break;
            case R.id.image_user:
                registerPresenter.checkAndRequestForPermission();
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

    @Override
    public void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showMessage(R.string.required_permission);
            } else {
                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, 1);
            }
        } else {
            openGallery();
        }
    }

    @Override
    public void openGallery() {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(RegisterActivity.this);
    }
}