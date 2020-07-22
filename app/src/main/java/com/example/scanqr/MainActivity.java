package com.example.scanqr;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Button btnGallery, btnCamera;
    TextView txt;
    Uri imageUri;
    Bitmap photoBitmap;
    private static final int PICK_IMG = 1;
    private static final int TAKE_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnGallery = findViewById(R.id.button);
        btnCamera = findViewById(R.id.button2);
        txt = findViewById(R.id.txt);
    }

    public void tap(View v) {
        if (v.getId() == btnGallery.getId()) {
            Intent gallery = new Intent();
            gallery.setType("image/*");
            gallery.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(gallery, "Select picture"), PICK_IMG);
        } else if (v.getId() == btnCamera.getId()) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, TAKE_PHOTO);
            }
        }
    }

    private void scanQR() {
        BarcodeDetector detector = new BarcodeDetector.Builder(getApplicationContext()).
                setBarcodeFormats(Barcode.ALL_FORMATS).build();
        if (!detector.isOperational()) {
            txt.setText("Что-то пошло не так");
        } else {
            Frame frame = new Frame.Builder().setBitmap(photoBitmap).build();
            SparseArray<Barcode> barcodes = detector.detect(frame);
            try {
                Barcode code = barcodes.valueAt(0);
                txt.setText(code.rawValue);
            } catch (IndexOutOfBoundsException e) {
                txt.setText("QR-код распознать не удалось. Попробуйте еще раз");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMG) {
                imageUri = data.getData();
                try {
                    photoBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    scanQR();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == TAKE_PHOTO) {
                Bundle extras = data.getExtras();
                photoBitmap = (Bitmap) extras.get("data");
                scanQR();
            }
        }
    }
}