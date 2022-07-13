package com.paladium.Model.Firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class BaseDeDatos {

    private static DatabaseReference fireDatabase;
    private static StorageReference fireStorageReference;

    public BaseDeDatos() {
        fireDatabase = FirebaseDatabase.getInstance().getReference();
        fireStorageReference = FirebaseStorage.getInstance().getReference();
    }

    public static DatabaseReference getFireDatabase() {
        return fireDatabase;
    }

    public static StorageReference getFireStorageReference() {
        return fireStorageReference;
    }


}
