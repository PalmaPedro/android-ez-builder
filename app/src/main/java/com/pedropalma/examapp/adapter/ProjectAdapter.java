package com.pedropalma.examapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pedropalma.examapp.ui.ProjectDetailsActivity;
import com.pedropalma.examapp.ui.ProjectsActivity;
import com.pedropalma.examapp.R;
import com.pedropalma.examapp.model.Project;
import com.pedropalma.examapp.view.ViewHolder;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ViewHolder> {

    private ProjectsActivity projectsActivity;
    private List<Project> projects;
    Context context;

    public ProjectAdapter(ProjectsActivity projectsActivity, List<Project> projects) {
        this.projectsActivity = projectsActivity;
        this.projects = projects;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.project_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(itemView);
        //handle item clicks here
        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
            /*
            @Override
            public void onItemLongClick(View view, int position) {
                //this will be called when user clicks item
                //show data in toast on clicking
                String title = projects.get(position).getTitle();
                String startDate = projects.get(position).getStartDate();
                String endDate = projects.get(position).getEndDate();
                Toast.makeText(projectsActivity, title + "\n" + startDate + endDate, Toast.LENGTH_SHORT).show();
            }*/

            @Override
            public void onItemClick(View view, final int position) {
                //this will be called when user  clicks item
                //get data
                String id = projects.get(position).getId();
                //String imageUrl = projects.get(position).getImageURL();
                String title = projects.get(position).getTitle();
                String startDate = projects.get(position).getStartDate();
                String endDate = projects.get(position).getEndDate();
                //intent to start activity
                Intent intent = new Intent(projectsActivity, ProjectDetailsActivity.class);
                //put data in the intent
                intent.putExtra("pId", id);
                //intent.putExtra("pImageUrl", imageUrl);
                intent.putExtra("pTitle", title);
                intent.putExtra("pStartDate", startDate);
                intent.putExtra("pEndDate", endDate);
                //start activity
                projectsActivity.startActivity(intent);
            }

        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //bind views / set data
        holder.mTitleTv.setText(projects.get(position).getTitle());
        holder.mStartDateTv.setText(projects.get(position).getStartDate());
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }
}
