package com.pedropalma.examapp.storage;

import android.content.ContentResolver;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pedropalma.examapp.controller.ImageController;
import com.pedropalma.examapp.ui.ProjectDetailsActivity;

public class FirebaseStorageManager {

    private ImageController imageController;
    private ProjectDetailsActivity projectDetailsActivity;

    public FirebaseStorageManager(ImageController imageController, ProjectDetailsActivity projectDetailsActivity) {
        this.projectDetailsActivity = projectDetailsActivity;
        this.imageController = imageController;
    }

    // method to add extension to image files
    private String getFileExtension(Uri uri) {
        ContentResolver cR = projectDetailsActivity.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    // method to upload image to Firebase storage
    public void uploadImageFile() {
        if (imageController.imageUri != null) {
            StorageReference fileReference = projectDetailsActivity.mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageController.imageUri));
            projectDetailsActivity.mUploadTask = fileReference.putFile(imageController.imageUri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            System.out.println("upload completed");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("error " + e.getMessage());
                        }
                    });
        } else {
            Toast.makeText(projectDetailsActivity, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}
