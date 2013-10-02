package com.abir.photolotto;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageEffectAdapter extends BaseAdapter {

	private Activity activity;
	public ImageLoader imageLoader;
	private ArrayList<Bitmap> lstImageEffect = new ArrayList<Bitmap>();

	public ImageEffectAdapter(Activity a, ArrayList<Bitmap> lstImages) {
		activity = a;
		this.lstImageEffect = lstImages;
	}

	@Override
	public int getCount() {
		return lstImageEffect.size();
	}

	@Override
	public Object getItem(int position) {
		return lstImageEffect.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) { // if it's not recycled, initialize some
									// attributes
			imageView = new ImageView(activity);
			imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.setPadding(5, 5, 5, 5);
		} else {
			imageView = (ImageView) convertView;
		}
		imageView.setTag(position);
		imageView.setImageBitmap(lstImageEffect.get(position));
		return imageView;
	}

}
