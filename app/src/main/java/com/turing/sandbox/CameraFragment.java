package com.turing.sandbox;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.Face;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by usuario on 1/22/2018.
 */

public class CameraFragment extends Fragment
        implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback, InterfaceData {

    /**
     * Conversion from screen rotation to JPEG orientation.
     */
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";

    private boolean mFaceDetectSupported = false;
    private int mFaceDetectMode;
    private boolean takePictureOfFace = false;
    private Paint greenPaint;
    private CameraCharacteristics characteristics;
    private int cameraWidth;
    private int cameraHeight;
    private Face detectedFace;
    private Rect rectangleFace;
    private boolean mAutoFocusSupported = false;
    private int fotoRotation = 0;
    private ProgressDialog progress;
    private String[] cameras;
    private int displayRotation;
    private int deviceRotation = 0;
    private Size largest;

    /**
     * Variables del recuadro del rostro
     */
    int outLeft, outTop, outWidth, outHeight;

    private CameraFragment.OnFragmentInteractionListener mListener;

    private SensorManager mSensorManager;
    private Sensor mOrientation;
    /**
     * Sensor de acelerometro, con el detectamos los cambios en la orientacion para cambiar los iconos de la pantalla y el comportamiento de la camara
     */
    private SensorEventListener mOrientationListener = new SensorEventListener() {
        int orientation = 0;
        @Override
        public void onSensorChanged(SensorEvent event){
            Activity activity = getActivity();
            if(Sensor.TYPE_ACCELEROMETER != event.sensor.getType()){
                return;
            }

            if ((int)event.values[1] > 0 && (int)event.values[0] == 0) {
                final int value = Surface.ROTATION_0;//portrait
                if (orientation != value) {
                    Log.d("orientation", "portrait  + update");
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final RotateAnimation rotateAnim = new RotateAnimation((float)orientation, (float) value,
                                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);

                            rotateAnim.setDuration(500);
                            getView().findViewById(R.id.cam_btnBack).startAnimation(rotateAnim);
                        }
                    });
                    deviceRotation = 0;
                }
                orientation = value;

            }else if ((int)event.values[1] < 0 && (int)event.values[0] == 0) {
                final int value = Surface.ROTATION_180;//portrait reverse
                if (orientation != value) {
                    Log.d("orientation", "portrait reverse + update");
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final RotateAnimation rotateAnim = new RotateAnimation((float)orientation, (float) value,
                                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);

                            rotateAnim.setDuration(500);
                            getView().findViewById(R.id.cam_btnBack).startAnimation(rotateAnim);
                        }
                    });
                    deviceRotation = 180;
                }
                orientation = value;

            }else if ((int)event.values[0] > 0 && (int)event.values[1] == 0) {
                final int value = Surface.ROTATION_90;//portrait reverse
                if (orientation != value) {
                    Log.d("orientation", "landscape  + update");
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final RotateAnimation rotateAnim = new RotateAnimation((float)orientation, (float) value,
                                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);

                            rotateAnim.setDuration(500);
                            getView().findViewById(R.id.cam_btnBack).startAnimation(rotateAnim);
                        }
                    });
                    deviceRotation = 90;
                }
                orientation = value;

            }else if ((int)event.values[0] < 0 && (int)event.values[1] == 0) {
                final int value = Surface.ROTATION_270;//portrait reverse
                if (orientation != value) {
                    Log.d("orientation", "landscape reverse + update");
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final RotateAnimation rotateAnim = new RotateAnimation((float)orientation, (float) value,
                                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                            rotateAnim.setInterpolator(new LinearInterpolator());
                            rotateAnim.setDuration(500);
                            getView().findViewById(R.id.cam_btnBack).setAnimation(rotateAnim);
                            getView().findViewById(R.id.cam_btnBack).startAnimation(rotateAnim);

                        }
                    });
                }
                deviceRotation = 270;
                orientation = value;

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };


    /**
     * Tag for the {@link Log}.
     */
    private static final String TAG = "Camera2BasicFragment";

    /**
     * Camera state: Showing camera preview.
     */
    private static final int STATE_PREVIEW = 0;

    /**
     * Camera state: Waiting for the focus to be locked.
     */
    private static final int STATE_WAITING_LOCK = 1;

    /**
     * Camera state: Waiting for the exposure to be precapture state.
     */
    private static final int STATE_WAITING_PRECAPTURE = 2;

    /**
     * Camera state: Waiting for the exposure state to be something other than precapture.
     */
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;

    /**
     * Camera state: Picture was taken.
     */
    private static final int STATE_PICTURE_TAKEN = 4;

    /**
     * Max preview width that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_WIDTH = 1920;

    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_HEIGHT = 1080;

    private int usuario;

    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            openCamera(width, height);
            Rect cameraBounds = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
            cameraWidth = cameraBounds.right;
            cameraHeight = cameraBounds.bottom;

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
            Canvas currentCanvas = mSquareView.getHolder().lockCanvas();
            if(currentCanvas != null){
                currentCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                if (detectedFace != null && rectangleFace.height() > 0) {
                    currentCanvas.save();
                    currentCanvas.rotate(mSensorOrientation,currentCanvas.getWidth() / 2,currentCanvas.getHeight() / 2);

                    //Dimensiones del canvas (estas cambian dependiendo de la posicion del dispositivo)
                    int canvasWidth, canvasHeight;

                    canvasWidth = currentCanvas.getWidth();
                    canvasHeight = currentCanvas.getHeight();

                    //multiplicadores del tamaño
                    double mx, my;
                    //calculos previos dependientes de la rotacion
                    switch(deviceRotation){
                        case 0:{
                            mx = 1.9;
                            my = 1.5;
                            break;
                        }
                        case 90:{
                            mx = 1.5;
                            my = 1.9;


                            break;
                        }
                        case 180:{
                            mx = 1.9;
                            my = 1.5;
                            break;
                        }
                        case 270:{
                            mx = 1.5;
                            my = 1.9;
                            break;
                        }
                        default:{
                            mx = 1.5;
                            my = 1.5;
                        }
                    }

                    //Dimensiones de la imagen final
                    int finalWidth, finalHeight;
                    finalWidth = largest.getWidth();
                    finalHeight = largest.getHeight();

                    //CALCULOS DEL ROSTRO CON SALIDA EN UN ARCHIVO

                    //Dimensiones del  rectangulo de la cara
                    int l = rectangleFace.left;
                    int t = rectangleFace.top;
                    int r = rectangleFace.right;
                    int b = rectangleFace.bottom;

                    //Diferencia entre ejes del rectaangulo de la cara
                    int difx, dify;

                    difx = (r - l)/2;
                    dify = (b - t)/2;

                    //centros en x y y
                    int outCenterx, outCentery;

                    outCenterx = l + (difx);//La diferencia en x es igual la esquina derecha menos la esquina izquierda
                    outCentery = t + (dify);//La diferencia en y es igual la esquina inferior menos la esquina superior

                    //vaiables finales de la ubicacion del rostro
                    int left, right, top, bottom;

                    //calculamos las esquinas del rectangulo del rostro aumentando sus dimensiones
                    left = (int)(outCenterx - (difx * mx));
                    right = (int)(outCenterx + (difx * mx));
                    top = (int)(outCentery - (dify * my));
                    bottom = (int)(outCentery + (dify * my));

                    //convertimos estas dimensiones a la resolucion final
                    outLeft = (left * finalWidth)/cameraWidth;
                    outTop = (top * finalHeight)/cameraHeight;
                    outWidth = ((right - left) * finalWidth)/cameraWidth;
                    outHeight = ((bottom - top) * finalHeight)/cameraHeight;

                    Log.d("DIM", "\nCameraH " + cameraHeight +
                                    "\nCameraW " + cameraWidth +
                                    "\nFaceL " + rectangleFace.left +
                                    "\nFaceR " + rectangleFace.right +
                                    "\nFaceT " + rectangleFace.top +
                                    "\nFaceB " + rectangleFace.bottom +
                                    "\nResH " + finalHeight +
                                    "\nResW " + finalWidth +
                                    "\nFaceL " + left +
                                    "\nFaceR " + right +
                                    "\nFaceT " + top +
                                    "\nFaceB " + bottom +
                                    "\nSensorOr " + mSensorOrientation +
                                    "\nDeviceRotation " + deviceRotation
                    );

                    if((left < 0) || (right > cameraWidth) || (top < 0) || (bottom > cameraHeight)){
                        getView().findViewById(R.id.picture).setEnabled(false);
                        getView().findViewById(R.id.picture).setAlpha(0.5f);
                    }
                    else{
                        getView().findViewById(R.id.picture).setEnabled(true);
                        getView().findViewById(R.id.picture).setAlpha(1f);
                    }

                    //CALCULOS DEL RECTANGULO A DIBUJAR


                    //calculamos los puntos del rectangulo a dibujar
                    l = (l * canvasWidth)/cameraWidth;
                    r = (r * canvasWidth)/cameraWidth;

                    //abajo y arriba varia dependiendo de la rotacion de la camara
                    if(mSensorOrientation == 90){
                        t  = (t * canvasHeight)/cameraHeight;
                        b = (b * canvasHeight)/cameraHeight;
                    }else{
                        t  = canvasHeight - ((t * canvasHeight)/cameraHeight);
                        b = canvasHeight - ((b * canvasHeight)/cameraHeight);
                    }

                    //calculamos la difrencia entre l y r, t y b
                    difx = (r - l)/2;
                    dify = (b - t)/2;

                    //centros en x y y
                    outCenterx = l + (difx);//La diferencia en x es igual la esquina derecha menos la esquina izquierda
                    outCentery = t + (dify);//La diferencia en y es igual la esquina inferior menos la esquina superior

                    //calculamos las esquinas del rectangulo del rostro aumentando sus dimensiones, los calculos dependen de la rotacion del dispositivo

                    switch(deviceRotation){
                        case 0:{
                            left = (int)(outCenterx - (dify * 1.5));
                            right = (int)(outCenterx + (dify * 1.5));
                            top = (int)(outCentery - (difx * 1.5));
                            bottom = (int)(outCentery + (difx * 1.5));
                            break;
                        }
                        case 90:{
                            left = (int)(outCenterx - (difx * 1.5));
                            right = (int)(outCenterx + (difx * 1.5));
                            top = (int)(outCentery - (dify * 1.5));
                            bottom = (int)(outCentery + (dify * 1.5));
                            break;
                        }
                        case 180:{
                            left = (int)(outCenterx - (dify * 1.5));
                            right = (int)(outCenterx + (dify * 1.5));
                            top = (int)(outCentery - (difx * 1.5));
                            bottom = (int)(outCentery + (difx * 1.5));
                            break;
                        }
                        case 270:{
                            left = (int)(outCenterx - (difx * 1.5));
                            right = (int)(outCenterx + (difx * 1.5));
                            top = (int)(outCentery - (dify * 1.5));
                            bottom = (int)(outCentery + (dify * 1.5));
                            break;
                        }
                        default:{
                            left = (int)(outCenterx - (difx * 1.5));
                            right = (int)(outCenterx + (difx * 1.5));
                            top = (int)(outCentery - (dify * 1.5));
                            bottom = (int)(outCentery + (dify * 1.5));
                        }
                    }
                    currentCanvas.drawRect(left, top, right, bottom, greenPaint);
                }
                mSquareView.getHolder().unlockCanvasAndPost(currentCanvas);
            }
        }

    };

    /**
     * ID of the current {@link CameraDevice}.
     */
    private String mCameraId;

    /**
     * An {@link AutoFitTextureView} for camera preview.
     */
    private AutoFitTextureView mTextureView;

    /**
     * camera squares faces
     */
    private FaceSquareView mSquareView;

    /**
     * A {@link CameraCaptureSession } for camera preview.
     */
    private CameraCaptureSession mCaptureSession;

    /**
     * A reference to the opened {@link CameraDevice}.
     */
    private CameraDevice mCameraDevice;

    /**
     * The {@link android.util.Size} of camera preview.
     */
    private Size mPreviewSize;

    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
     */
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            Activity activity = getActivity();
            if (null != activity) {
                activity.finish();
            }
        }

    };

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private HandlerThread mBackgroundThread;

    /**
     * A {@link Handler} for running tasks in the background.
     */
    private Handler mBackgroundHandler;

    /**
     * An {@link ImageReader} that handles still image capture.
     */
    private ImageReader mImageReader;

    /**
     * This is the output file for our picture.
     */
    private File mFile;

    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), mFile));
        }

    };

    /**
     * {@link CaptureRequest.Builder} for the camera preview
     */
    private CaptureRequest.Builder mPreviewRequestBuilder;

    /**
     * {@link CaptureRequest} generated by {@link #mPreviewRequestBuilder}
     */
    private CaptureRequest mPreviewRequest;

    /**
     * The current state of camera state for taking pictures.
     *
     * @see #mCaptureCallback
     */
    private int mState = STATE_PREVIEW;

    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    /**
     * Whether the current camera device supports Flash or not.
     */
    private boolean mFlashSupported;

    /**
     * Orientation of the camera sensor
     */
    private int mSensorOrientation;

    /**
     * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
     */
    private CameraCaptureSession.CaptureCallback mCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {

            switch (mState) {
                case STATE_PREVIEW: {
                    Face face[]=result.get(CaptureResult.STATISTICS_FACES);
                    if (face.length>0){
                        detectedFace = face[0];
                        rectangleFace = detectedFace.getBounds();
                        if(takePictureOfFace){
                            Log.i("AF", "tomar foto");
                            detectedFace = null;
                            takePictureOfFace = false;

                            //Comprobamos si la camara tiene soporte para autofocus o no
                            if (mAutoFocusSupported) {
                                Log.i("AF", "lock focus");
                                lockFocus();
                            } else {
                                Log.i("AF", "capture still");
                                captureStillPicture();
                            }
                        }


                    }
                    else
                    {
                        detectedFace = null;
                    }
                    break;
                }
                case STATE_WAITING_LOCK: {
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    Log.i("AF", "AFON" + afState);
                    if (afState == null) {
                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_INACTIVE == afState) {
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null ||
                                aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            mState = STATE_PICTURE_TAKEN;
                            captureStillPicture();
                        } else {
                            runPrecaptureSequence();
                        }
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    break;
                }
            }
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            process(result);
        }



    };

    /**
     * Shows a {@link Toast} on the UI thread.
     *
     * @param text The message to show
     */
    private void showToast(final String text) {
        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, choose the smallest one that
     * is at least as large as the respective texture view size, and that is at most as large as the
     * respective max size, and whose aspect ratio matches with the specified value. If such size
     * doesn't exist, choose the largest one that is at most as large as the respective max size,
     * and whose aspect ratio matches with the specified value.
     *
     * @param choices           The list of sizes that the camera supports for the intended output
     *                          class
     * @param textureViewWidth  The width of the texture view relative to sensor coordinate
     * @param textureViewHeight The height of the texture view relative to sensor coordinate
     * @param maxWidth          The maximum width that can be chosen
     * @param maxHeight         The maximum height that can be chosen
     * @param aspectRatio       The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth,
                                          int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                        option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    public CameraFragment(){

    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        this.usuario = args.getInt("isUser");
    }

    public static CameraFragment newInstance() {
        return new CameraFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camera_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        view.findViewById(R.id.picture).setOnClickListener(this);
        view.findViewById(R.id.cam_btnBack).setOnClickListener(this);
        mTextureView = view.findViewById(R.id.texture);
        mSquareView = view.findViewById(R.id.face_view);
        mSquareView.setZOrderOnTop(true);
        mSquareView.getHolder().setFormat(
                PixelFormat.TRANSPARENT); //remove black background from view
        greenPaint = new Paint();
        greenPaint.setColor(Color.GREEN);
        greenPaint.setStyle(Paint.Style.STROKE);
        greenPaint.setStrokeWidth(2);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");


        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return;
            }
        }

        mFile = new File(mediaStorageDir.toString() + File.separator + "pic.jpg");


        if(savedInstanceState != null)restoreState(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();
        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
        mSensorManager.registerListener(mOrientationListener, mOrientation, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
        if(mOrientation != null){
            mSensorManager.unregisterListener(mOrientationListener);
        }
    }

    private void requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            new ConfirmationDialog().show(getChildFragmentManager(), FRAGMENT_DIALOG);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ErrorDialog.newInstance("Otros Permisos de Camara")
                        .show(getChildFragmentManager(), FRAGMENT_DIALOG);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * Sets up member variables related to camera.
     *
     * @param width  The width of available size for camera preview
     * @param height The height of available size for camera preview
     */
    @SuppressWarnings("SuspiciousNameCombination")
    private void setUpCameraOutputs(int width, int height) {
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            cameras = manager.getCameraIdList();
            for (String cameraId : cameras) {
                characteristics = manager.getCameraCharacteristics(cameraId);

                // We don't use a front facing camera in this sample.
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    mCameraId = cameraId;
                }

                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }

                // For still image captures, we use the largest available size.
                largest = Collections.max(
                        Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                        new CompareSizesByArea());
                Log.i("SIZE", largest + "\n" + Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)));
                if(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)).contains(Size.parseSize("1280x960")))largest = Size.parseSize("1280x960");
                Log.i("SIZE", largest + "");
                mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
                        ImageFormat.JPEG, /*maxImages*/2);
                mImageReader.setOnImageAvailableListener(
                        mOnImageAvailableListener, mBackgroundHandler);

                // En este caso no ocupamos displayRotation ya que nuestra rotacion esta bloqueada y siempre va a ser 0 pero lo dejo aqui para futuras implementaciones
                displayRotation = activity.getWindowManager().getDefaultDisplay().getRotation();

                //Orientacion del sensor de la camara
                mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);

                boolean swappedDimensions = false;

                switch (displayRotation) {
                    case Surface.ROTATION_0:
                        break;
                    case Surface.ROTATION_90:
                        break;
                    case Surface.ROTATION_180:
                        if(mSensorOrientation == 90){
                            swappedDimensions = true;
                        }
                        if(mSensorOrientation == 270){
                            swappedDimensions = true;
                        }
                        break;
                    case Surface.ROTATION_270:
                        if(mSensorOrientation == 0){
                            swappedDimensions = true;
                        }
                        if(mSensorOrientation == 180){
                            swappedDimensions = true;
                        }
                        break;
                    default:
                        Log.e(TAG, "Display rotation is invalid: " + displayRotation);
                }

                Log.e(TAG, "Display rotation: " + displayRotation);

                Point displaySize = new Point();
                activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
                int rotatedPreviewWidth = width;
                int rotatedPreviewHeight = height;
                int maxPreviewWidth = displaySize.x;
                int maxPreviewHeight = displaySize.y;

                if (swappedDimensions) {
                    rotatedPreviewWidth = height;
                    rotatedPreviewHeight = width;
                    maxPreviewWidth = displaySize.y;
                    maxPreviewHeight = displaySize.x;
                }

                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                    maxPreviewWidth = MAX_PREVIEW_WIDTH;
                }

                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                    maxPreviewHeight = MAX_PREVIEW_HEIGHT;
                }

                // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                        rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                        maxPreviewHeight, largest);

                Log.i("SIZE", "" + mPreviewSize);

                // We fit the aspect ratio of TextureView to the size of preview we picked.
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mTextureView.setAspectRatio(
                            mPreviewSize.getWidth(), mPreviewSize.getHeight());
                    mSquareView.setAspectRatio(
                            mPreviewSize.getWidth(), mPreviewSize.getHeight());
                } else {
                    mTextureView.setAspectRatio(
                            mPreviewSize.getHeight(), mPreviewSize.getWidth());
                    mSquareView.setAspectRatio(
                            mPreviewSize.getHeight(), mPreviewSize.getWidth());
                }

                //Face Detection
                int[] FD =characteristics.get(CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES);
                int maxFD=characteristics.get(CameraCharacteristics.STATISTICS_INFO_MAX_FACE_COUNT);

                if (FD.length>0) {
                    List<Integer> fdList = new ArrayList<>();
                    for (int FaceD : FD
                            ) {
                        fdList.add(FaceD);
                        //Log.d(TAG, "setUpCameraOutputs: FD type:" + Integer.toString(FaceD));
                    }
                    //Log.d(TAG, "setUpCameraOutputs: FD count" + Integer.toString(maxFD));

                    if (maxFD > 0) {
                        mFaceDetectSupported = true;
                        mFaceDetectMode = Collections.max(fdList);
                    }
                }

                // Check if the flash is supported.
                Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                mFlashSupported = available == null ? false : available;

                int[] afAvailableModes = characteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
                Log.i("AF", afAvailableModes.toString() + "\n" + afAvailableModes.length);
                if (afAvailableModes.length == 0 || (afAvailableModes.length == 1
                        && afAvailableModes[0] == CameraMetadata.CONTROL_AF_MODE_OFF)) {
                    Log.i("AF", "AFON");
                    mAutoFocusSupported = false;
                } else {
                    Log.i("AF", "AFOFF");
                    mAutoFocusSupported = true;
                }

                if(mCameraId == null) mCameraId = cameraId;

            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            ErrorDialog.newInstance("Error de Camara")
                    .show(getChildFragmentManager(), FRAGMENT_DIALOG);
        }
    }

    /**
     * Opens the camera specified by {@link CameraFragment#mCameraId}.
     */
    private void openCamera(int width, int height) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
            return;
        }
        setUpCameraOutputs(width, height);
        configureTransform(width, height);
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }

    /**
     * Closes the current {@link CameraDevice}.
     */
    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new {@link CameraCaptureSession} for camera preview.
     */
    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewRequestBuilder
                    = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            if (null == mCameraDevice) {
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            mCaptureSession = cameraCaptureSession;
                            try {
                                // Auto focus should be continuous for camera preview.
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                // Flash is automatically enabled when necessary.
                                setAutoFlash(mPreviewRequestBuilder);

                                setFaceDetect(mPreviewRequestBuilder,mFaceDetectMode);

                                // Finally, we start displaying the camera preview.
                                mPreviewRequest = mPreviewRequestBuilder.build();
                                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        mCaptureCallback, mBackgroundHandler);


                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                @NonNull CameraCaptureSession cameraCaptureSession) {
                            showToast("Failed");
                        }
                    }, null
            );


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    /**
     * Funcion para detectar los rostros de la imagen
     * @param requestBuilder
     * @param faceDetectMode
     */
    private void setFaceDetect(CaptureRequest.Builder requestBuilder , int faceDetectMode){
        if (mFaceDetectSupported){
            requestBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE,faceDetectMode);
        }

    }

    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = getActivity();
        if (null == mTextureView || null == mPreviewSize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    /**
     * Initiate a still image capture.
     */
    private void takePicture() {
        final Activity activity = getActivity();
        progress = new ProgressDialog(getContext(), deviceRotation);
        takePictureOfFace = true;
        progress.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.setMensaje("Validando");

                progress.show();
            }
        });

    }

    /**
     * Lock the focus as the first step for a still image capture.
     */
    private void lockFocus() {
        try {
            // This is how to tell the camera to lock focus.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the lock.
            mState = STATE_WAITING_LOCK;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Run the precapture sequence for capturing a still image. This method should be called when
     * we get a response in {@link #mCaptureCallback} from {@link #lockFocus()}.
     */
    private void runPrecaptureSequence() {
        try {
            // This is how to tell the camera to trigger.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Capture a still picture. This method should be called when we get a response in
     * {@link #mCaptureCallback} from both {@link #lockFocus()}.
     */
    private void captureStillPicture() {
        try {
            final Activity activity = getActivity();
            if (null == activity || null == mCameraDevice) {
                return;
            }
            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());

            // Use the same AE and AF modes as the preview.
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            setAutoFlash(captureBuilder);
            Log.i("AF", "AFON");

            // Orientation
            Log.i("AF", "AFON" + fotoRotation);
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, fotoRotation);

            CameraCaptureSession.CaptureCallback CaptureCallback
                    = new CameraCaptureSession.CaptureCallback() {

                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                               @NonNull CaptureRequest request,
                                               @NonNull TotalCaptureResult result) {


                    showToast("Saved: " + mFile);
                    //Notificamos al media scanner que se ha creado un archivo
                    MediaScannerConnection.scanFile (activity, new String[] {mFile.toString()}, null, null);
                    Bitmap bm = BitmapFactory.decodeFile(mFile.getPath());

                    Matrix matrix = new Matrix();
                    matrix.postScale(1f, 1f);
                    //matrix.postRotate(fotoRotation);

                    Log.i("AF", "tamaños" + bm.getWidth()
                            + "\n" + bm.getHeight()
                            + "\n" + outLeft
                            + "\n" + outWidth
                            + "\n" + outTop
                            + "\n" + outHeight
                    );

                    //write the bytes in file
                    try {
                        Bitmap bitmapChico = Bitmap.createBitmap(bm, outLeft, outTop,
                                outWidth, outHeight, matrix, true);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bitmapChico.compress(Bitmap.CompressFormat.PNG, 0 , bos);
                        byte[] bitmapdata = bos.toByteArray();
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +
                                File.separator + "MyCameraApp" + File.separator + "IMG" + (System.currentTimeMillis()/1000) + ".png");
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(bitmapdata);
                        fos.flush();
                        fos.close();
                        MediaScannerConnection.scanFile (activity, new String[] {file.toString()}, null, null);
                    }
                    catch(IOException ioe){

                    }
                    /*Bitmap bitmapChico = Bitmap.createBitmap(bm, 0, 0,
                            bm.getWidth(), bm.getHeight(), matrix, false);

                    bm.recycle();*/


                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                    byte[] b = baos.toByteArray();
                    String base64Image = Base64.encodeToString(b, Base64.NO_WRAP);
                    Log.d(TAG, mFile.toString());
                    Log.d(TAG, base64Image);

                    /*Map<String, String> headers = new HashMap<>();
                    headers.put("Content-type", "application/json");
                    JSONObject json = new JSONObject();
                    try{
                        json.put("fecha", "2018-01-10");
                        json.put("ubicacion", "ubicacion");
                        json.put("asistenciasuserId", usuario);
                        json.put("imagen", base64Image);
                    }
                    catch(JSONException jsone){

                    }

                    Comunicaciones com = new Comunicaciones(getContext());
                    com.getSomethingJSON(
                            Constants.url + Constants.asistencia,
                            Request.Method.POST,
                            json,
                            headers,
                            CameraFragment.this
                    );*/
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.setValid();
                        }
                    });
                    unlockFocus();
                }
            };

            mCaptureSession.stopRepeating();
            mCaptureSession.abortCaptures();
            mCaptureSession.capture(captureBuilder.build(), CaptureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the JPEG orientation from the specified screen rotation.
     *
     * @param rotation The screen rotation.
     * @return The JPEG orientation (one of 0, 90, 270, and 360)
     */
    private int getOrientation(int rotation) {
        // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
        // We have to take that into account and rotate JPEG properly.
        // For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
        // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
        return rotation% 360;
    }

    /**
     * Unlock the focus. This method should be called when still image capture sequence is
     * finished.
     */
    private void unlockFocus() {
        try {
            // Reset the auto-focus trigger
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            setAutoFlash(mPreviewRequestBuilder);
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
            // After this, the camera will go back to the normal state of preview.
            mState = STATE_PREVIEW;
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.picture: {
                Log.i("AF", "presionado el boton");
                takePicture();
                /*if(usuario > 0){
                    takePicture();
                }else{
                    Activity activity = getActivity();
                    if (null != activity) {
                        new AlertDialog.Builder(activity)
                                .setMessage("Inserta tu ID")
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                    }
                }*/
                break;
            }
            case R.id.cam_btnBack:{
                mListener.onCallBack(usuario);
                break;
            }
        }
    }

    private void setAutoFlash(CaptureRequest.Builder requestBuilder) {
        if (mFlashSupported) {
            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        }
    }

    @Override
    public void mostrarDatos(String datos) {

    }

    @Override
    public void mostrarDatos(JSONObject datos) {
        Log.i("DATA", datos.toString());
        final Activity activity = getActivity();
        if(datos.has("validado")){
            try {
                boolean val = (boolean) datos.get("validado");
                if (val) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.setValid();
                        }
                    });
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.setInvalid();
                        }
                    });
                }
                unlockFocus();
            }
            catch(JSONException jsone)
            {

            }
        }

    }

    /**
     * Saves a JPEG {@link Image} into the specified {@link File}.
     */
    private static class ImageSaver implements Runnable {

        /**
         * The JPEG image
         */
        private final Image mImage;
        /**
         * The file we save the image into.
         */
        private final File mFile;

        ImageSaver(Image image, File file) {
            mImage = image;
            mFile = file;
        }

        @Override
        public void run() {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    /**
     * Shows an error message dialog.
     */
    public static class ErrorDialog extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    })
                    .create();
        }

    }

    /**
     * Shows OK/Cancel confirmation dialog about camera permission.
     */
    public static class ConfirmationDialog extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Fragment parent = getParentFragment();
            return new AlertDialog.Builder(getActivity())
                    .setMessage("Permisos")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            parent.requestPermissions(new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA_PERMISSION);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Activity activity = parent.getActivity();
                                    if (activity != null) {
                                        activity.finish();
                                    }
                                }
                            })
                    .create();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        saveState(outState);
        super.onSaveInstanceState(outState);
    }

    /*private void saveStateToArguments(Bundle savedState) {
        if (getView() != null)
            savedState = saveState();
        if (savedState != null) {
            Bundle b = getArguments();
            if (b != null)
                b.putBundle("internalSavedViewState8954201239547", savedState);
        }
    }*/

    private void saveState(Bundle state) {
        // For Example
        onSaveState(state);
    }

    protected void onSaveState(Bundle outState) {

    }

    private void restoreState(Bundle savedState) {
        onRestoreState(savedState);
    }

    protected void onRestoreState(Bundle savedInstanceState) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentAsistencias.OnFragmentInteractionListener) {
            mListener = (CameraFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void onCallBack(int UserId);
    }

}
