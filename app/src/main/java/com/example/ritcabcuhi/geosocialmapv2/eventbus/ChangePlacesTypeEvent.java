package com.example.ritcabcuhi.geosocialmapv2.eventbus;

public class ChangePlacesTypeEvent {
    public static enum PLACES_TYPE{
        HEALTH,
        EDUCATION,
        MY_PLACES
    }

    private PLACES_TYPE placesType = PLACES_TYPE.MY_PLACES;

    public ChangePlacesTypeEvent(PLACES_TYPE placesType){
        this.placesType = placesType;
    }

    public PLACES_TYPE getPlacesType(){
        return placesType;
    }
}
