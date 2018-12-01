package com.example.ritcabcuhi.geosocialmapv2.api;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.ritcabcuhi.geosocialmapv2.api.listener.ApiListener;
import com.example.ritcabcuhi.geosocialmapv2.model.Place;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class PlaceApi {

    private static PlaceApi placeApi;
    private static ApiListener mListener;

    public static PlaceApi getInstance(){
        if(placeApi == null)
            placeApi = new PlaceApi();
        return placeApi;
    }

    public PlaceApi createPlace(Place place){
        final DatabaseReference tablePlace = FirebaseDatabase.getInstance().getReference().child("Place");

        tablePlace.child(place.getId()).setValue(place);
        retrieveImageUri(place);

        return placeApi;
    }

    public PlaceApi removePlace(Place place){
        final DatabaseReference tablePlace = FirebaseDatabase.getInstance().getReference().child("Place");

        tablePlace.child(place.getId()).removeValue();

        return placeApi;
    }

    private void retrieveImageUri(final Place place){
        if(place.getImageUri() == null){
            FirebaseStorage.getInstance().getReference(place.getImageUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    place.setImageUri(uri.toString());
                    createPlace(place);
                }
            });
        }
    }

    public void setListener(ApiListener listener){
        mListener = listener;
        final DatabaseReference tablePlace = FirebaseDatabase.getInstance().getReference().child("Place");
        tablePlace.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mListener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mListener.onFailure(databaseError);
            }
        });
    }
}
