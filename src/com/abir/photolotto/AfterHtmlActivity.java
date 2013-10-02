package com.abir.photolotto;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class AfterHtmlActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_html);
		String html = EventModel.getSelectedEvent().getsHtmlAfter();
		String mime = "text/html";
		String encoding = "utf-8";

		WebView myWebView = (WebView) this.findViewById(R.id.webViewHtml);
		//myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.loadDataWithBaseURL(null, html, mime, encoding, null);
	}
}