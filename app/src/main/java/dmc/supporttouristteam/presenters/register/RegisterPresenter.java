package dmc.supporttouristteam.presenters.register;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import dmc.supporttouristteam.R;
import dmc.supporttouristteam.models.RegisterModel;

public class RegisterPresenter implements OnRegisterListener{
    private RegisterView registerView;
    private RegisterModel registerModel;

    public RegisterPresenter(RegisterView registerView) {
        this.registerView = registerView;
        this.registerModel = new RegisterModel(this);
    }

    public void register(String name, String email, String password, String confirmPassword, Uri pickedImgUri) {
        registerView.hideRegisterButton();
        registerView.showProgress();
        registerModel.handleRegister(name, email, password, confirmPassword, pickedImgUri);
    }

    public void navigateToRegister() {
        registerView.navigateToLogin();
    }

    public void checkAndRequestForPermission(Context context, Activity activity) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                registerView.showMessage(R.string.required_permission);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, 1);
            }
        } else {
            openGallery(activity);
        }
    }

    private void openGallery(Activity activity) {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(activity);
    }

    @Override
    public void onSuccess() {
        registerView.showRegisterButton();
        registerView.hideProgress();
        registerView.navigateToLogin();
    }

    @Override
    public void onFail(int message) {
        registerView.showRegisterButton();
        registerView.hideProgress();
        registerView.showMessage(message);
    }
}
