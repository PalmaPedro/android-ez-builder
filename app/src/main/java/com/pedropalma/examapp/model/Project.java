package com.pedropalma.examapp.model;

public class Project {

    private String id = "";
    //private String imageURL = "";
    private String title = "";
    private String startDate = "";
    private String endDate = "";
    private String location = "";


    public Project(String id, String title, String startDate, String endDate) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Project(String id, String title, String startDate, String endDate, String location) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getLocation() {
        return location;
    }

    public String getId() {
        return id;
    }

}