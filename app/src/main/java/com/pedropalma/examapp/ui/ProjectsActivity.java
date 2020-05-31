package com.pedropalma.examapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.pedropalma.examapp.R;
import com.pedropalma.examapp.adapter.ProjectAdapter;
import com.pedropalma.examapp.auth.MainActivity;
import com.pedropalma.examapp.model.Project;

import java.util.ArrayList;
import java.util.List;

public class ProjectsActivity extends AppCompatActivity {

    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    List<Project> projects = new ArrayList<>();
    RecyclerView recyclerView;
    //layout manager for recycler view
    RecyclerView.LayoutManager layoutManager;
    //firestore instance;
    FirebaseFirestore db;
    ProjectAdapter adapter;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        //initialize firestore
        db = FirebaseFirestore.getInstance();

        //initialize views
        recyclerView = findViewById(R.id.recycler_view);

        //set recycler view properties
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //init progress dialog
        pd = new ProgressDialog(this);

        //show projects in recycler view
        showProjects();

        //handle FloatingAction button click (go to ProjectDetailsActivity)
        FloatingActionButton add = findViewById(R.id.btnAddProject);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProjectsActivity.this, ProjectDetailsActivity.class));
                finish();
            }
        });


        //handle FloatingAction button click (logout)
        FloatingActionButton logout = findViewById(R.id.btnLogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // logs out and forwards user to Main Activity
                FirebaseAuth.getInstance().signOut();
                // when user signs out gets re-directed to main activity
                Intent intent = new Intent(ProjectsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showProjects() {
        //set title of progress dialog
        pd.setTitle("Loading Data...");
        //show progress dialog
        pd.show();
        db.collection("projects")  // firestore collection name
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //called when data is retrieved
                        pd.dismiss();
                        //show projects
                        for (DocumentSnapshot doc : task.getResult()) {
                            Project project = new Project(doc.getString("id"),
                                    //get image
                                    doc.getString("imageUrl"),
                                    doc.getString("title"),
                                    doc.getString("start date"),
                                    doc.getString("end date"));
                                    //doc.getString("location"));
                            projects.add(project);
                        }
                        //adapter
                        adapter = new ProjectAdapter(ProjectsActivity.this, projects);
                        //set adapter to recycler view
                        recyclerView.setAdapter(adapter);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //called when there is any error while retrieving
                        pd.dismiss();
                        Toast.makeText(ProjectsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
