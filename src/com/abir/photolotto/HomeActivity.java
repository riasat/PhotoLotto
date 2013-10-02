package com.abir.photolotto;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class HomeActivity extends Activity implements LocationListener, ReadFeedEventHandler {

	private LocationManager	mLocationManager;
	private Location		mLocation;
	private Button			mButtonStart, mButtonReload;
	private boolean			bButtonState	= false;
	private ImageView		mImageViewLoading;
	private ReadFeed		mReadFeedEvent = new ReadFeed();
	private ImageView		mImageViewAnimation;
	
	private void changeButtonState() {
		mButtonStart.setEnabled(bButtonState);
		mButtonReload.setVisibility(bButtonState ? View.GONE : View.VISIBLE);
		mButtonReload.setEnabled(!bButtonState);
	}

	@Override
    /**
     * This method is called whenever the Activity becomes visible or invisible to the user.
     * During this method call its possible to start the animation.
     */
	public void onWindowFocusChanged (boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		AnimationDrawable frameAnimation = (AnimationDrawable) mImageViewAnimation.getBackground();
		if(hasFocus) {
			frameAnimation.start();
		} else {
			frameAnimation.stop();
		}
	}
	
	@Override
	protected void onDestroy() {
		AnimationDrawable frameAnimation = (AnimationDrawable) mImageViewAnimation.getBackground();
		frameAnimation.stop();
		mImageViewAnimation.setBackgroundResource(R.drawable.image_home);
		mImageViewAnimation = null;
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if(mLocation != null) {
            // Do something with the recent location fix otherwise wait for the update below
        }
        else {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

		setContentView(R.layout.activity_home);
		mImageViewAnimation = (ImageView) findViewById(R.id.imageViewAnimation);
		mImageViewAnimation.setBackgroundResource(R.drawable.animation);

		mImageViewLoading = (ImageView) findViewById(R.id.imageViewLoading);
		mButtonStart = (Button) findViewById(R.id.button_home_start);
		mButtonReload = (Button) findViewById(R.id.button_home_reload);
		
		mButtonReload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				startReadFeed();
				mButtonReload.setVisibility(View.GONE);
			}
		});
	}

	private void startReadFeed() {
		mReadFeedEvent = new ReadFeed();
		mReadFeedEvent.setEventHandler(HomeActivity.this);
		mReadFeedEvent.setLocation(mLocation);
		mReadFeedEvent.execute("");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			buildAlertMessageNoGps();
		} else {
			startReadFeed();
		}
	}

	/** Called when buttonStart is clicked, declared in the activity_home.xml
	 * 
	 * @param view
	 *            Associated view of the buttonStart  */
	public void onClickStart(View view) {
		Intent intent = new Intent(HomeActivity.this, SelectEventActivity.class);
		Bundle bundle = new Bundle();
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder	.setMessage("Yout GPS seems to be disabled, do you want to enable it?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						dialog.cancel();
						HomeActivity.this.finish();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public void onPreExecute() {

	}

	@Override
	public void onPostExecute(int ns) {
		mImageViewLoading.setImageResource(R.drawable.loading_pixta);
		bButtonState = true;
		changeButtonState();
	}

	@Override
	public void onFailure() {
		bButtonState = false;
		changeButtonState();
	}

	@Override
	protected void onPause() {
		mLocationManager.removeUpdates(this);
		super.onPause();
	}

	@Override
	public void onLocationChanged(Location location) {
		mLocation = location;
		mReadFeedEvent.setLocation(mLocation);
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
}