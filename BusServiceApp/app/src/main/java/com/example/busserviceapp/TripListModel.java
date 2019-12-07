package com.example.busserviceapp;

public class TripListModel {

    String startAdd;
    String endAdd,rating;
    String date_time,busname,drivername;
    String driverPictureUrl;

    public TripListModel() {
    }

    public TripListModel(String startAdd, String endAdd, String rating, String date_time, String busname, String drivername, String driverPictureUrl) {
        this.startAdd = startAdd;
        this.endAdd = endAdd;
        this.rating = rating;
        this.date_time = date_time;
        this.busname = busname;
        this.drivername = drivername;
        this.driverPictureUrl = driverPictureUrl;
    }

    public String getStartAdd() {
        return startAdd;
    }

    public void setStartAdd(String startAdd) {
        this.startAdd = startAdd;
    }

    public String getEndAdd() {
        return endAdd;
    }

    public void setEndAdd(String endAdd) {
        this.endAdd = endAdd;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getBusname() {
        return busname;
    }

    public void setBusname(String busname) {
        this.busname = busname;
    }

    public String getDrivername() {
        return drivername;
    }

    public void setDrivername(String drivername) {
        this.drivername = drivername;
    }

    public String getDriverPictureUrl() {
        return driverPictureUrl;
    }

    public void setDriverPictureUrl(String driverPictureUrl) {
        this.driverPictureUrl = driverPictureUrl;
    }
}
