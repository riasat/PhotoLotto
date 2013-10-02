package com.abir.photolotto;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;


public class CInstagram {
	
	private static Activity mActivity = null;
	
	public static void setActivity(Activity activity) {
		mActivity = activity;
	}
	/*
	 * This method use PackageManager Class to check for instagram package.
	 * */
	public static boolean isInstagramInstalled() {

		boolean app_installed = false;
		try {
			ApplicationInfo info = mActivity.getPackageManager().getApplicationInfo("com.instagram.android", 0);
			app_installed = true;
		} catch (PackageManager.NameNotFoundException e) {
			app_installed = false;
		}
		return app_installed;
	}
	
	/* this mathod actually share image to Instagram, It accept Uri */
	public static void shareInstagram(Uri uri){
		
		/// Method 1 : Optimize
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType("image/*"); // set mime type 
		shareIntent.putExtra(Intent.EXTRA_STREAM,uri); // set uri 
		shareIntent.setPackage("com.instagram.android");
		mActivity.startActivityForResult(shareIntent, Utils.REQUEST_ACTIVITY_INSTAGRAM);

	}
}
