package com.abir.photolotto;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

public class SelectCameraOverlayActivity extends Activity {
	private final String tag = SelectCameraOverlayActivity.class
			.getSimpleName();
	private Camera mCamera;
	private CameraPreview mPreview;
	private FrameLayout mFrameLayoutPreview;
	ImageView mImageViewOverlay;
	private int mNumberOfImages;
	private int mSelectedImageNumber;
	public ImageLoader mImageLoader;
	private ArrayList<String> mListOverlayUrls = new ArrayList<String>();

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overlay);

		mImageViewOverlay = (ImageView) findViewById(R.id.imageViewOverlay);
		mImageLoader = new ImageLoader(this);
		EventModel em = EventModel.getSelectedEvent();
		mNumberOfImages = em.getnNumberOfOverlay();

		if (mNumberOfImages >= 1)
			mListOverlayUrls.add(em.getsImageOverlay1());
		if (mNumberOfImages >= 2)
			mListOverlayUrls.add(em.getsImageOverlay2());
		if (mNumberOfImages >= 3)
			mListOverlayUrls.add(em.getsImageOverlay3());
		if (mNumberOfImages >= 4)
			mListOverlayUrls.add(em.getsImageOverlay4());
		if (mNumberOfImages >= 5)
			mListOverlayUrls.add(em.getsImageOverlay5());
		// getWindow().setFormat(PixelFormat.UNKNOWN);

		// Create our Preview view and set it as the content of our activity.
		mFrameLayoutPreview = (FrameLayout) findViewById(R.id.camera_preview);

		mPreview = new CameraPreview(this);
		mCamera = mPreview.getCamera();
		mFrameLayoutPreview.addView(mPreview);

		GridView gridview = (GridView) findViewById(R.id.gridViewOverlay);
		gridview.setGravity(Gravity.CENTER);
		gridview.setNumColumns(mNumberOfImages);
		gridview.setAdapter(new ImageAdapter(this, mListOverlayUrls));
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// mImageViewOverlay.setImageBitmap(mImageLoader.getBitmap(mListOverlayUrls.get(position)));
				mSelectedImageNumber = position;
				mImageLoader.displayImage(mListOverlayUrls.get(position),
						mImageViewOverlay);
			}
		});

		final Button buttonFlash = (Button) findViewById(R.id.buttonFlash);
		buttonFlash.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				int n = mPreview.toggleFlash();
				if (n == -1)
					return;
				buttonFlash
						.setBackgroundResource(n == 0 ? R.drawable.takephoto_flashon
								: R.drawable.takephoto_flashoff);
			}
		});

		final Button buttonToggleCamera = (Button) findViewById(R.id.buttonToggleCamera);
		buttonToggleCamera.setOnClickListener(new OnClickListener() {
			boolean bBackCamera = false;

			@Override
			public void onClick(View view) {
				final int n = bBackCamera ? 0 : 1;
				if (n == 0) {
					buttonToggleCamera
							.setBackgroundResource(R.drawable.takephoto_switchcam1);
				} else {
					buttonToggleCamera
							.setBackgroundResource(R.drawable.takephoto_switchcam);
				}
				mPreview.toggleCamera(n);
				SharedImageObjects.mSelectedCamera = n;
				bBackCamera = !bBackCamera;
				mCamera = mPreview.getCamera();
				buttonFlash
						.setBackgroundResource(R.drawable.takephoto_flashoff);
			}
		});

		final Button buttonCapture = (Button) findViewById(R.id.buttonCapture);
		buttonCapture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (mCamera != null)
					mCamera.takePicture(cameraShutterCallback,
							cameraPictureCallbackRaw, cameraPictureCallbackJpeg);
			}
		});

		mImageLoader.displayImage(em.getsImageOverlay1(), mImageViewOverlay);
		/*
		FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) mPreview
				.getLayoutParams();
		params.height = getScreenWidth();
		params.width = getScreenWidth();
		mPreview.setLayoutParams(params);
		*/
	}

	private int getScreenWidth() {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		return displaymetrics.widthPixels;
	}

	ShutterCallback cameraShutterCallback = new ShutterCallback() {
		@Override
		public void onShutter() {
			// TODO Auto-generated method stub
		}
	};

	PictureCallback cameraPictureCallbackRaw = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
		}
	};

	PictureCallback cameraPictureCallbackJpeg = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			SharedImageObjects.mData = data;
			SharedImageObjects.mBitmap = Utils
					.rotateBitmap(
							BitmapFactory.decodeByteArray(data, 0, data.length),
							SharedImageObjects.mSelectedCamera == CameraInfo.CAMERA_FACING_BACK ? 90
									: 270);
			SharedImageObjects.mSelectedImageUrl = mListOverlayUrls
					.get(mSelectedImageNumber);

			Intent intent = new Intent(SelectCameraOverlayActivity.this,
					SelectEffectActivity.class);
			startActivity(intent);
			SelectCameraOverlayActivity.this.finish();
		}
	};
}