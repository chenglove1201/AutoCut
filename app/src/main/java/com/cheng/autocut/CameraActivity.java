package com.cheng.autocut;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.cheng.autocut.utils.AreaUtil;
import com.cheng.autocut.view.AreaView;
import com.cheng.openvclib.AutoCut;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private AreaView areaView;
    //    private int viewWidth, viewHeight;
    private Camera.Parameters parameters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camrea);
        initView();
    }

    private void initView() {
        areaView = findViewById(R.id.area);
        mSurfaceView = findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        // mSurfaceView 不需要自己的缓冲区
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                initCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // 释放Camera
                if (mCamera != null) {
                    mCamera.stopPreview();
                    mCamera.release();
                }
            }
        });
        mSurfaceView.setOnClickListener(this);
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (mSurfaceView != null) {
//            viewWidth = mSurfaceView.getWidth();
//            viewHeight = mSurfaceView.getHeight();
//        }
//    }

    private void initCamera() {
        mCamera = Camera.open();
        mCamera.setDisplayOrientation(90);//摄像头进行旋转90°
        if (mCamera != null) {
            try {
                parameters = mCamera.getParameters();
                //设置图片的质量
                parameters.setJpegQuality(100);
                int[] i = new int[2];
                parameters.getPreviewFpsRange(i);
                //设置相机预览照片帧数
                parameters.setPreviewFpsRange(i[0] / 1000, i[1] / 1000);
                //设置照片大小
                parameters.setPictureSize(mSurfaceView.getHeight(), mSurfaceView.getWidth());
                //设置预览大小
                parameters.setPreviewSize(mSurfaceView.getHeight(), mSurfaceView.getWidth());
                parameters.setPreviewFormat(ImageFormat.NV21);
                parameters.setPictureFormat(ImageFormat.JPEG);
                mCamera.setParameters(parameters);
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();
                startScan();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 定时开启扫描区域
     */
    private void startScan() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Log.i("Jfoiwejijgioweg", "78fw7e87f8w7e8fw");
                mCamera.autoFocus(autoFocusCallback);

            }
        }, 1000, 3000);
    }

    @Override
    public void onClick(View v) {
//        if (mCamera == null) {
//            return;
//        }
//        mCamera.autoFocus(autoFocusCallback);
    }

    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            Log.i("Jfoiwejijgioweg", "aaaaaaaaaaaaaaaaaa");
            if (success) {
                camera.setOneShotPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        Log.i("Jfoiwejijgioweg", "==================");
                        long j = System.currentTimeMillis();
                        Camera.Size size = camera.getParameters().getPreviewSize(); //获取预览大小
                        final int w = size.width;  //宽度
                        final int h = size.height;
                        final YuvImage image = new YuvImage(data, ImageFormat.NV21, w, h, null);
                        ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
                        if (!image.compressToJpeg(new Rect(0, 0, w, h), 100, os)) {
                            return;
                        }
                        byte[] tmp = os.toByteArray();
                        Bitmap bmp = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);
                        Matrix matrix = new Matrix();
                        matrix.setRotate(90);
                        Bitmap bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
//                        Log.i("Jfoiwejijgioweg", (System.currentTimeMillis() - j) + "");
                        long l = System.currentTimeMillis();
                        Point[] points = AutoCut.scan(bitmap);
                        Log.i("Jfoiwejijgioweg", (System.currentTimeMillis() - l) + "");
//                        areaView.updateArea(points, bitmap);
                    }
                });
            }
//            if (success) {//对焦成功
//
//                camera.takePicture(new Camera.ShutterCallback() {//按下快门
//                    @Override
//                    public void onShutter() {
//                        //按下快门瞬间的操作
//                    }
//                }, new Camera.PictureCallback() {
//                    @Override
//                    public void onPictureTaken(byte[] data, Camera camera) {//是否保存原始图片的信息
//
//                    }
//                }, null);
//            }
        }
    };
//    /**
//     * 获取图片
//     */
//    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
//        @Override
//        public void onPictureTaken(byte[] data, Camera camera) {
//            final Bitmap resource = BitmapFactory.decodeByteArray(data, 0, data.length);
//            if (resource == null) {
//                Toast.makeText(MainActivity.this, "拍照失败", Toast.LENGTH_SHORT).show();
//            }
//            final Matrix matrix = new Matrix();
//            matrix.setRotate(90);
//            final Bitmap bitmap = Bitmap.createBitmap(resource, 0, 0, resource.getWidth(), resource.getHeight(), matrix, true);
//            if (bitmap != null && iv_show != null && iv_show.getVisibility() == View.GONE) {
//                mCamera.stopPreview();
//                iv_show.setVisibility(View.VISIBLE);
//                mSurfaceView.setVisibility(View.GONE);
//                Toast.makeText(MainActivity.this, "拍照", Toast.LENGTH_SHORT).show();
//                iv_show.setImageBitmap(bitmap);
//            }
//        }
//    };
}
