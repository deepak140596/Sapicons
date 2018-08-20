package com.sapicons.deepak.tbd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    TextView nameTv, phoneTv,emailTv;
    CircleImageView picIv;
    FirebaseUser user;
    StorageReference storageReference;

    Uri userPicUri;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        setTitle("Profile");

        initialiseViews();
        getUserAccount();
        setViews();
    }

    private void initialiseViews(){
        nameTv=findViewById(R.id.user_profile_name_tv);
        phoneTv=findViewById(R.id.user_profile_phone_tv);
        emailTv=findViewById(R.id.user_profile_email_tv);
        picIv=findViewById(R.id.user_profile_pic_iv);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Updating Profile Pic ...");

    }

    private void getUserAccount(){
        user= FirebaseAuth.getInstance().getCurrentUser();
    }

    private void setViews(){
        nameTv.setText(user.getDisplayName());
        emailTv.setText(user.getEmail());

        Log.d("USER","picUrl: "+user.getPhotoUrl());
        Log.d("USER","phoneN: "+user.getPhoneNumber());

        if(user.getPhotoUrl()!=null){
            Glide.with(this).load(user.getPhotoUrl()).into(picIv);
        }

        picIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(UserProfile.this);


            }
        });
    }

    private void uploadUserProfilePicToDB(){
        progressDialog.show();
        storageReference= FirebaseStorage.getInstance().getReference().child("users_profile_pic")
                .child(user.getEmail());
        if(userPicUri!=null) {
            storageReference = storageReference.child(userPicUri.getLastPathSegment());
            UploadTask task = storageReference.putFile(userPicUri);

            Task<Uri> urlTask = task.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.d("PIC_URL","url: "+downloadUri);
                        updateUserProfile(downloadUri);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }

    }

    private void updateUserProfile(Uri picUri){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(picUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("USER", "User profile updated.");
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                userPicUri = result.getUri();
                Glide.with(this).load(userPicUri).into(picIv);
                Log.d("URI","Result URI: "+userPicUri.toString());

                uploadUserProfilePicToDB();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}
