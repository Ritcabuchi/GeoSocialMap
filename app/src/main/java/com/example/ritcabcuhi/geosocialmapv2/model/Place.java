package com.example.ritcabcuhi.geosocialmapv2.model;

import com.google.android.gms.maps.model.LatLng;

public class Place {

    private String address;
    private String imageUrl;
    private Double latitude;
    private Double longitude;

    public Place(){

    }

    public Place(String address, String imageUrl, Double latitude, Double longitude) {
        this.address = address;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LatLng getLatLng(){
        return new LatLng(latitude,longitude);
    }
}
