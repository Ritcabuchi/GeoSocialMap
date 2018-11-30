package com.example.ritcabcuhi.geosocialmapv2.api.listener;

import com.example.ritcabcuhi.geosocialmapv2.model.Place;

import java.util.HashMap;
import java.util.List;

public interface GooglePlaceApiListener {
    void onSuccess(HashMap<String,Place> placeList);
}
