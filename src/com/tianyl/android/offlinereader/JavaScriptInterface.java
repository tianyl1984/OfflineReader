package com.tianyl.android.offlinereader;

import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.tianyl.android.offlinereader.dao.ArticleDBUtil;

public class JavaScriptInterface {

	private WebView webView;

	private Handler handler;

	private ArticleActivity articleActivity;

	public JavaScriptInterface(WebView webView, Handler handler, ArticleActivity articleActivity) {
		this.webView = webView;
		this.handler = handler;
		this.articleActivity = articleActivity;
	}

	@JavascriptInterface
	public void logScrollTop(final long id, final int top) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				new ArticleDBUtil(articleActivity).updateLastTop(id, top);
			}
		});
	}

	@JavascriptInterface
	public void toast(final String str) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				// Looper.prepare();
				Toast.makeText(articleActivity, "str:" + str, Toast.LENGTH_SHORT).show();
				// Looper.loop();
			}
		});
	}
}
