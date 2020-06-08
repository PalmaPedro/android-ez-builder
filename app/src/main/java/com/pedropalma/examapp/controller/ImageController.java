package com.pedropalma.examapp.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import com.pedropalma.examapp.ui.ProjectDetailsActivity;
import java.io.InputStream;
import static android.app.Activity.RESULT_OK;

public class ImageController {

    private  ProjectDetailsActivity projectDetailsActivity;
    public Uri imageUri;

    public ImageController(ProjectDetailsActivity projectDetailsActivity) {
        this.projectDetailsActivity = projectDetailsActivity;
    }

    public void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(projectDetailsActivity);
        builder.setTitle("Choose your profile picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                checkImageOptions(dialog, item, options);
            }
        });
        builder.show();
    }

    private void checkImageOptions(DialogInterface dialog, int item, CharSequence[] options) {
        if (options[item].equals("Take Photo")) {
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            projectDetailsActivity.startActivityForResult(takePicture, 0);
        } else if (options[item].equals("Choose from Gallery")) {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            projectDetailsActivity.startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
        } else if (options[item].equals("Cancel")) {
            dialog.dismiss();
        }
    }

    public void getImageFromCamera(int resultCode, @Nullable Intent intent) {
        if (resultCode == RESULT_OK && intent != null) {
            Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
            //load image to ImageView
            projectDetailsActivity.mImage.setImageBitmap(bitmap);
            // convert to uri to be uploaded to storage
            String path = MediaStore.Images.Media.insertImage(projectDetailsActivity.getContentResolver(), bitmap, "title", null);
            if (path != null) {
                imageUri = Uri.parse(path);
                Log.i("URI from camera", imageUri.toString());
            }
        }
    }

    public void getImageFromGallery(int resultCode, @Nullable Intent intent) {
        if (resultCode == RESULT_OK && intent != null) {
            imageUri = intent.getData();
            Log.i("URI from gallery", imageUri.toString());
            // load image to ImageView
            try {
                InputStream is = projectDetailsActivity.getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                projectDetailsActivity.mImage.setImageBitmap(bitmap);
            } catch (Exception e) {
            }
            //Picasso.get().load(imageUri).into(mImage);
        }
    }


}
