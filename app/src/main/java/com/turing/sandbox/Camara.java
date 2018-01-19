package com.turing.sandbox;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Camera;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.bytedeco.javacpp.opencv_core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.bytedeco.javacpp.opencv_core.CV_32SC1;
import static org.bytedeco.javacpp.opencv_core.CV_8UC1;
import static org.bytedeco.javacpp.opencv_face.FisherFaceRecognizer.*;
// import static org.bytedeco.javacpp.opencv_face.createEigenFaceRecognizer;
// import static org.bytedeco.javacpp.opencv_face.createLBPHFaceRecognizer;
import org.bytedeco.javacpp.opencv_face;
import org.bytedeco.javacpp.opencv_imgcodecs;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;

/**
 * Created by usuario on 1/18/2018.
 */

public class Camara extends AppCompatActivity{

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private Camera mCamera;
    private CameraPreview mPreview;
    private Button btnTomar, btnrec;

    private File image1;

    private ImageView img1, img2;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara);

        btnTomar = findViewById(R.id.btn_capturar);
        btnrec = findViewById(R.id.btn_reconocer);
        img1 = findViewById(R.id.image1);
        img2 = findViewById(R.id.image2);

        // Create an instance of Camera
        mCamera = getCameraInstance();

        int orientation = getWindowManager().getDefaultDisplay().getRotation();
        Log.d("CAM", "Rotation: " + orientation);

        if(orientation == 0){
            mPreview = new CameraPreview(this, mCamera, 90);
        }else if(orientation == 1){
            mPreview = new CameraPreview(this, mCamera, 0);
        }else if(orientation == 2){
            mPreview = new CameraPreview(this, mCamera, 270);
        }else if(orientation == 3){
            mPreview = new CameraPreview(this, mCamera, 180);
        }

        // Create our Preview view and set it as the content of our activity.

        FrameLayout preview = findViewById(R.id.camara_preview);
        preview.addView(mPreview);

        btnTomar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.takePicture(null, null, mPicture);
            }
        });

        btnrec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reconicer();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCamera = null;
    }



    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            Log.i("Camara", "" + Camera.getNumberOfCameras());
            c = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d("CAM", "Error creating media file, check storage permissions: ");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                img1.setImageURI(Uri.fromFile(pictureFile));
                img2.setImageURI(Uri.fromFile(pictureFile));
                image1 = pictureFile;
            } catch (FileNotFoundException e) {
                Log.d("CAM", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("CAM", "Error accessing file: " + e.getMessage());
            }
        }
    };

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    /** A basic Camera preview class */
    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;
        private int rotation;

        public CameraPreview(Context context, Camera camera, int rotation) {
            super(context);
            mCamera = camera;

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            this.rotation = rotation;
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                Log.d("CAM", "Error setting camera preview: " + e.getMessage());
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // empty. Take care of releasing the Camera preview in your activity.
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null){
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e){
                // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here

            // start preview with new settings
            try {
                int orientation = getWindowManager().getDefaultDisplay().getRotation();
                Log.d("CAM", "Rotation: " + orientation);
                mCamera.setDisplayOrientation(rotation);
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e){
                Log.d("CAM", "Error starting camera preview: " + e.getMessage());
            }
        }
    }

    public void reconicer(){
        String trainingDir = image1.getAbsolutePath();
        opencv_core.Mat testImage = imread(image1.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);

        File root = new File(trainingDir);

        FilenameFilter imgFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
            }
        };

        File imageFiles = image1;

        opencv_core.MatVector images = new opencv_core.MatVector(1);

        Mat labels = new Mat(1, 1, CV_32SC1);
        IntBuffer labelsBuf = labels.createBuffer();

        int counter = 0;

        Mat img = imread(imageFiles.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);

        //int label = Integer.parseInt(imageFiles.getName().split(".")[0]);
        int label = 10;
        images.put(counter, img);

        labelsBuf.put(counter, label);

        /*for (File image : imageFiles) {
            Mat img = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);

            int label = Integer.parseInt(image.getName().split("\\-")[0]);

            images.put(counter, img);

            labelsBuf.put(counter, label);

            counter++;
        }

        //FaceRecognizer faceRecognizer = createFisherFaceRecognizer();*/
        FaceRecognizer faceRecognizer = opencv_face.FisherFaceRecognizer.create();
        // FaceRecognizer faceRecognizer = createEigenFaceRecognizer();
        // FaceRecognizer faceRecognizer = createLBPHFaceRecognizer()

        faceRecognizer.train(images, labels);

        IntPointer label2 = new IntPointer(1);
        DoublePointer confidence = new DoublePointer(1);
        faceRecognizer.predict(testImage, label2, confidence);
        int predictedLabel = label2.get(0);

        System.out.println("Predicted label: " + predictedLabel);
    }
}
