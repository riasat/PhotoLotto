package com.abir.photolotto;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class BeforeHtmlActivity extends Activity {
	private ProgressDialog dialog;

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_html);
		
		String url = "";
		switch (this.getIntent().getIntExtra("requestCode", Utils.REQUEST_ACTIVITY_BEFORE_HTML)) {
			case Utils.REQUEST_ACTIVITY_BEFORE_HTML :
				url = EventModel.getSelectedEvent().getsHtmlBefore();
				break;
			case Utils.REQUEST_ACTIVITY_AFTER_HTML:
				url = EventModel.getSelectedEvent().getsHtmlAfter();
			default :
				break;
		}
		dialog = new ProgressDialog(BeforeHtmlActivity.this);

		WebView webView = (WebView) this.findViewById(R.id.webViewHtml);

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {                  
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		});
		dialog.setMessage("Loading... Please wait.");
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		webView.loadUrl(url);

		//WebSettings webSettings = webView.getSettings();
		//webSettings.setJavaScriptEnabled(true);
		
		final Button buttonDone = (Button) findViewById(R.id.buttonDoneHtml);
		buttonDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setResult(Utils.REQUEST_ACTIVITY_DONE);
				finish();
			}
		});
		final Button buttonShareAgain = (Button) findViewById(R.id.buttonShareAgain);
		buttonShareAgain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setResult(Utils.REQUEST_ACTIVITY_SHARE);
				finish();
			}
		});
	}
}
