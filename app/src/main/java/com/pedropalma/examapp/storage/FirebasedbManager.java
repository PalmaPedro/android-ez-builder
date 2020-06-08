package com.pedropalma.examapp.storage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pedropalma.examapp.ui.ProjectDetailsActivity;
import com.pedropalma.examapp.ui.ProjectsActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FirebasedbManager {

    private ProjectDetailsActivity projectDetailsActivity;
    public ProgressDialog pd;
    public FirebaseFirestore db;

    public FirebasedbManager(ProjectDetailsActivity projectDetailsActivity) {
        this.projectDetailsActivity = projectDetailsActivity;
    }

    public void addProject(String image, String title, String startDate, String endDate, String location) {
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
        map.put("location", location);
        //add project
        db.collection("projects").document(id).set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //called when note is added successfully
                        pd.dismiss();
                        //get and show error message
                        Toast.makeText(projectDetailsActivity, "Uploaded...", Toast.LENGTH_SHORT).show();
                        projectDetailsActivity.onBackPressed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //added if error occurs
                        Toast.makeText(projectDetailsActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void updateProject(String id, String image, String title, String startDate, String endDate, String location) {
        //set title of progress bar
        pd.setTitle("Updating project...");
        //show progress bar when user clicks save button
        pd.show();
        db.collection("projects").document(id)
                .update("image", image, "title", title, "start date", startDate, "end date", endDate, "location", location)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //called when updated successfully
                        pd.dismiss();
                        Toast.makeText(projectDetailsActivity, "Updated...", Toast.LENGTH_SHORT).show();
                        projectDetailsActivity.onBackPressed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //called when there is an error
                        pd.dismiss();
                        //get and show error message
                        Toast.makeText(projectDetailsActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void deleteProject(String id) {
        //set title of progress bar
        pd.setTitle("Deleting project...");
        //show progress bar when user clicks save button
        pd.show();
        db.collection("projects").document(id)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //called when updated successfully
                        pd.dismiss();
                        Toast.makeText(projectDetailsActivity, "Deleted...", Toast.LENGTH_SHORT).show();
                        projectDetailsActivity.onBackPressed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //called when there is an error
                        pd.dismiss();
                        //get and show error message
                        Toast.makeText(projectDetailsActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



}
