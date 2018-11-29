package com.example.ritcabcuhi.geosocialmapv2.model;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

public class Place {

    private String id;
    private String address;
    private String imageUrl;
    private String imageUri;
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

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getId(){
        if(id!=null) return id;
        if(latitude != null && longitude != null){
            String s1 = String.valueOf(latitude).replace('.','p');
            String s2 = String.valueOf(longitude).replace('.','p');
            id = s1 + "_" + s2;
        }
        return id;
    }

}
