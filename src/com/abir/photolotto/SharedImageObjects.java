package com.abir.photolotto;

import android.graphics.Bitmap;
import android.hardware.Camera.CameraInfo;

public class SharedImageObjects {
	public static String mKey;
	public static byte[] mData;
	public static String mSelectedImageUrl;
	public static Bitmap mBitmap;
	public static Bitmap mBitmapWithEffect;
	public static int mSelectedCamera = CameraInfo.CAMERA_FACING_BACK;;
}
