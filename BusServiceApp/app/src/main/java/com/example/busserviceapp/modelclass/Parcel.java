package com.example.busserviceapp.modelclass;

import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Parcel implements Parcelable {
    LatLng start,destination,busLatlng;
    String key,busName,placeName,username;

    public Parcel() {
    }

    public Parcel(LatLng start, LatLng destination, LatLng busLatlng, String key, String busName, String placeName, String username) {

        this.start = start;
        this.destination = destination;
        this.busLatlng = busLatlng;
        this.key = key;
        this.busName = busName;
        this.placeName = placeName;
        this.username = username;
    }

    protected Parcel(android.os.Parcel in) {
        start = in.readParcelable(LatLng.class.getClassLoader());
        destination = in.readParcelable(LatLng.class.getClassLoader());
        busLatlng = in.readParcelable(LatLng.class.getClassLoader());
        key = in.readString();
        busName = in.readString();
        placeName = in.readString();
        username = in.readString();
    }

    public static final Creator<Parcel> CREATOR = new Creator<Parcel>() {
        @Override
        public Parcel createFromParcel(android.os.Parcel in) {
            return new Parcel(in);
        }

        @Override
        public Parcel[] newArray(int size) {
            return new Parcel[size];
        }
    };

    public LatLng getStart() {
        return start;
    }

    public void setStart(LatLng start) {
        this.start = start;
    }

    public LatLng getDestination() {
        return destination;
    }

    public void setDestination(LatLng destination) {
        this.destination = destination;
    }

    public LatLng getBusLatlng() {
        return busLatlng;
    }

    public void setBusLatlng(LatLng busLatlng) {
        this.busLatlng = busLatlng;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel parcel, int i) {
        parcel.writeParcelable(start, i);
        parcel.writeParcelable(destination, i);
        parcel.writeParcelable(busLatlng, i);
        parcel.writeString(key);
        parcel.writeString(busName);
        parcel.writeString(placeName);
        parcel.writeString(username);
    }
}
