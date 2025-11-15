package com.example.alpha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class Gallery extends BaseActivity {

    private static final int REQUEST_PICK_IMAGE = 300;

    ImageView ivPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ivPicture = findViewById(R.id.ivPicture);
    }

    public void Upload_Click(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadImageToFirebase(imageUri); // Start the upload process
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.setCancelable(false);
        pd.show();

        String fileName = "Gallery"; // Using a fixed name as per the code
        StorageReference fileRef = FBRef.refStorage.child("images/" + fileName);

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    pd.dismiss();
                    Toast.makeText(Gallery.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(Gallery.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnProgressListener(snapshot -> {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    pd.setMessage("Uploaded " + (int) progress + "%");
                });
    }

    /**
     * Downloads the specific "Gallery" image from Firebase Storage into memory and displays it.
     */
    public void Download_Click(View view) {
        // Create a direct reference to the file.
        StorageReference fileToDownload = FBRef.refStorage.child("images/Gallery");

        // Set up a ProgressDialog.
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Downloading: Gallery");
        pd.setMessage("Please wait...");
        pd.setCancelable(false);
        pd.show();

        // Define the maximum size of the image to download (e.g., 5MB).
        final long MAX_SIZE = 5 * 1024 * 1024;

        fileToDownload.getBytes(MAX_SIZE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        pd.dismiss();
                        // Decode the byte array into a Bitmap and set it on the ImageView.
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        ivPicture.setImageBitmap(bitmap);
                        Toast.makeText(Gallery.this, "Download successful.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(Gallery.this, "Download Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
