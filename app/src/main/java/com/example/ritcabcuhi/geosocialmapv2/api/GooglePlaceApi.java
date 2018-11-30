package com.example.ritcabcuhi.geosocialmapv2.api;

import android.os.AsyncTask;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.ritcabcuhi.geosocialmapv2.api.listener.GoogleDirectionApiListener;
import com.example.ritcabcuhi.geosocialmapv2.api.listener.GooglePlaceApiListener;
import com.example.ritcabcuhi.geosocialmapv2.model.Direction;
import com.example.ritcabcuhi.geosocialmapv2.model.Place;
import com.example.ritcabcuhi.geosocialmapv2.utils.JSONParserUtils;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GooglePlaceApi {
    private static final String TAG = "GooglePlaceApi";
    private static GooglePlaceApi googlePlaceApi;
    private GooglePlaceApiListener mListener;
    private String key;

    public static GooglePlaceApi getInstace(){
        if (googlePlaceApi == null) {
            googlePlaceApi = new GooglePlaceApi();
        }
        return googlePlaceApi;
    }

    public GooglePlaceApi getNeaybyPlace(LatLng origin, String textInput, int radius){
        new DownloadTask().execute(getPlaceUrl(origin,textInput,radius));
        return googlePlaceApi;
    }

    private String downloadUrl(String url){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try{
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (NullPointerException e){
            Log.e(TAG, "downloadUrl: ",e );
        } catch (IOException e){
            Log.e(TAG, "downloadUrl: ",e );
        }

        return null;
    }

    private String getPlaceUrl(LatLng origin, String textInput, int radius){
        String str_origin = origin.latitude + "," + origin.longitude;

        String location = "location=" + str_origin;

        String radiusText = "&radius=" + radius;

        String typeText = "&type=" + textInput;

        String parameters = location + radiusText + typeText;

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + parameters + "&key=" + key;

        Log.d(TAG, "getPlaceUrl: " + url);

        return url;
    }

    public void setApiKey(String key){
        this.key = key;
    }

    public void setListener(GooglePlaceApiListener listener){
        mListener = listener;
    }

    class DownloadTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... strings) {
            return downloadUrl(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s!=null){
                Log.d(TAG, "response : " + s);
                new JSONParserTask().execute(s);
            }
        }
    }

    class JSONParserTask extends AsyncTask<String,Integer,HashMap<String,Place>>{
        @Override
        protected HashMap<String,Place> doInBackground(String... strings) {
            try{
                Log.d(TAG, "doInBackground: " + strings[0]);
                JSONParserUtils utils = new JSONParserUtils();
                return utils.parsePlace(new JSONObject(strings[0]));
            }catch (JSONException e) {
                Log.e(TAG, "doInBackground: ", e);
            }catch (NullPointerException e){
                Log.e(TAG, "doInBackground: ", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(HashMap<String,Place> placeList) {
            super.onPostExecute(placeList);
            String keyH;
            Place valueH;
            if(placeList != null){
                for(Map.Entry<String,Place> place: placeList.entrySet()){
                    valueH = place.getValue();
                    keyH = place.getKey();

                    placeList.get(keyH).setImageUrl("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + valueH.getPhotoReference() + "&key=" + key);
                    placeList.get(keyH).setImageUri("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + valueH.getPhotoReference() + "&key=" + key);
                }
            }

            if(mListener != null)
                mListener.onSuccess(placeList);
        }
    }

//    class RequestPhotoUrlTask extends AsyncTask<List<Place>,Integer,List<Place>>{
//        @Override
//        protected List<Place> doInBackground(List<Place>... lists) {
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(List<Place> places) {
//            super.onPostExecute(places);
//        }
//    }

}
