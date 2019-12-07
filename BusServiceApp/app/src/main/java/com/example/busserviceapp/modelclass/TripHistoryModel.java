package com.example.busserviceapp.modelclass;

public class TripHistoryModel {

    String startAddress,DestinationAddress,date_time,busName,rating, driverPhotoUrl, driverName;

    public TripHistoryModel() {
    }

    public TripHistoryModel(String startAddress, String destinationAddress, String date_time, String busName, String rating, String driverPhotoUrl, String driverName) {
        this.startAddress = startAddress;
        DestinationAddress = destinationAddress;
        this.date_time = date_time;
        this.busName = busName;
        this.rating = rating;
        this.driverPhotoUrl = driverPhotoUrl;
        this.driverName = driverName;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getDestinationAddress() {
        return DestinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        DestinationAddress = destinationAddress;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDriverPhotoUrl() {
        return driverPhotoUrl;
    }

    public void setDriverPhotoUrl(String driverPhotoUrl) {
        this.driverPhotoUrl = driverPhotoUrl;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
}
