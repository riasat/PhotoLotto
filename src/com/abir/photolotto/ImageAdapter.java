package com.abir.photolotto;

import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private Activity			mActivity;
	private ImageLoader			mImageLoader;
	private ArrayList<String>	mListOverlayUrls	= new ArrayList<String>();

	public ImageAdapter(Activity a, ArrayList<String> lstOverlayImages) {
		mActivity = a;
		this.mListOverlayUrls = lstOverlayImages;
		mImageLoader = new ImageLoader(mActivity.getApplicationContext());
	}

	public int getCount() {
		return mListOverlayUrls.size();
	}

	public Object getItem(int position) {
		return mListOverlayUrls.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) { // if it's not recycled, initialize some attributes
			imageView = new ImageView(mActivity);
			imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(5, 5, 5, 5);
		} else {
			imageView = (ImageView) convertView;
		}
		//imageView.setImageBitmap(mImageLoader.getBitmap(mListOverlayUrls.get(position)));
		mImageLoader.displayImage(mListOverlayUrls.get(position), imageView);
		return imageView;
	}

}