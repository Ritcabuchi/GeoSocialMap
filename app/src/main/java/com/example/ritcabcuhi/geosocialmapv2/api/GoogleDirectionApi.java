package com.example.ritcabcuhi.geosocialmapv2.api;

import android.os.AsyncTask;
import android.util.Log;

import com.example.ritcabcuhi.geosocialmapv2.api.listener.GoogleDirectionApiListener;
import com.example.ritcabcuhi.geosocialmapv2.model.Direction;
import com.example.ritcabcuhi.geosocialmapv2.utils.JSONParserUtils;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GoogleDirectionApi {
    private static final String TAG = "GoogleDirectionApi";
    private static GoogleDirectionApi directionAPI;
    private String key;
    private GoogleDirectionApiListener mListerner;

    public static GoogleDirectionApi getInstance(){
        if(directionAPI == null)
            directionAPI = new GoogleDirectionApi();
        return directionAPI;
    }

    public GoogleDirectionApi findPath(LatLng origin, LatLng dest){

        new DownloadTask().execute(getDirectionsUrl(origin,dest));

        return directionAPI;
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

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + key;

        return url;
    }

    public void setApiKey(String key){
        this.key = key;
    }

    public void setListener(GoogleDirectionApiListener listener){
        mListerner = listener;
    }

    class DownloadTask extends AsyncTask<String,Integer,String>{
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

    class JSONParserTask extends AsyncTask<String,Integer,Direction>{
        @Override
        protected Direction doInBackground(String... strings) {
            try{
                JSONParserUtils utils = new JSONParserUtils();
                return utils.parseDirection(new JSONObject(strings[0]));
            }catch (JSONException e){
                Log.e(TAG, "doInBackground: ", e);
            }catch (NullPointerException e){
                Log.e(TAG, "doInBackground: ", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Direction direction) {
            super.onPostExecute(direction);
            if(direction != null)
                mListerner.onSuccess(direction);
        }
    }
}
