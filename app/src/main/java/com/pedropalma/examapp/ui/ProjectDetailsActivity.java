package com.pedropalma.examapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pedropalma.examapp.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProjectDetailsActivity extends AppCompatActivity {

    //views
    private ImageView mImage;
    private EditText mTitle, mStartDate, mEndDate;
    private DatePickerDialog picker;
    private TextView mLocation;
    private Button mSaveBtn, mDeleteBtn;

    // progress dialog
    ProgressDialog pd;

    //firestore instance
    private FirebaseFirestore db;

    private String pId, pTitle, pStartDate, pEndDate, pLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        //initialize views
        // imageView project logo
        //EditTextView project title
        mTitle = findViewById(R.id.etTitle);
        //set start project date
        pickStartDate();
        //set end project date
        pickEndDate();
        //GoogleMapView
        //TextView location fetched from Google Map marker
        //Button save/update
        mSaveBtn = findViewById(R.id.btn_save);
        //Button delete
        mDeleteBtn = findViewById(R.id.btn_delete);

        //evaluate if its a 'add new project' or 'update existing one'
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mSaveBtn.setText("Update");
            //get data
            pId = bundle.getString("pId");
            // logo image here
            pTitle = bundle.getString("pTitle");
            pStartDate = bundle.getString("pStartDate");
            pEndDate = bundle.getString("pEndDate");
            //pLocation = bundle.getString("pLocation");
            //set data
            // set logo image here
            mTitle.setText(pTitle);
            mStartDate.setText(pStartDate);
            mEndDate.setText(pEndDate);
            //mLocation.setText(pLocation);
        } else {
            mSaveBtn.setText("Save");
        }

        //progress dialog
        pd = new ProgressDialog(this);

        //firestore instance
        db = FirebaseFirestore.getInstance();

        //handle button click to save/update project into firestore database
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getIntent().getExtras();
                if (bundle != null) {
                    //updating
                    //input data
                    String id = pId;
                    String title = mTitle.getText().toString().trim();
                    String startDate = mStartDate.getText().toString().trim();
                    String endDate = mEndDate.getText().toString().trim();
                    //String location = mLocation.getText().toString().trim();
                    //function call to update data
                    updateProject(id, title, startDate, endDate);

                } else {
                    //adding new
                    //input data
                    String title = mTitle.getText().toString().trim();
                    String startDate = mStartDate.getText().toString().trim();
                    String endDate = mEndDate.getText().toString().trim();
                    //String location = mLocation.getText().toString().trim();
                    // function call to add note
                    addProject(title, startDate, endDate);
                }
            }
        });


        //handle button click to delete project from firestore database
        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //deleting
                String id = pId;
                //String title = mTitle.getText().toString().trim();
                //String startDate = mStartDate.getText().toString().trim();
                //String endDate = mStartDate.getText().toString().trim();
                //function call to delete note
                deleteProject(id);
            }
        });

    }

    private void addProject(String title, String startDate, String endDate) {
        //set title of progress bar
        pd.setTitle("Adding project to Firestore");
        //show progress bar when user clicks save button
        pd.show();
        //random id for each note to be stored in Firestore
        String id = UUID.randomUUID().toString();

        Map<String, Object> map = new HashMap<>();
        map.put("id", id); // generated id for note
        map.put("title", title);
        map.put("start date", startDate);
        map.put("end date", endDate);


        //add note
        db.collection("projects").document(id).set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //called when note is added successfully
                        pd.dismiss();
                        //get and show error message
                        Toast.makeText(ProjectDetailsActivity.this, "Uploaded...", Toast.LENGTH_SHORT).show();
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

    private void updateProject(String id, String title, String startDate, String endDate) {
        //set title of progress bar
        pd.setTitle("Updating note...");
        //show progress bar when user clicks save button
        pd.show();
        db.collection("projects").document(id)
                .update("title", title, "start date", startDate, "end date",endDate )
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //called when updated successfully
                        pd.dismiss();
                        Toast.makeText(ProjectDetailsActivity.this, "Updated...", Toast.LENGTH_SHORT).show();

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
        super.onBackPressed();
        Intent intent = new Intent(ProjectDetailsActivity.this, ProjectsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}