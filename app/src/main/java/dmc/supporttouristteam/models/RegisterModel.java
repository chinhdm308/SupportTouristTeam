package dmc.supporttouristteam.models;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import dmc.supporttouristteam.R;
import dmc.supporttouristteam.presenters.register.OnRegisterListener;
import dmc.supporttouristteam.utils.Common;

public class RegisterModel {
    private OnRegisterListener callback;

    public RegisterModel(OnRegisterListener callback) {
        this.callback = callback;
    }

    public void handleRegister(String name, String email, String password, String confirmPassword, Uri pickedImgUri) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            callback.onFail(R.string.fields_empty);

        } else {
            if (confirmPassword.equals(password)) {
                if (pickedImgUri != null) {
                    createUserAccount(email, name, password, pickedImgUri);
                } else {
                    callback.onFail(R.string.image_picked_empty);
                }
            } else {
                callback.onFail(R.string.confirm_pass_incorrect);
            }
        }
    }

    private void createUserAccount(String email, final String name, String password, Uri pickedImgUri) {
        // this method create user account with specific email and password
        Common.FB_AUTH.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser currentUser = Common.FB_AUTH.getCurrentUser();
                            updateUserInfo(name, pickedImgUri, currentUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            callback.onFail(R.string.register_failed);
                        }
                    }
                });
    }

    private void updateUserInfo(final String name, final Uri pickedImgUri, final FirebaseUser currentUser) {
        // need to upload user photo to firebase storage and get url
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child(Common.RF_PHOTOS);
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // image uploaded successfully
                // now we can get our image url
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // uri contain user image url
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();
                        currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // user info updated successfully
                                    addUserIntoDatabase(currentUser);
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void addUserIntoDatabase(FirebaseUser currentUser) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.RF_USERS).child(currentUser.getUid());
        reference.setValue(new User(currentUser.getUid(),
                currentUser.getEmail(),
                currentUser.getDisplayName(),
                currentUser.getPhotoUrl().toString()))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseAuth.getInstance().signOut();
                            callback.onSuccess(R.string.register_successful);
                        } else {
                            callback.onFail(R.string.register_failed);
                        }
                    }
                });
    }
}
