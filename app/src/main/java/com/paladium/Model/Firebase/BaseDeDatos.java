package com.paladium.Model.Firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class BaseDeDatos {

    private static DatabaseReference fireDatabaseIntanceReference;
    private static StorageReference fireStorageInstanceReference;

    public BaseDeDatos() {
        fireDatabaseIntanceReference = FirebaseDatabase.getInstance().getReference();
        fireStorageInstanceReference = FirebaseStorage.getInstance().getReference();
    }

    public static DatabaseReference getFireDatabaseIntanceReference() {
        return fireDatabaseIntanceReference;
    }

    public static StorageReference getFireStorageInstanceReference() {
        return fireStorageInstanceReference;
    }


}
