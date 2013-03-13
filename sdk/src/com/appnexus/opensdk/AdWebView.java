package com.appnexus.opensdk;

import com.appnexus.opensdk.R;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * @author jshufro@appnexus.com
 * 
 */
@SuppressLint("ViewConstructor") //This will only be constructed by AdFetcher.
public class AdWebView extends WebView implements Displayable {
	private boolean failed=false;
	private AdView destination;

	protected AdWebView(AdView owner) {
		super(owner.getContext());
		destination=owner;
		setup();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void setup() {
		Settings.getSettings().ua = this.getSettings().getUserAgentString();
		this.getSettings().setJavaScriptEnabled(true);
		this.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		this.getSettings().setPluginState(WebSettings.PluginState.ON);
		this.getSettings().setBuiltInZoomControls(false);
		this.getSettings().setLightTouchEnabled(false);
		this.getSettings().setLoadsImagesAutomatically(true);
		this.getSettings().setSupportZoom(false);
		this.getSettings().setUseWideViewPort(true);
		this.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		
		setHorizontalScrollbarOverlay(false);
		setHorizontalScrollBarEnabled(false);
		setVerticalScrollbarOverlay(false);
		setVerticalScrollBarEnabled(false);

		setBackgroundColor(Color.TRANSPARENT);
		setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);

		setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return (event.getAction() == MotionEvent.ACTION_MOVE);
			}
		});
		
		setWebViewClient(new WebViewClient(){
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingURL){
				Clog.e(Clog.httpReqLogTag, String.format(Clog.getString(R.string.webclient_error), errorCode, description));
			}
		});
		
	}
	
	@Override
	public void flingScroll(int vx, int vy){
		return;
	}

	protected void loadAd(AdResponse ad) {
		if(ad.getBody()==""){
			Clog.e(Clog.httpRespLogTag, Clog.getString(R.string.blank_ad));
			fail();
			return;
		}

		String body = "<html><head /><body style='margin:0;padding:0;'>"
				+ ad.getBody() + "</body></html>";
		Clog.v(Clog.baseLogTag, Clog.getString(R.string.webview_loading, body));
		this.loadData(body, "text/html", "UTF-8");
		
		final float scale = destination.getContext().getResources().getDisplayMetrics().density;
		int rheight = (int)(ad.getHeight()*scale+0.5f);
		int rwidth = (int)(ad.getWidth()*scale+0.5f);
		int rgravity=Gravity.CENTER;
		AdView.LayoutParams resize = new AdView.LayoutParams(rwidth, rheight, rgravity);
		this.setLayoutParams(resize);
	}

	@Override
	public View getView() {
		return this;
	}

	private void fail() {
		failed=true;
	}

	@Override
	public boolean failed() {
		return failed;
	}
}
