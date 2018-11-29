package com.example.ritcabcuhi.geosocialmapv2.api;

import android.database.DatabaseErrorHandler;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public interface ApiListener {
    void onSuccess(DataSnapshot dataSnapshot);
    void onFailure(DatabaseError error);
}
