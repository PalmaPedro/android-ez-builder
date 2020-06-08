package com.pedropalma.examapp.controller;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;

import com.pedropalma.examapp.R;
import com.pedropalma.examapp.ui.ProjectDetailsActivity;

import java.util.Calendar;

public class DateController {

    private ProjectDetailsActivity projectDetailsActivity;
    public static DatePickerDialog picker;

    public DateController(ProjectDetailsActivity projectDetailsActivity) {
        this.projectDetailsActivity= projectDetailsActivity;
    }

    // method to set project start date
    public void pickStartDate() {
        projectDetailsActivity.mStartDate = projectDetailsActivity.findViewById(R.id.etStartDate);
        projectDetailsActivity.mStartDate.setInputType(InputType.TYPE_NULL);
        projectDetailsActivity.mStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(projectDetailsActivity,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                projectDetailsActivity.mStartDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });
    }

    // method to set project end date
    public void pickEndDate() {
        projectDetailsActivity.mEndDate = projectDetailsActivity.findViewById(R.id.etEndDate);
        projectDetailsActivity.mEndDate.setInputType(InputType.TYPE_NULL);
        projectDetailsActivity.mEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(projectDetailsActivity,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                projectDetailsActivity.mEndDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });
    }


}
