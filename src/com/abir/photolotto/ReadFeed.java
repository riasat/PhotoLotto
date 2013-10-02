package com.abir.photolotto;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

public class ReadFeed extends AsyncTask<String, Void, String> {
	private int mEvent = -1;
	ReadFeedEventHandler eventHandler;
	boolean mIsFailed = true;
	public Location mLocation;
	
	public ReadFeed() {
		mEvent = -1;
	}
	
	public ReadFeed(int nEvent){
		mEvent = nEvent;
	}
	
	private double getLatitude(){
		if(mLocation == null)
			return 0.0;
		return mLocation.getLatitude();
	}
	
	private double getLongitude(){
		if(mLocation == null)
			return 0.0;
		return mLocation.getLongitude();
	}
	
	public void setLocation(Location location) {
		mLocation = location;
	}
		
	public void setEventHandler(ReadFeedEventHandler handler){
		eventHandler = handler;
	}
	
	@Override
	protected String doInBackground(String... params) {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		double gpslat = getLatitude();
		double gpslong = getLongitude();
		HttpGet httpGet = new HttpGet("http://www.pixta.com.au/eventlist?gpslat=" + gpslat + "&gpslong=" + gpslong);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
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
				mIsFailed = true;

			}
		} catch (Exception e) {
			e.printStackTrace();
			mIsFailed = true;
		}
		
		return builder.toString();
	}

	@Override
	protected void onPostExecute(String result) {
		mIsFailed = false;
		try {
			JSONArray jsonArray = new JSONArray(result);
			// Log.i(ParseJSON.class.getName(), "Number of entries " + jsonArray.length());
			EventModel.lstPixtaEvents.clear();
			EventModel.lstNearEvents.clear();
			EventModel.lstNationalEvents.clear();

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Log.d("SelectEventActivity", jsonObject.toString());
				EventModel eventModel = new EventModel();
				try {
					eventModel.setfDistance(jsonObject.getString("distance"));
					eventModel.setlId(jsonObject.getLong("id"));
					eventModel.setnNumberOfOverlay(jsonObject.getInt("number_of_overlay"));
					eventModel.setsCompanyName(jsonObject.getString("company_name"));
					eventModel.setsFacebookMessage(jsonObject.getString("facebook_msg"));
					eventModel.setsFacebookUrl(jsonObject.getString("facebook_url"));
					eventModel.setsHtmlAfter(jsonObject.getString("html_after"));
					eventModel.setsHtmlBefore(jsonObject.getString("html_before"));
					eventModel.setsImgThumb(jsonObject.getString("img_thumb"));
					eventModel.setsName(jsonObject.getString("name"));
					eventModel.setsShortDescription1(jsonObject.getString("shortdescription_line_1"));
					eventModel.setsShortDescription2(jsonObject.getString("shortdescription_line_2"));
					eventModel.setsEventType(jsonObject.getString("event_type"));
					eventModel.setsTerms(jsonObject.getString("t_c"));
					eventModel.setsTwitterMsg(jsonObject.getString("twitter_msg"));
					int number_of_overlay = jsonObject.getInt("number_of_overlay");
					eventModel.setnNumberOfOverlay(number_of_overlay);
					switch (number_of_overlay) {
						case 5 :
							eventModel.setsImageOverlay5(jsonObject.getString("img_overlay_5"));
						case 4 :
							eventModel.setsImageOverlay4(jsonObject.getString("img_overlay_4"));
						case 3 :
							eventModel.setsImageOverlay3(jsonObject.getString("img_overlay_3"));
						case 2 :
							eventModel.setsImageOverlay2(jsonObject.getString("img_overlay_2"));
						case 1 :
							eventModel.setsImageOverlay1(jsonObject.getString("img_overlay_1"));
							break;
						default :
							break;
					}
				} catch (JSONException e) {
					e.printStackTrace();
					mIsFailed = true;
				}

				if (eventModel.getsEventType().equalsIgnoreCase("pixta-play"))
					EventModel.lstPixtaEvents.add(eventModel);
				else if(eventModel.getsEventType().equalsIgnoreCase("national")){
					EventModel.lstNationalEvents.add(eventModel);
				}
				else { 
					String[] sDistance = eventModel.getfDistance().split("\\s+");
					if(Float.parseFloat(sDistance[0]) < 25)
						EventModel.lstNearEvents.add(eventModel);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			mIsFailed = true;
		}
		
		if(mIsFailed) 
			eventHandler.onFailure();
		else
			eventHandler.onPostExecute(mEvent);
	}

	@Override
	protected void onPreExecute() {
		eventHandler.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(Void... values) {
	}
}

interface ReadFeedEventHandler {
	public void onPreExecute();
	public void onPostExecute(int nEvent);
	public void onFailure();
}