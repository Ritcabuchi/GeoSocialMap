package com.example.ritcabcuhi.geosocialmapv2.utils;

import android.util.Log;

import com.example.ritcabcuhi.geosocialmapv2.model.Direction;
import com.example.ritcabcuhi.geosocialmapv2.model.Place;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JSONParserUtils {

    public Direction parseDirection(JSONObject jObject) {

        Direction data = new Direction();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {
            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                    int distance = (int)(((JSONObject) jLegs.get(j)).getJSONObject("distance")).get("value");
                    int duration = (int)(((JSONObject)jLegs.get(j)).getJSONObject("duration")).get("value");
                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                            hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                            path.add(hm);
                        }
                    }
                    data.setDuration(duration);
                    data.setDistance(distance);
                    data.getPath().add(path);
                }
            }

        } catch (JSONException e) {
            Log.d("", "parseDirection: distance : error");
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("", "parseDirection: distance : error");
        }

        return data;
    }

    public HashMap<String,Place> parsePlace(JSONObject jObject) {

        HashMap<String,Place> placeList = new HashMap<>();
        JSONArray result = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        Place place = null;

        try {
            result = jObject.getJSONArray("results");

            for (int i = 0; i < result.length(); i++) {
                try{
                    place = new Place();

                    String name = ((JSONObject)result.get(i)).getString("name");
                    place.setName(name);

                    String address = ((JSONObject)result.get(i)).getString("vicinity");
                    place.setAddress(address);

                    double latitude = ((JSONObject)((JSONObject)((JSONObject)result.get(i)).getJSONObject("geometry")).getJSONObject("location")).getDouble("lat");
                    place.setLatitude(latitude);

                    double longitude = ((JSONObject)((JSONObject)((JSONObject)result.get(i)).getJSONObject("geometry")).getJSONObject("location")).getDouble("lng");
                    place.setLongitude(longitude);

                    String photoReference  = ((JSONObject)((JSONArray)((JSONObject)result.get(i)).getJSONArray("photos")).get(0)).getString("photo_reference");
                    place.setPhotoReference(photoReference);

                    placeList.put(place.getId(),place);
                } catch (JSONException e) {
                    Log.d("", "parseDirection: distance : error");
                    if(place != null)
                        placeList.put(place.getId(),place);
                } catch (Exception e) {
                    Log.d("", "parseDirection: distance : error");
                    if(place != null)
                        placeList.put(place.getId(),place);
                }
            }

        } catch (JSONException e) {
            Log.d("", "parseDirection: distance : error");
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("", "parseDirection: distance : error");
        }

        return placeList;
    }


    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

}
