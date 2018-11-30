package com.example.ritcabcuhi.geosocialmapv2.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Direction {
    private int duration;
    private int distance;
    private List<List<HashMap<String,String>>> path;

    public Direction(){
        path = new ArrayList<>();
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public List<List<HashMap<String, String>>> getPath() {
        return path;
    }

    public void setPath(List<List<HashMap<String, String>>> path) {
        this.path = path;
    }
}
