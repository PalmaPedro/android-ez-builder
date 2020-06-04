package com.pedropalma.examapp.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import android.text.InputType;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.pedropalma.examapp.R;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ProjectDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    //views
    public ImageView mImage;
    private EditText mTitle, mStartDate, mEndDate;
    private DatePickerDialog picker;
    private TextView mLocation;
    private Button mSaveBtn, mDeleteBtn;

    // progress dialog
    ProgressDialog pd;

    //Firestore db instance
    FirebaseFirestore db;

    //Firestore storage variables
    private StorageReference mStorageRef;

    private Uri imageUri;

    public StorageTask mUploadTask;

    private ProgressBar mProgressBar;

    public String pId, pImage, pTitle, pStartDate, pEndDate, pLocation;

    private MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        //initialize views
        buildViews();

        //evaluate if its a new project or an existing one
        saveOrUpdateCheck();

        //progress dialog
        pd = new ProgressDialog(this);

        //firestore db instance
        db = FirebaseFirestore.getInstance();

        //firestore storage instance
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        //handle button click to save/update project into firestore database
        btnSaveOrUpdate();

        //handle button click to delete project from firestore database
        btnDelete();

    }

    private void buildViews() {
        // PROJECT LOGO IMAGE - ImageView
        mImage = findViewById(R.id.ivProjectLogo);
        // handle click image event
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(ProjectDetailsActivity.this);
            }
        });
        // PROJECT TITLE - EditTextView
        mTitle = findViewById(R.id.etTitle);
        // PROJECT START DATE
        pickStartDate();
        // PROJECT ESTIMATED END DATE
        pickEndDate();
        // GOOGLE MAP VIEW
        //initGoogleMapView(){}
        // PROJECT LOCATION - TextView
        // BUTTON SAVE / UPDATE
        mSaveBtn = findViewById(R.id.btn_save);
        // BUTTON DELETE
        mDeleteBtn = findViewById(R.id.btn_delete);
    }

    private void selectImage(ProjectDetailsActivity projectDetailsActivity) {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(projectDetailsActivity);
        builder.setTitle("Choose your profile picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:  // take photo
                    if (resultCode == RESULT_OK && intent != null) {
                        Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
                        //load image to ImageView
                        mImage.setImageBitmap(bitmap);
                        // convert to uri to be uploaded to storage
                        String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap,"title",null);
                        if (path!=null) {
                            imageUri = Uri.parse(path);
                        }
                    }
                    break;
                case 1: // choose from gallery
                    if (resultCode == RESULT_OK && intent != null) {
                        imageUri = intent.getData();
                        // load image to ImageView
                        try {
                            InputStream is = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            mImage.setImageBitmap(bitmap);
                        } catch (Exception e) {
                        }
                        //Picasso.get().load(imageUri).into(mImage);
                    }
                    break;
            }
        }
    }

    private void saveOrUpdateCheck() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mSaveBtn.setText("Update");
            //get data
            pId = bundle.getString("pId");
            pImage = bundle.getString("pImage");
            pTitle = bundle.getString("pTitle");
            pStartDate = bundle.getString("pStartDate");
            pEndDate = bundle.getString("pEndDate");
            //pLocation = bundle.getString("pLocation");
            //set data
            mImage.setImageURI(imageUri);
            mTitle.setText(pTitle);
            mStartDate.setText(pStartDate);
            mEndDate.setText(pEndDate);
            //mLocation.setText(pLocation);
        } else {
            mSaveBtn.setText("Save");
        }
    }

    private void btnDelete() {
        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //deleting
                String id = pId;
                //function call to delete note
                deleteProject(id);
            }
        });
    }

    private void btnSaveOrUpdate() {
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
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
                    //String location = mLocation.getText().toString().trim();
                    //function call to update data
                    updateProject(id, image, title, startDate, endDate);
                    // upload image to Fireabase storage
                    uploadFile();

                } else {
                    //adding new
                    //input data
                    String image = mImage.getDrawable().toString();
                    String title = mTitle.getText().toString().trim();
                    String startDate = mStartDate.getText().toString().trim();
                    String endDate = mEndDate.getText().toString().trim();
                    //String location = mLocation.getText().toString().trim();
                    // function call to add note
                    addProject(image, title, startDate, endDate);
                    // upload image to Fireabase storage
                    uploadFile();
                }
            }
        });
    }

    // method to add extension to image files
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    // method to upload image to Firebase storage
    private void uploadFile() {
        if (imageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));
            mUploadTask = fileReference.putFile(imageUri)
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
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadFile() {
    }

    private void addProject(String image, String title, String startDate, String endDate) {
        //set title of progress bar
        pd.setTitle("Adding project to Firestore");
        //show progress bar when user clicks save button
        pd.show();
        //random id for each note to be stored in Firestore
        String id = UUID.randomUUID().toString();
        Map<String, Object> map = new HashMap<>();
        map.put("id", id); // generated id for note
        map.put("image", image);
        map.put("title", title);
        map.put("start date", startDate);
        map.put("end date", endDate);
        //add project
        db.collection("projects").document(id).set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //called when note is added successfully
                        pd.dismiss();
                        //get and show error message
                        Toast.makeText(ProjectDetailsActivity.this, "Uploaded...", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //added if error occurs
                        Toast.makeText(ProjectDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateProject(String id, String image, String title, String startDate, String endDate) {
        //set title of progress bar
        pd.setTitle("Updating note...");
        //show progress bar when user clicks save button
        pd.show();
        db.collection("projects").document(id)
                .update("image", image, "title", title, "start date", startDate, "end date", endDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //called when updated successfully
                        pd.dismiss();
                        Toast.makeText(ProjectDetailsActivity.this, "Updated...", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //called when there is an error
                        pd.dismiss();
                        //get and show error message
                        Toast.makeText(ProjectDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteProject(String id) {
        //set title of progress bar
        pd.setTitle("Deleting note...");
        //show progress bar when user clicks save button
        pd.show();
        db.collection("projects").document(id)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //called when updated successfully
                        pd.dismiss();
                        Toast.makeText(ProjectDetailsActivity.this, "Deleted...", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //called when there is an error
                        pd.dismiss();
                        //get and show error message
                        Toast.makeText(ProjectDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // method to set project start date
    public void pickStartDate() {
        mStartDate = findViewById(R.id.etStartDate);
        mStartDate.setInputType(InputType.TYPE_NULL);
        mStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(ProjectDetailsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                mStartDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });
    }

    // method to set project end date
    public void pickEndDate() {
        mEndDate = findViewById(R.id.etEndDate);
        mEndDate.setInputType(InputType.TYPE_NULL);
        mEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(ProjectDetailsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                mEndDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });
    }

    // redirect to projects list if back button is pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ProjectDetailsActivity.this, ProjectsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }
}





