package com.android.facebook;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class FacebookActivity extends Activity implements OnClickListener{
	
	private static final String TAG = "FacebookActivity";
	// Instance of Facebook Class
		private Facebook facebook;
		private AsyncFacebookRunner mAsyncRunner;
		private SharedPreferences mPrefs;
		
		private static final String APP_ID = "469543539748993";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Setup Facebook button and install listener
		facebook = new Facebook(APP_ID);
		mAsyncRunner = new AsyncFacebookRunner(facebook);
	}

	/**
	 * Facebook Login.
	 */
	public void loginToFacebook() {
		mPrefs = getPreferences(MODE_PRIVATE);
		String access_token = mPrefs.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);

		if (access_token != null) {
			facebook.setAccessToken(access_token);
		}
		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}
		if (!facebook.isSessionValid()) {
			facebook.authorize(this,
					new String[] { "email", "publish_stream" },
					new DialogListener() {

						@Override
						public void onCancel() {
							// Returns back to the screen it was launched
						}

						@Override
						public void onComplete(Bundle values) {
							SharedPreferences.Editor editor = mPrefs.edit();
							editor.putString("access_token",
									facebook.getAccessToken());
							editor.putLong("access_expires",
									facebook.getAccessExpires());
							editor.commit();
							
						}

						@Override
						public void onError(DialogError error) {
							Toast.makeText(
									FacebookActivity.this,
									"An unknown error has occured, please try again!",
									Toast.LENGTH_LONG).show();
							Log.i(TAG, "Facebook error occured on Enthuse end");
						}

						@Override
						public void onFacebookError(FacebookError e) {
							Toast.makeText(
									FacebookActivity.this,
									"An unknown error has occured, please try again!",
									Toast.LENGTH_LONG).show();
							Log.i(TAG, "Facebook error occured on Facebook end");
						}
					});
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

}
