package test.test.arkuni;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.webkit.HttpAuthHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {
	private WebView webView;
	private Dialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WebSettings websetting = null;
		
		setContentView(R.layout.webview);
		webView = (WebView) findViewById(R.id.web_view);
		websetting = webView.getSettings();
		websetting.setJavaScriptEnabled(true);
		websetting.setBuiltInZoomControls(false);
		websetting.setJavaScriptCanOpenWindowsAutomatically(true);
		websetting.setLightTouchEnabled(true);
		websetting.setSaveFormData(true);
		webView.setVerticalScrollbarOverlay(true);
		websetting.setSupportMultipleWindows(true);
		webView.setWebViewClient(new FinderWebViewClient());
		
		webView.setWebChromeClient(new WebChromeClient(){
			@Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean userGesture, Message resultMsg) {
				Log.d("WebViewActivity","onCreateWindow : "+ view.getUrl());
				
				return true;
			}
			
			@Override
			public void onCloseWindow(WebView window) {
				super.onCloseWindow(window);
			}
		});
		webView.loadUrl("http://m.happymoney.co.kr/");
	}
	class FinderWebViewClient extends WebViewClient {    
		@Override    
		public boolean shouldOverrideUrlLoading(WebView view, String url) {    
			Log.d("WebViewActivity","shouldOverrideUrlLoading");
			view.loadUrl(url);
			return false;  
		}
		
		@Override
		public void onReceivedHttpAuthRequest(WebView view,
				HttpAuthHandler handler, String host, String realm) {
			super.onReceivedHttpAuthRequest(view, handler, host, realm);
		}
	}
	@Override
	protected Dialog onCreateDialog(int seq) {
		dialog = new Dialog(WebViewActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));		

		dialog.setContentView(R.layout.webview_dialog);
		WebView childView = (WebView) dialog.findViewById(R.id.web_view_dialog);
		childView.loadUrl("http://www.google.co.kr/");
		WebSettings settings = childView.getSettings();         
		settings.setJavaScriptEnabled(true); 
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setSupportMultipleWindows(true);
		return dialog;
	}

}
