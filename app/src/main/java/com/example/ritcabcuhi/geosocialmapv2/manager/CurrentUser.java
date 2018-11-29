package com.example.ritcabcuhi.geosocialmapv2.manager;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.ritcabcuhi.geosocialmapv2.eventbus.DataEditEvent;
import com.example.ritcabcuhi.geosocialmapv2.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.greenrobot.eventbus.EventBus;

public class CurrentUser {
    private static final String TAG = "CurrentUser";
    private static CurrentUser currentUser;
    private static User user;

    public interface OnCompleteListener{
        void onComplete(User user);
    }

    public static CurrentUser getInstace(){
        if(currentUser == null) {
            currentUser = new CurrentUser();
        }
        return currentUser;
    }

    public User getUser(){
        return user;
    }


    public void updateCurrentUser(String uid){
        if(uid!=null) {
            final FirebaseDatabase db = FirebaseDatabase.getInstance();
            final DatabaseReference tableUser = db.getReference("User");

            tableUser.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onDataChange: ");
                    user = dataSnapshot.getValue(User.class);
                    retrieveImageUri();

                    EventBus.getDefault().post(new DataEditEvent());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled: ", databaseError.toException());
                }
            });
        }
    }

    private void retrieveImageUri(){
        try{
            Log.d(TAG, "retrieveImageUri: " + user.getImageUrl());
            FirebaseStorage.getInstance().getReference(user.getImageUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    user.setImageUri(uri.toString());
                    EventBus.getDefault().post(new DataEditEvent());
                }
            });
        }catch (Exception e){
            Log.e(TAG, "retrieveImageUri: ", e);
        }

    }
}
