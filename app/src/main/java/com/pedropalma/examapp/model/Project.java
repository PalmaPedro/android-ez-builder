package com.pedropalma.examapp.model;

public class Project {

    private String id = "";
    private String imageURL = "";
    private String title = "";
    private String startDate = "";
    private String endDate = "";
    //private String location = "";


    public Project() {
        // empty constructor is needed
    }

    public Project(String id, String imageUrl, String title, String startDate, String endDate) {
        this.id = id;
        this.imageURL = imageUrl;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }


    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
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

    /*
    public String getLocation() {
        return location;
    }*/

    public String getId() {
        return id;
    }

}