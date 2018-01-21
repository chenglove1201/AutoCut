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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;

import com.cheng.autocut.entity.ViewPoint;
import com.cheng.autocut.utils.AreaUtil2;
import com.cheng.autocut.utils.ImageUtil;
import com.cheng.autocut.view.AreaView;
import com.cheng.openvclib.AutoCut;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Camera2Activity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private TextureView myTexture;
    private Camera mCamera;
    private AreaView areaView;
    private Camera.Parameters parameters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camrea2);
        myTexture = findViewById(R.id.textureView1);
        areaView = findViewById(R.id.area);
        myTexture.setSurfaceTextureListener(this);
        matrix = new Matrix();
        matrix.setRotate(90);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1,
                                          int arg2) {
        mCamera = Camera.open();
        if (mCamera != null) {
            parameters = mCamera.getParameters();
            int myTextureHeight = myTexture.getHeight();
            int myTextureWidth = myTexture.getWidth();
            List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
            int previewSize = supportedPreviewSizes.size();
            if (previewSize > 0) {
                Camera.Size maxSize = supportedPreviewSizes.get(previewSize - 1);
                if (myTextureHeight <= maxSize.height
                        && myTextureWidth <= maxSize.width) {
                    parameters.setPreviewSize(myTextureHeight, myTextureWidth);
                }
            }
//            parameters.setPictureSize(2560, 1440);
            List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
            int pictureSize = supportedPictureSizes.size();
            if (pictureSize > 0) {
                Camera.Size maxSize = supportedPictureSizes.get(pictureSize - 1);
                parameters.setPictureSize(maxSize.height,
                        maxSize.width);
            }
            parameters.setJpegQuality(100);
            parameters.setAutoWhiteBalanceLock(true);
            //聚焦模式
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            //获取预览大小
            this.size = parameters.getPreviewSize();
            mCamera.setParameters(parameters);
            //摄像头进行旋转90°
            mCamera.setDisplayOrientation(90);
            try {
                mCamera.setPreviewTexture(arg0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCamera.startPreview();
//            mCamera.cancelAutoFocus();
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    if (mFaceTask != null) {
                        switch (mFaceTask.getStatus()) {
                            case RUNNING:
                                return;
                            case PENDING:
                                mFaceTask.cancel(false);
                                break;
                            default:
                                break;
                        }
                    }
                    mFaceTask = new RectangleDetectTask(data);
                    mFaceTask.execute((Void) null);
                }
            });
//            startScan();
        }
    }

    private RectangleDetectTask mFaceTask;

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

    private class RectangleDetectTask extends AsyncTask<Void, Void, Void> {
        private byte[] data;

        RectangleDetectTask(byte[] data) {
            this.data = data;
        }

        @Override
        protected Void doInBackground(Void... voids) {
//            long j = System.currentTimeMillis();
            YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
            ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
            if (!image.compressToJpeg(new Rect(0, 0, size.width, size.height), 0, os)) {
                return null;
            }
            byte[] tmp = os.toByteArray();
            Bitmap bmp = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);
            Bitmap bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
//            Log.i("Jfoiwejijgioweg", (System.currentTimeMillis() - j) + "");
//            long u = System.currentTimeMillis();
            Point[] points = AutoCut.scan(bitmap);
//            long h = System.currentTimeMillis() - j;
//            ViewPoint[] viewPoints = AreaUtil.filterPoint(points, Camera2Activity.this, myTexture.getWidth(),
//                    myTexture.getHeight(), bitmap.getWidth(), bitmap.getHeight());
            ViewPoint[] viewPoints = AreaUtil2.filterPoint(points, Camera2Activity.this);
            areaView.updateArea(viewPoints);
            return null;
        }
    }

    private Camera.Size size;
    private Matrix matrix;

    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                mCamera.takePicture(null, null, new MyPictureCallback());
            }
        }
    };

    /**
     * 拍照
     *
     * @param rect
     */
    public void shutter(Rect rect) {
        mCamera.setPreviewCallback(null);
        List<Camera.Area> areaList = new ArrayList<>();
        areaList.add(new Camera.Area(rect, 100));
        parameters.setFocusAreas(areaList);
//        parameters.setMeteringAreas(areaList);
        mCamera.setParameters(parameters);
        mCamera.autoFocus(autoFocusCallback);
    }

    /**
     * 获取图片
     */
    private class MyPictureCallback implements Camera.PictureCallback {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap resource = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (resource == null) {
                return;
            }
            Bitmap bitmap = Bitmap.createBitmap(resource, 0, 0, resource.getWidth(), resource.getHeight(), matrix, true);
            Bitmap crop = AutoCut.crop(bitmap, AutoCut.scan(bitmap));
            ImageUtil.saveImageToGallery(Camera2Activity.this, crop);
        }
    }

    private ViewPoint[] getViewPoints(Point[] points) {
        ViewPoint viewPoint0 = new ViewPoint(points[0].x, points[0].y);
        ViewPoint viewPoint1 = new ViewPoint(points[1].x, points[1].y);
        ViewPoint viewPoint2 = new ViewPoint(points[2].x, points[2].y);
        ViewPoint viewPoint3 = new ViewPoint(points[3].x, points[3].y);
        return new ViewPoint[]{viewPoint0, viewPoint1, viewPoint2, viewPoint3};
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
