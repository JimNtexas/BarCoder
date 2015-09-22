package com.grayraven.barcoder;
/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.hardware.Camera;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CameraActivityFragement extends android.support.v4.app.Fragment
        implements SurfaceHolder.Callback {


    private static final String TAG = "CameraActivityFragement";

    Camera mCamera;
    Context mContext;
    List<Camera.Size> mSupportedPreviewSizes;
    SurfaceHolder mHolder;
    SurfaceView mSurfaceView;


    public static CameraActivityFragement newInstance() {
        return new CameraActivityFragement();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

     @Override
      public void onViewCreated(final View view, Bundle savedInstanceState) {
  //        view.findViewById(R.id.picture).setOnClickListener(this);
    //      view.findViewById(R.id.info).setOnClickListener(this);
         mSurfaceView = (SurfaceView) view.findViewById(R.id.surface_view);

       /*  rawCallback = new PictureCallback() {
             public void onPictureTaken(byte[] data, Camera camera) {
                 Log.d("Log", "onPictureTaken - raw");
             }
         };*/
     }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        Log.d(TAG, "onActivityCreated");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        setCamera(mCamera);

        //startBackgroundThread();

        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
      /*  if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }*/
    }

    @Override
    public void onPause() {
        stopPreviewAndFreeCamera();
        Log.d(TAG, "onPause");
        super.onPause();
    }

    public void setCamera(Camera camera) {
        stopPreviewAndFreeCamera();

        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        safeCameraOpen();
        if (mCamera != null) {
            List<Camera.Size> localSizes = mCamera.getParameters().getSupportedPreviewSizes();
            mSupportedPreviewSizes = localSizes;
           // mPreview = new Preview(mContext);
            try {
                mCamera.setPreviewDisplay(mHolder);
            } catch (IOException e) {
                Log.e(TAG, "could not set preview display");
                return;
            }
            mCamera.startPreview();
        }
    }

    /**
     * When this function returns, mCamera will be null.
     */
    private void stopPreviewAndFreeCamera() {

        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }



    private boolean safeCameraOpen() {
        boolean qOpened = false;

        try {
            stopPreviewAndFreeCamera();

            //get index of front camera
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            int count = Camera.getNumberOfCameras();
            for(int i=0; i < count; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                   mCamera = Camera.open(i);
                   break;
                }
            }
            qOpened = (mCamera != null);
        } catch (Exception e) {
            Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
        }

        return qOpened;
    }

    public void startCameraPreview() {
        try{
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
     /*   mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());*/
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
       /* mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "sufaceCreated");
        startCameraPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "sufaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "sufaceDestroyed");

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
     /*   Activity activity = getActivity();
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
        mTextureView.setTransform(matrix);*/
    }

    /**
     * Begin the preview of the camera input.
     */


    /**
     * Initiate a still image capture.
     */
    private void takePicture() {
        lockFocus();
    }

    /**
     * Lock the focus as the first step for a still image capture.
     */
    private void lockFocus() {
     /*   try {
            // This is how to tell the camera to lock focus.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the lock.
            mState = STATE_WAITING_LOCK;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }*/
    }

    private void runPrecaptureSequence() {
      /*  try {
            // This is how to tell the camera to trigger.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }*/
    }


    /**
     * Unlock the focus. This method should be called when still image capture sequence is
     * finished.
     */
    private void unlockFocus() {
      /*  try {
            // Reset the auto-focus trigger
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
            // After this, the camera will go back to the normal state of preview.
            mState = STATE_PREVIEW;
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }*/
    }

/*    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.picture: {
                takePicture();
                break;
            }
            case R.id.info: {
                Activity activity = getActivity();
                if (null != activity) {
                    new AlertDialog.Builder(activity)
                            .setMessage(R.string.info_message)
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
                break;
            }
        }
    }*/

    /**
     * Saves a JPEG {@link Image} into the specified {@link File}.
     */
  /* *//*     private static class ImageSaver implements Runnable {

        *//**
         * The JPEG image
         *//*
        private final Image mImage;
        *//**
         * The file we save the image into.
         *//*
        private final File mFile;

        public ImageSaver(Image image, File file) {
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
        }*//*



        *//**
         * Compares two {@code Size}s based on their areas.
         *//*
      *//*  static class CompareSizesByArea implements Comparator<Size> {

            @Override
            public int compare(Size lhs, Size rhs) {
                // We cast here to ensure the multiplications won't overflow
                return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                        (long) rhs.getWidth() * rhs.getHeight());
            }

        }*//*

        *//**
         * Shows an error message dialog.
         *//*
        public static class ErrorDialog extends DialogFragment {

            private static final String ARG_MESSAGE = "message";

            public static ErrorDialog newInstance(String message) {
                ErrorDialog dialog = new ErrorDialog();
                Bundle args = new Bundle();
                args.putString(ARG_MESSAGE, message);
                dialog.setArguments(args);
                return dialog;
            }

        }
*/
}
