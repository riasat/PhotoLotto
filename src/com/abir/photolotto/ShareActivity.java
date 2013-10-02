package com.abir.photolotto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.android.facebook.SampleUploadListener;
import com.android.twitter.TwitterActivity;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class ShareActivity extends Activity {

	private AmazonS3Client		s3Client		= new AmazonS3Client(new BasicAWSCredentials(Constants.ACCESS_KEY_ID, Constants.SECRET_KEY));
	private int					nSelectedIndex	= -1;
	private Facebook			facebook;
	private AsyncFacebookRunner	mAsyncRunner;
	private SharedPreferences	mPrefs;
	private static final String	APP_ID			= "469543539748993";
	private static final String	TAG				= "ShareActivity";

	private void completeAction(int n) {

		switch (n) {
			case 0 :
				loginToFacebook();
				break;
			case 1 :
				Intent i = new Intent(ShareActivity.this, TwitterActivity.class);
				startActivityForResult(i, Utils.REQUEST_ACTIVITY_TWITTER);
				break;
			case 2:
				galleryAddPic(Utils.getFileFromBitmap(SharedImageObjects.mBitmapWithEffect));
				Toast.makeText(ShareActivity.this, "Photo saved in gallery", Toast.LENGTH_SHORT).show();
				postHtmlActivity();
				break;
			case 3:
			{
				Intent intent = new Intent(ShareActivity.this, EmailActivity.class);
				startActivityForResult(intent, Utils.REQUEST_ACTIVITY_EMAIL);
			}
			break;
			case 4:
			{
				CInstagram.setActivity(this);
				if(CInstagram.isInstagramInstalled()){
					CInstagram.shareInstagram(
							Uri.fromFile(Utils.getFileFromBitmap(SharedImageObjects.mBitmapWithEffect))
							);
				} else {
					new AlertDialog.Builder(this)
				    .setTitle("Instagram not found")
				    .setCancelable(false)
				    .setMessage("You will need to install the Instagram app to share this image, please download it from GooglePlay")
				    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				        	postHtmlActivity();
				        }
				     })
				    /*.setNegativeButton("No", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				            // do nothing
				        }
				     })*/
				     .show();
					//Toast.makeText(ShareActivity.this, "Instagram not installed", Toast.LENGTH_SHORT).show();
				}
			}
				break;
			default :
				break;
		}
	}

	/** Facebook Login. */
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
			facebook.authorize(this, new String[]{"email", "publish_stream"}, new DialogListener() {
				@Override
				public void onCancel() {
					// Returns back to the screen it was launched
				}

				@Override
				public void onComplete(Bundle values) {
					SharedPreferences.Editor editor = mPrefs.edit();
					editor.putString("access_token", facebook.getAccessToken());
					editor.putLong("access_expires", facebook.getAccessExpires());
					editor.commit();
					publishPhoto();
				}

				@Override
				public void onError(DialogError error) {
					Toast.makeText(ShareActivity.this, "An unknown error has occured, please try again!", Toast.LENGTH_LONG).show();
					Log.i(TAG, "Facebook error occured on PIXTA end");

				}

				@Override
				public void onFacebookError(FacebookError e) {
					Toast.makeText(ShareActivity.this, "An unknown error has occured, please try again!", Toast.LENGTH_LONG).show();
					Log.i(TAG, "Facebook error occured on Facebook end");
				}
			});
			facebook.setAccessToken(access_token);
		} else {
			publishPhoto();
			facebook.setAccessToken(access_token);
		}
	}

	public void publishPhoto() {
		String response = "";
		byte[] data = null;
		try {
			Bitmap bi = SharedImageObjects.mBitmapWithEffect;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bi.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			data = baos.toByteArray();

			Bundle params = new Bundle();
			params.putString(Facebook.TOKEN, facebook.getAccessToken());
			params.putString("message", EventModel.getSelectedEvent().getsFacebookMessage());
			params.putString("filename", "fb");
			params.putByteArray("picture", data);
			params.putString("caption", EventModel.getSelectedEvent().getsFacebookMessage());

			AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
			mAsyncRunner.request("me/photos", params, "POST", new SampleUploadListener(), null);
			mAsyncRunner.request(response, new SampleUploadListener());
			Log.e("post result", response);
			Toast.makeText(this, "Posted Successfully", Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(this, "Posting Failed", Toast.LENGTH_LONG).show();
			e.printStackTrace();

		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	public void postHtmlActivity() {
		if (!EventModel.getSelectedEvent().getsHtmlAfter().isEmpty()) {
			Intent intent = new Intent(ShareActivity.this, BeforeHtmlActivity.class);
			intent.putExtra("requestCode", Utils.REQUEST_ACTIVITY_AFTER_HTML);
			startActivityForResult(intent, Utils.REQUEST_ACTIVITY_AFTER_HTML);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case Utils.REQUEST_ACTIVITY_BEFORE_HTML :
				completeAction(nSelectedIndex);
				break;
			case Utils.REQUEST_ACTIVITY_AFTER_HTML : {
				switch (resultCode) {
					case Utils.REQUEST_ACTIVITY_DONE :
						finish();
						break;

					default :
						break;
				}
				break;
			}
			case Utils.REQUEST_ACTIVITY_EMAIL :
			case Utils.REQUEST_ACTIVITY_TWITTER :
			case Utils.REQUEST_ACTIVITY_INSTAGRAM :
				postHtmlActivity();
				break;
			default :
				break;
		}
	}

	private void showCustomDialog() {
		View dialogView = getLayoutInflater().inflate(R.layout.custom_share_dialog, null);
		final AlertDialog alert = new AlertDialog.Builder(ShareActivity.this)
									.setView(dialogView)
									.setCancelable(true)
									.create();

		TextView textViewTerms = (TextView) dialogView.findViewById(R.id.textViewTerms);
		textViewTerms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(ShareActivity.this, TermsConditionsActivity.class);
				startActivity(intent);
			}
		});

		Button buttonOk = (Button) dialogView.findViewById(R.id.buttonTermsOk);
		buttonOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				alert.dismiss();
				if (!EventModel.getSelectedEvent().getsHtmlBefore().isEmpty()) {
					Intent intent = new Intent(ShareActivity.this, BeforeHtmlActivity.class);
					intent.putExtra("requestCode", Utils.REQUEST_ACTIVITY_BEFORE_HTML);
					startActivityForResult(intent, Utils.REQUEST_ACTIVITY_BEFORE_HTML);
				} else {
					completeAction(nSelectedIndex);
				}
			}
		});

		Button buttonCancel = (Button) dialogView.findViewById(R.id.buttonTermsCancel);
		buttonCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				alert.dismiss();
			}
		});
		alert.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		new S3PutObjectTask().execute("");

		setContentView(R.layout.activity_share);
		List<String> strings = Arrays.asList(new String[]{"facebook", "twitter", "album", "email", "instagram"});
		ListView listView = (ListView) findViewById(R.id.listViewShareList);
		ArrayAdapter<String> arrayAdapter = new ShareAdapter(this, strings);
		listView.setAdapter(arrayAdapter);
		Bitmap bm = SharedImageObjects.mBitmapWithEffect;
		bm = Utils.rotateBitmap(bm, -5);
		ImageView imageView = (ImageView) findViewById(R.id.imageViewSharePicture);
		imageView.setImageBitmap(bm);

		arrayAdapter.notifyDataSetChanged();
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				nSelectedIndex = position;
				showCustomDialog();
			}
		});

		// Setup Facebook button and install listener
		facebook = new Facebook(APP_ID);
		mAsyncRunner = new AsyncFacebookRunner(facebook);

		ImageButton imageButtonClose = (ImageButton) findViewById(R.id.imageButtonClose);
		imageButtonClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ShareActivity.this.finish();
			}
		});
	}

	private void galleryAddPic(File f) {

		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}

	private class S3TaskResult {
		String	errorMessage	= null;
		Uri		uri				= null;

		public String getErrorMessage() {
			return errorMessage;
		}

		public void setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
		}

		public Uri getUri() {
			return uri;
		}

		public void setUri(Uri uri) {
			this.uri = uri;
		}
	}

	private class S3PutObjectTask extends AsyncTask<String, Void, S3TaskResult> {

		protected void onPreExecute() {

		}

		protected S3TaskResult doInBackground(String... params) {
			S3TaskResult result = new S3TaskResult();

			// Put the image data into S3.
			try {

				File file = Utils.getFileFromBitmap(SharedImageObjects.mBitmapWithEffect);
				// s3Client.createBucket(Constants.getPictureBucket());
				// Content type is determined by file extension.
				SharedImageObjects.mKey = file.getName();
				PutObjectRequest por = new PutObjectRequest(Constants.getPictureBucket(), SharedImageObjects.mKey, file);
				s3Client.putObject(por);
				file.delete();
			} catch (Exception exception) {
				result.setErrorMessage(exception.getMessage());
			}
			return result;
		}

		protected void onPostExecute(S3TaskResult result) {
			String sResult = result.getErrorMessage() == null ? "Uploaded successfully in S3" : result.getErrorMessage();
			Log.d("PIXTA", sResult);
		}
	}
}

class ShareAdapter extends ArrayAdapter<String> {
	private List<String>	mListItems;
	private Activity		mContext;

	static class ViewHolder {
		public ImageView	imageViewMedia;
	}

	public ShareAdapter(Activity context, List<String> list) {
		super(context, R.layout.share_row_layout, list);
		this.mListItems = list;
		this.mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = null;
		if (convertView == null) {
			rowView = mContext.getLayoutInflater().inflate(R.layout.share_row_layout, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.imageViewMedia = (ImageView) rowView.findViewById(R.id.imageViewMedia);
			rowView.setTag(viewHolder);
		} else {
			rowView = convertView;
		}
		ViewHolder holder = (ViewHolder) rowView.getTag();

		if (mListItems.get(position).equalsIgnoreCase("Facebook")) {
			holder.imageViewMedia.setBackgroundResource(R.drawable.upload_facebook);
		} else if (mListItems.get(position).equalsIgnoreCase("Twitter")) {
			holder.imageViewMedia.setBackgroundResource(R.drawable.upload_twitter);
		} else if (mListItems.get(position).equalsIgnoreCase("instagram")) {
			holder.imageViewMedia.setBackgroundResource(R.drawable.upload_instagram);
		} else if (mListItems.get(position).equalsIgnoreCase("email")) {
			holder.imageViewMedia.setBackgroundResource(R.drawable.upload_email);
		}else {
			holder.imageViewMedia.setBackgroundResource(R.drawable.upload_save);
		}

		return rowView;
	}
}