package com.cheng.autocut;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.cheng.openvclib.AutoCut;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_TAKE_PHOTO = 100;
    private static final int REQUEST_CODE_SELECT_ALBUM = 200;

    private ImageView imageView;

    private File cutPhotoFile;
    private File sourcePhotoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnCamera = findViewById(R.id.btn_camera);
        Button btnGallery = findViewById(R.id.btn_gallery);
        imageView = findViewById(R.id.iv_cut_view);

        btnCamera.setOnClickListener(this);
        btnGallery.setOnClickListener(this);

        cutPhotoFile = new File(getExternalFilesDir("img"), "cut.jpg");
        sourcePhotoFile = new File(getExternalFilesDir("img"), "source.jpg");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_camera:
                Intent startCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(sourcePhotoFile));
                if (startCameraIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(startCameraIntent, REQUEST_CODE_TAKE_PHOTO);
                }
                break;
            case R.id.btn_gallery:
                Intent selectIntent = new Intent(Intent.ACTION_PICK);
                selectIntent.setType("image/*");
                if (selectIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(selectIntent, REQUEST_CODE_SELECT_ALBUM);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            setResult(RESULT_CANCELED);
            return;
        }
        Bitmap selectedBitmap = null;
        if (requestCode == REQUEST_CODE_TAKE_PHOTO && sourcePhotoFile.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(sourcePhotoFile.getPath(), options);
            options.inJustDecodeBounds = false;
            options.inSampleSize = calculateSampleSize(options);
            selectedBitmap = BitmapFactory.decodeFile(sourcePhotoFile.getPath(), options);
        } else if (requestCode == REQUEST_CODE_SELECT_ALBUM && data != null && data.getData() != null) {
            ContentResolver cr = getContentResolver();
            Uri bmpUri = data.getData();
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(cr.openInputStream(bmpUri), new Rect(), options);
                options.inJustDecodeBounds = false;
                options.inSampleSize = calculateSampleSize(options);
                selectedBitmap = BitmapFactory.decodeStream(cr.openInputStream(bmpUri), new Rect(), options);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (selectedBitmap != null) {
            Bitmap crop = AutoCut.crop(selectedBitmap, AutoCut.scan(selectedBitmap));
            if (crop != null) {
                imageView.setImageBitmap(crop);
                saveImage(crop, cutPhotoFile);
            }
        }
    }

    private int calculateSampleSize(BitmapFactory.Options options) {
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        int sampleSize = 1;
        int destHeight = 1000;
        int destWidth = 1000;
        if (outHeight > destHeight || outWidth > destHeight) {
            if (outHeight > outWidth) {
                sampleSize = outHeight / destHeight;
            } else {
                sampleSize = outWidth / destWidth;
            }
        }
        if (sampleSize < 1) {
            sampleSize = 1;
        }
        return sampleSize;
    }

    private void saveImage(Bitmap bitmap, File saveFile) {
        try {
            FileOutputStream fos = new FileOutputStream(saveFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
