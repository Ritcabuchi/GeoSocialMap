package com.example.ritcabcuhi.geosocialmapv2.Manager;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.ritcabcuhi.geosocialmapv2.EventBus.MainEvent;
import com.example.ritcabcuhi.geosocialmapv2.Model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

                    EventBus.getDefault().post(new MainEvent());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled: ", databaseError.toException());
                }
            });
        }
    }
}
