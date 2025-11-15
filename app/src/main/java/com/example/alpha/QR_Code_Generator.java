package com.example.alpha;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QR_Code_Generator extends BaseActivity {
    EditText etText;
    ImageView imageCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Make sure you are using the correct layout file, e.g., R.layout.activity_qr_code_generator
        setContentView(R.layout.activity_qr_code_generator);
        etText = findViewById(R.id.etText);
        imageCode = findViewById(R.id.imageCode);
    }

    public void Generate_Click(View view) {
        String myText = etText.getText().toString().trim();
        if (myText.isEmpty()) {
            etText.setError("Text cannot be empty");
            return;
        }

        MultiFormatWriter mWriter = new MultiFormatWriter();
        try {
            BitMatrix mMatrix = mWriter.encode(myText, BarcodeFormat.QR_CODE, 400, 400);
            BarcodeEncoder mEncoder = new BarcodeEncoder();
            Bitmap mBitmap = mEncoder.createBitmap(mMatrix);
            imageCode.setImageBitmap(mBitmap);

            // Hide the keyboard after generating the code.
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager != null) {
                manager.hideSoftInputFromWindow(etText.getApplicationWindowToken(), 0);
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
