package dmc.supporttouristteam.Views.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;
import dmc.supporttouristteam.R;
import dmc.supporttouristteam.Service.TrackerService;
import dmc.supporttouristteam.Utils.Common;
import dmc.supporttouristteam.Utils.Config;
import dmc.supporttouristteam.Views.Activitis.LoginActivity;

public class AccountFragment extends Fragment implements View.OnClickListener {

    private CircleImageView userPhoto;
    private TextView textEmail, textUsername;
    private Button buttonLogout;
    private ImageView imageEditAvatar;

    private FirebaseUser currentUser;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
    }

    private void init() {
        userPhoto = getView().findViewById(R.id.image_user);
        textUsername = getView().findViewById(R.id.text_name);
        textEmail = getView().findViewById(R.id.text_email);
        buttonLogout = getView().findViewById(R.id.button_logout);
        imageEditAvatar = getView().findViewById(R.id.image_edit_avatar);

        buttonLogout.setOnClickListener(this);
        imageEditAvatar.setOnClickListener(this);
        textUsername.setOnClickListener(this);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Glide.with(getContext()).load(currentUser.getPhotoUrl()).into(userPhoto);
        textUsername.setText(currentUser.getDisplayName());
        textEmail.setText(currentUser.getEmail());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_logout:
                showAlertDialogLogout();
                break;
            case R.id.image_edit_avatar:
                checkAndRequestForPermission();
                break;
            case R.id.text_name:
                showAlertDialogEditDisplayNameAccount();
                break;
        }
    }

    private void showAlertDialogEditDisplayNameAccount() {
        ViewGroup viewGroup = getView().findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_edit_account, viewGroup, false);

        EditText etext = dialogView.findViewById(R.id.etextInfo);
        TextView text = dialogView.findViewById(R.id.text_request_edit);
        text.setText("Chỉnh sửa tên hiển thị");

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String getText = etext.getText().toString();
                // uri contain user image url
                UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                        .setDisplayName(getText)
                        .build();
                currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // user info updated successfully
                            FirebaseDatabase.getInstance().getReference(Config.RF_USERS)
                                    .child(currentUser.getUid())
                                    .child("displayName")
                                    .setValue(getText)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Thay đổi thông tin thành công", Toast.LENGTH_SHORT).show();
                                                textUsername.setText(getText);
                                                dialog.dismiss();
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
        });
        alertDialog.show();
    }

    private void showAlertDialogLogout() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có muốn đăng xuất không ?")
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getContext().stopService(new Intent(getContext(), TrackerService.class));
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        onDestroy();
                    }
                });
        dialogBuilder.show();
    }

    public void checkAndRequestForPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // show explanation
                Common.showExplanation(getContext(), getActivity(), "", "Bạn cần cấp quyền cho ứng dụng để có thể đăng ký tài khoản",
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Config.MY_REQUEST_CODE);
            } else {
                Common.requestPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE, Config.MY_REQUEST_CODE);
            }
        } else {
            openGallery();
        }
    }

    public void openGallery() {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(getActivity());
    }

    private void updateAvatar(Uri pickedImgUri) {
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(currentUser.getPhotoUrl().toString());
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.d(Config.TAG, "onSuccess: deleted file");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.d(Config.TAG, "onFailure: did not delete file");
            }
        });


        // need to upload user photo to firebase storage and get url
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child(Config.RF_USER_PHOTOS);
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
                                .setPhotoUri(uri)
                                .build();
                        currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // user info updated successfully
                                    FirebaseDatabase.getInstance().getReference(Config.RF_USERS)
                                            .child(currentUser.getUid())
                                            .child("profileImg").setValue(uri.toString());
                                    Toast.makeText(getContext(), "Thay đổi ảnh đại diện thành công", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    public void onSelectedImage(Uri value) {
        if (value != null) {
            updateAvatar(value);
        } else {
            Glide.with(getContext()).load(currentUser.getPhotoUrl()).into(userPhoto);
        }
    }

    // set image avatat temporary
    public void onSetAvatarTemp(Uri value) {
        if (value != null) {
            userPhoto.setImageURI(value);
        }
    }
}