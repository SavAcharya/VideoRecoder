package com.mphasis.videorecoder.common.media.test2;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	private final Context mContext;
	private SurfaceHolder mHolder;
	private Camera mCamera;

	public CameraPreview(Context context, Camera camera) {
		super(context);
		mContext=context;
		mCamera = camera;
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceCreated(SurfaceHolder holder) {


		try {
			// create the surface and start camera preview
			if (mCamera == null) {
				setCameraDisplayOrientation(mCamera);
				mCamera.setPreviewDisplay(holder);

				mCamera.startPreview();
			}
		} catch (IOException e) {
			Log.d(VIEW_LOG_TAG, "Error setting camera preview: " + e.getMessage());
		}
	}

	public void refreshCamera(Camera camera) {
		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}
		// stop preview before making changes
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}
		// set preview size and make any resize, rotate or
		// reformatting changes here
		// start preview with new settings
		setCamera(camera);
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();
		} catch (Exception e) {
			Log.d(VIEW_LOG_TAG, "Error starting camera preview: " + e.getMessage());
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.

		setCameraDisplayOrientation(mCamera);
		refreshCamera(mCamera);
	}

	public void setCamera(Camera camera) {
		//method to set a camera instance
		mCamera = camera;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		// mCamera.release();

	}

	public void setCameraDisplayOrientation(android.hardware.Camera camera) {
		Camera.Parameters parameters = camera.getParameters();

		android.hardware.Camera.CameraInfo camInfo =
				new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(getBackFacingCameraId(), camInfo);


		Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int rotation = display.getRotation();
		int degrees = 0;
		switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
		}

		int result;
		if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (camInfo.orientation + degrees) % 360;
			result = (360 - result) % 360;  // compensate the mirror
		} else {  // back-facing
			result = (camInfo.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}




	private int getBackFacingCameraId() {
		int cameraId = -1;
		// Search for the front facing camera
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			Camera.CameraInfo info = new Camera.CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {

				cameraId = i;
				break;
			}
		}
		return cameraId;
	}
}