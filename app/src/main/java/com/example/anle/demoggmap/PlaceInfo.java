package com.example.anle.demoggmap;

import com.google.android.gms.location.places.Place;

public class PlaceInfo {
    private String name;
    private String formatted_address;
    private String photoReference;
    private double lat;
    private double lng;

    public PlaceInfo(){

    }
    public PlaceInfo(String name, String formatted_address, String photoReference, double lat, double lng) {
        this.name = name;
        this.formatted_address = formatted_address;
        this.photoReference = photoReference;
        this.lat = lat;
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
