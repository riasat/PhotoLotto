package com.abir.photolotto;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class EmailActivity extends Activity 
							implements OnClickListener{
	EditText mEditTextToEmail, mEditTextSubject, mEditTextMessage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_email);
		//mBitmap = Utils.rotateBitmap(SharedImageObjects.mBitmapWithEffect, -5);
		ImageView imageViewPhoto = (ImageView) findViewById(R.id.imageViewPhoto);
		imageViewPhoto.setImageBitmap(Utils.rotateBitmap(SharedImageObjects.mBitmapWithEffect, -5));
		mEditTextToEmail = (EditText) findViewById(R.id.editTextEmailAddress);
		mEditTextSubject = (EditText) findViewById(R.id.editTextEmailSubject);
		mEditTextMessage = (EditText) findViewById(R.id.editTextEmailMessage);
		
		Button buttonSend = (Button) findViewById(R.id.buttonSendEmail);
		buttonSend.setOnClickListener(this);
		Button buttonCancel = (Button) findViewById(R.id.buttonCancelEmail);
		buttonCancel.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.buttonSendEmail :
				new EmailTask().execute("");
				break;
			case R.id.buttonCancelEmail:
				finish();
				break;
			default :
				break;
		}
	}
	
	public final boolean isValidEmail(CharSequence target) {
	    if (target == null || target.toString().isEmpty()) {
	        return false;
	    } else {
	        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	    }
	}
	
	private class EmailTask extends AsyncTask <String, Void, String>{

		protected String doInBackground(String... urls) {
			String sUrl = "";
			try{
			sUrl = "http://www.pixta.com.au/eventemail?event_id="
						+ EventModel.getSelectedEvent().getlId() 
						+ "&phone_type=Android&action=email&phone_id=1&photo=" 
						+ /*Constants.getPictureBucket()*/ SharedImageObjects.mKey
						+ "&email_to="
						+ mEditTextToEmail.getEditableText().toString()
						+ "&subject="
						+ URLEncoder.encode(mEditTextSubject.getEditableText().toString(),"UTF-8")
						+ "&message=" 
						+ URLEncoder.encode(mEditTextMessage.getEditableText().toString(),"UTF-8");
			}catch(Exception e){
				e.printStackTrace();
			}
			if(!isValidEmail(mEditTextToEmail.getEditableText().toString())) {
				return null;
			}
				
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(sUrl);
			try {
				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				Log.e("EmailActivity", statusLine.getReasonPhrase().toString());
				/*StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
				} else {
					Log.e("ReadFeed", "Failed to download file");
				}*/
			} catch (Exception e) {
				e.printStackTrace();
			}
		 
			return builder.toString();
	     }

	     protected void onPostExecute(String result) {
	    	 if(result == null)
	    		 Toast.makeText(EmailActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
	    	 else
	    		 EmailActivity.this.finish();
	     }
	 }
}
