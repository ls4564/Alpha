package com.example.alpha;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FBRef {
    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();

    public static FirebaseStorage storage = FirebaseStorage.getInstance();
    public static StorageReference refStorage = storage.getReference();
    public static FirebaseAuth refAuth = FirebaseAuth.getInstance();
    public static DatabaseReference refItems = FBDB.getReference("Items");
}
