package com.edom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class wwwActivity  extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_www);
		
		Intent intent = getIntent();

        String webSiteName = intent.getStringExtra("WebSite");
		
		
		WebView myWebView = (WebView) findViewById(R.id.webView1);
		myWebView.loadUrl(webSiteName);
		WebSettings webSettings = myWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(true);
		
	}
	

}
