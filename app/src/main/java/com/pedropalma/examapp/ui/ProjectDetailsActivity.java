package com.pedropalma.examapp.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.pedropalma.examapp.R;
import com.pedropalma.examapp.controller.DateController;
import com.pedropalma.examapp.controller.ImageController;
import com.pedropalma.examapp.storage.FirebaseStorageManager;
import com.pedropalma.examapp.storage.FirebasedbManager;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProjectDetailsActivity extends AppCompatActivity {

    //views
    public ImageView mImage;
    public EditText mTitle, mStartDate, mEndDate, mLocation;
    public Button mSaveOrUpdateBtn, mDeleteBtn;
    public String pId, pImage, pTitle, pStartDate, pEndDate, pLocation;

    public StorageReference mStorageRef;
    public StorageTask mUploadTask;
    private ProgressBar mProgressBar;
    //instances
    private DateController dateController;
    private ImageController imageController;
    private FirebasedbManager firebasedbManager;
    private FirebaseStorageManager firebaseStorageManager;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        dateController = new DateController(this);
        imageController = new ImageController(this);
        firebasedbManager = new FirebasedbManager(this);
        firebaseStorageManager = new FirebaseStorageManager(imageController, this);

        //initialize views
        buildViews();
        saveOrUpdateCheck();

        firebasedbManager.pd = new ProgressDialog(this);

        //firestore database and storage
        firebasedbManager.db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        btnSaveOrUpdate();
        btnDelete();
    }

    private void buildViews() {
        mImage = findViewById(R.id.ivProjectLogo);
        // handle click image event
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageController.selectImage();
            }
        });

        mTitle = findViewById(R.id.etTitle);
        dateController.pickStartDate();
        dateController.pickEndDate();
        mLocation = findViewById(R.id.etLocation);
        mSaveOrUpdateBtn = findViewById(R.id.btn_save);
        mDeleteBtn = findViewById(R.id.btn_delete);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:  // take photo
                    imageController.getImageFromCamera(resultCode, intent);
                    break;
                case 1: // choose from gallery
                    imageController.getImageFromGallery(resultCode, intent);
                    break;
            }
        }
    }

    private void saveOrUpdateCheck() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mSaveOrUpdateBtn.setText("Update");
            //get data
            pId = bundle.getString("pId");
            pImage = bundle.getString("pImage");
            pTitle = bundle.getString("pTitle");
            pStartDate = bundle.getString("pStartDate");
            pEndDate = bundle.getString("pEndDate");
            pLocation = bundle.getString("pLocation");
            //set data
            mImage.setImageURI(imageController.imageUri);
            mTitle.setText(pTitle);
            mStartDate.setText(pStartDate);
            mEndDate.setText(pEndDate);
            mLocation.setText(pLocation);
        } else {
            mSaveOrUpdateBtn.setText("Save");
        }
    }

    private void btnDelete() {
        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = pId;
                firebasedbManager.deleteProject(id);
            }
        });
    }

    private void btnSaveOrUpdate() {
        mSaveOrUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getIntent().getExtras();
                if (bundle != null) {
                    //updating
                    //upload data to Fireabase db
                    String id = pId;
                    String image = mImage.getDrawable().toString();
                    String title = mTitle.getText().toString().trim();
                    String startDate = mStartDate.getText().toString().trim();
                    String endDate = mEndDate.getText().toString().trim();
                    String location = mLocation.getText().toString().trim();
                    //function call to update data
                    firebasedbManager.updateProject(id, image, title, startDate, endDate, location);
                    // upload image to Fireabase storage
                    firebaseStorageManager.uploadImageFile();

                } else {
                    //adding new
                    //input data
                    String image = mImage.getDrawable().toString();
                    String title = mTitle.getText().toString().trim();
                    String startDate = mStartDate.getText().toString().trim();
                    String endDate = mEndDate.getText().toString().trim();
                    String location = mLocation.getText().toString().trim();
                    // function call to add note
                    firebasedbManager.addProject(image, title, startDate, endDate, location);
                    // upload image to Fireabase storage
                    firebaseStorageManager.uploadImageFile();
                }
            }
        });
    }



    /*
    private void downloadImageFile() {
    }*/



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ProjectDetailsActivity.this, ProjectsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}





