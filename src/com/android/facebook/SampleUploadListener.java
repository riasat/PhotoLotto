package com.android.facebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.text.method.BaseKeyListener;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

public class SampleUploadListener extends BaseKeyListener implements RequestListener {

    public void onComplete(final String response, final Object state) {
    	String message = "<empty>";
        try {
            // process the response here: (executed in background thread)
            Log.d("Facebook-Example", "Response: " + response.toString());
            JSONObject json = Util.parseJson(response);
            message = json.getString("message");

            // then post the processed result back to the UI thread
            // if we do not do this, an runtime exception will be generated
            // e.g. "CalledFromWrongThreadException: Only the original
            // thread that created a view hierarchy can touch its views."

        } catch (JSONException e) {
            Log.w("Facebook-Example", "JSON Error in response");
        } catch (FacebookError e) {
            Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
        }
        
        final String text = "Your Wall Post: " + message;
    }

    public void onFacebookError(FacebookError e, Object state) {
        // TODO Auto-generated method stub

    }

    public Bitmap getInputType(Bitmap img) {
        // TODO Auto-generated method stub
        return img;
    }

    @Override
    public int getInputType() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void onIOException(IOException e, Object state) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFileNotFoundException(FileNotFoundException e,
            Object state) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMalformedURLException(MalformedURLException e,
            Object state) {
        // TODO Auto-generated method stub

    }
}