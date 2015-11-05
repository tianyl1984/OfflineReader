package com.tianyl.android.offlinereader;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.webkit.WebView;

import com.tianyl.android.offlinereader.common.FileUtil;
import com.tianyl.android.offlinereader.dao.ArticleDBUtil;
import com.tianyl.android.offlinereader.model.Article;

public class ArticleActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.article);
		Bundle b = getIntent().getExtras();
		long id = b.getLong("articleId");
		Article article = new ArticleDBUtil(this).get(id);

		WebView articleWebView = (WebView) findViewById(R.id.articleWebView);
		articleWebView.getSettings().setJavaScriptEnabled(true);
		articleWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		articleWebView.getSettings().setAllowFileAccess(true);
		articleWebView.addJavascriptInterface(new JavaScriptInterface(articleWebView, new Handler(), this), "android");
		articleWebView.loadUrl("file://" + FileUtil.getBathPath() + article.getPathId() + "/" + article.getPathId() + ".html?id=" + id + "&lastTop=" + article.getLastTop());
		// articleWebView.loadUrl("file:///android_asset/test.html?id=124&lastTop=12345");
	}

}
