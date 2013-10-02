package com.abir.photolotto;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera.CameraInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectEffectActivity extends Activity {
	
	private final static int 	FILTER_NO_ONE	= 0;
	private final static int 	FILTER_NO_TWO	= 1;
	private final static int 	FILTER_NO_THREE	= 2;
	private final static int 	FILTER_NO_FOUR	= 3;
	private final static int 	FILTER_NO_FIVE	= 4;
	private final static int 	FILTER_NO_SIX	= 5;
	private final static int 	FILTER_TOTAL	= 6;
	
	private GridView			gridview		= null;
	private final String		tag				= this.getClass().getSimpleName();
	private ArrayList<Bitmap>	lstEffectImages	= new ArrayList<Bitmap>();
	private TextView			textViewEffectName;
	private ImageView			imageView;
	private ImageEffectAdapter	imageEffectAdapter;
	ProgressDialog				mDialog;
	ImageLoader					mImageLoader;
	Bitmap						bitmapIn		= SharedImageObjects.mBitmap;
	Bitmap						bitmapIn2		= null;
	Bitmap						tmpBitmap		= null;
	Bitmap						bitmapOut		= null;
	private int					nSelectedItem	= 0;

	private class LoadImages extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			lstEffectImages.clear();
			lstEffectImages.add(tmpBitmap);
			lstEffectImages.add(Utils.hefeImage(bitmapIn));
			lstEffectImages.add(Utils.earlybirdImage(SelectEffectActivity.this, bitmapIn));
			lstEffectImages.add(Utils.xProImage(SelectEffectActivity.this, bitmapIn));
			lstEffectImages.add(Utils.inkwellImage(SelectEffectActivity.this, bitmapIn));
			lstEffectImages.add(Utils.nashvilleImage(SelectEffectActivity.this, bitmapIn));
			/* for (int i = 0; i < 5; i++) { lstEffectImages.remove(0); } */

			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			mDialog.dismiss();
			imageEffectAdapter.notifyDataSetChanged();
			imageView.setImageBitmap(lstEffectImages.get(0));
			gridview.performItemClick(null, FILTER_NO_SIX, 0);
			gridview.performItemClick(null, FILTER_NO_FIVE, 0);
			gridview.performItemClick(null, FILTER_NO_FOUR, 0);
			gridview.performItemClick(null, FILTER_NO_THREE, 0);
			gridview.performItemClick(null, FILTER_NO_TWO, 0);
			gridview.performItemClick(null, FILTER_NO_ONE, 0);
		}

		@Override
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(SelectEffectActivity.this, null, "Applying effects...", true);
			mDialog.setCancelable(false);
		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {

		for (int i = 0; i < FILTER_TOTAL; i++) {
			if (i == nSelectedItem)
				continue;
			Bitmap bmp = lstEffectImages.get(i);
			bmp.recycle();
			bmp = null;
		}
		lstEffectImages.clear();
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_effects);
		imageView = (ImageView) findViewById(R.id.full_image_view);
		textViewEffectName = (TextView) findViewById(R.id.textViewEffectName);
		textViewEffectName.setText("Normal");
		bitmapIn2 = Utils.rotateBitmap(BitmapFactory.decodeByteArray(SharedImageObjects.mData, 0, SharedImageObjects.mData.length), SharedImageObjects.mSelectedCamera == CameraInfo.CAMERA_FACING_BACK ? 90 : 270);

		tmpBitmap = Utils.convertToMutable(bitmapIn2);
		imageView.setImageBitmap(tmpBitmap);
		gridview = (GridView) findViewById(R.id.gridViewEffects);
		gridview.setGravity(Gravity.CENTER);
		gridview.setNumColumns(FILTER_TOTAL);
		imageEffectAdapter = new ImageEffectAdapter(this, lstEffectImages);
		gridview.setAdapter(imageEffectAdapter);

		imageEffectAdapter.notifyDataSetChanged();
		mImageLoader = new ImageLoader(this);

		gridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				bitmapOut = lstEffectImages.get(position);
				int h = bitmapOut.getHeight();
				int w = bitmapOut.getWidth();
				Canvas canvas = new Canvas(bitmapOut);
				canvas.drawBitmap(bitmapOut, 0f, 0f, null);
				Drawable drawable = new BitmapDrawable(getResources(), mImageLoader.getBitmap(SharedImageObjects.mSelectedImageUrl));
				drawable.setBounds(0, 0, w, h);
				drawable.draw(canvas);
				imageView.setImageBitmap(bitmapOut);

				nSelectedItem = position;
				for (int i = 0; i < gridview.getChildCount(); i++) {
					ImageView selImageView = (ImageView) gridview.findViewWithTag(i);
					if (i == position) {
						selImageView.setBackgroundColor(0xff00ff00);
					} else {
						selImageView.setBackgroundColor(0x00000000);
					}

				}
				switch (position) {
					case FILTER_NO_ONE :
						textViewEffectName.setText("Normal");
						break;
					case FILTER_NO_TWO :
						textViewEffectName.setText("Sydney");
						break;
					case FILTER_NO_THREE :
						textViewEffectName.setText("London");
						break;
					case FILTER_NO_FOUR :
						textViewEffectName.setText("New York");
						break;
					case FILTER_NO_FIVE :
						textViewEffectName.setText("Paris");
						break;
					case FILTER_NO_SIX :
						textViewEffectName.setText("Tokyo");
						break;
					default :
						break;
				}
			}
		});
		Button button = (Button) findViewById(R.id.buttonSelectEffect);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				SharedImageObjects.mBitmapWithEffect = (bitmapOut == null) ? bitmapIn : bitmapOut;
				Intent intent = new Intent(SelectEffectActivity.this, ShareActivity.class);
				startActivity(intent);
				SelectEffectActivity.this.finish();
			}
		});

		Button buttonBack = (Button) findViewById(R.id.buttonSelectEffectBack);
		buttonBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SelectEffectActivity.this.onBackPressed();
			}
		});
		new LoadImages().execute();
	}
}
