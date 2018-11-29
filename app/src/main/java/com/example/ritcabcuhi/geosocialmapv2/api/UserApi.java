package com.example.ritcabcuhi.geosocialmapv2.api;

import android.support.annotation.NonNull;

import com.example.ritcabcuhi.geosocialmapv2.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserApi {
    private static UserApi userApi;
    private static ApiListener mListener;

    public static UserApi getInstance(){
        if(userApi == null)
            userApi = new UserApi();
        return userApi;
    }

    public UserApi createUser(final String name,final FirebaseUser firebaseUser){
        final DatabaseReference tableUser = FirebaseDatabase.getInstance().getReference().child("User");

        User user = new User();
        user.setId(firebaseUser.getUid());
        user.setEmail(firebaseUser.getEmail());
        user.setName(name);

        tableUser.child(firebaseUser.getUid()).setValue(user);

        return userApi;
    }

    public UserApi updateUserData(User user){
        final DatabaseReference tableUser = FirebaseDatabase.getInstance().getReference().child("User");

        tableUser.child(user.getId()).setValue(user);
        return userApi;
    }

    public void setListener(ApiListener listener){
        mListener = listener;
        final DatabaseReference tableUser = FirebaseDatabase.getInstance().getReference().child("User");
        tableUser.addValueEventListener(new ValueEventListener() {
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
