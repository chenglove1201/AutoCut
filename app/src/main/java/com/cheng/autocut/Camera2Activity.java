package com.cheng.autocut;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.TextureView;

import com.cheng.openvclib.AutoCut;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Camera2Activity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private TextureView myTexture;
    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camrea2);
        myTexture = findViewById(R.id.textureView1);
        myTexture.setSurfaceTextureListener(this);
        matrix = new Matrix();
        matrix.setRotate(90);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1,
                                          int arg2) {
        mCamera = Camera.open();
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            //获取预览大小
            size = parameters.getPreviewSize();
            //设置预览大小
            parameters.setPreviewSize(1920, 1080);
            mCamera.setParameters(parameters);
            //摄像头进行旋转90°
            mCamera.setDisplayOrientation(90);
            try {
                mCamera.setPreviewTexture(arg0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCamera.startPreview();
            startScan();
        }
    }

    /**
     * 定时开启扫描区域
     */
    private void startScan() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                mCamera.autoFocus(autoFocusCallback);

            }
        }, 1000, 3000);
    }

    private Camera.Size size;
    private YuvImage image;
    private ByteArrayOutputStream os;
    private byte[] tmp;
    private Bitmap bmp;
    private Matrix matrix;
    private Bitmap bitmap;

    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                camera.setOneShotPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        long j = System.currentTimeMillis();
                        Log.i("jfowiejgiojwoieg", size.width + "..." + size.height);
                        image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                        os = new ByteArrayOutputStream(data.length);
                        if (!image.compressToJpeg(new Rect(0, 0, size.width, size.height), 100, os)) {
                            return;
                        }
                        tmp = os.toByteArray();
                        bmp = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);
                        bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                        Log.i("Jfoiwejijgioweg", (System.currentTimeMillis() - j) + "");
//                        Point[] points = AutoCut.scan(bitmap);
                        if (bitmap != null) {
                            Log.i("jfowiejgwe", "have....");
                        } else {
                            Log.i("jfowiejgwe", "null...");
                        }
                    }
                });
            }
        }
    };

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }
        return true;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1,
                                            int arg2) {
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture arg0) {
    }
}
