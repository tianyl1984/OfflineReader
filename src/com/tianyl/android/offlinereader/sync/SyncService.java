package com.tianyl.android.offlinereader.sync;

import java.io.File;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

import com.tianyl.android.offlinereader.common.DateUtil;
import com.tianyl.android.offlinereader.common.FileUtil;
import com.tianyl.android.offlinereader.common.NetUtil;
import com.tianyl.android.offlinereader.common.StringUtil;
import com.tianyl.android.offlinereader.common.UUID;
import com.tianyl.android.offlinereader.dao.ArticleDBUtil;
import com.tianyl.android.offlinereader.model.Article;

public class SyncService extends Service {

	private ArticleDBUtil articleDBUtil;

	private SyncNotification syncNotification;

	private static final File logFile = new File(FileUtil.getBathPath() + "/log.txt");

	private static void addLog(String str) {
		FileUtil.appendStringToFile(DateUtil.getCurrentDate() + "  " + str, logFile);
	}

	@Override
	public IBinder onBind(Intent intent) {
		addLog("onBind");
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		addLog("onCreate");
		articleDBUtil = new ArticleDBUtil(this);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		addLog("onStart");
		syncNotification = new SyncNotification(this);

		new Thread(new Runnable() {
			@Override
			public void run() {
				addLog("onStart run");
				List<Article> articles = articleDBUtil.selectUnEnd();
				if (articles.size() == 0) {
					return;
				}
				syncNotification.setCount(articles.size());
				for (Article article : articles) {
					try {
						String url = article.getUrl();
						String html = NetUtil.getUrlResponse(url);
						String uuid = UUID.getUUID();
						String fileName = FileUtil.getBathPath() + uuid + "/" + uuid + ".html";
						Document document = Jsoup.parse(html);
						article.setPathId(uuid);
						String title = "未知";
						Elements eles = document.getElementsByTag("title");
						if (!eles.isEmpty()) {
							Element ele = eles.iterator().next();
							title = ele.html();
						}
						article.setTitle(title);
						articleDBUtil.update(article);
						eles = document.getElementsByTag("img");
						for (Element ele : eles) {
							if (ele.hasAttr("src") || ele.hasAttr("data-src")) {
								String picUrl = ele.attr("src");
								if (ele.hasAttr("data-src") && StringUtil.isNotBlank(ele.attr("data-src"))) {
									picUrl = ele.attr("data-src");
								}
								picUrl = NetUtil.getRealURL(url, picUrl);
								String picId = UUID.getUUID();
								NetUtil.downloadFileSimple(picUrl, new File(FileUtil.getBathPath() + uuid + "/" + picId));
								ele.attr("src", picId);
							}
						}
						eles = document.getElementsByTag("link");
						for (Element ele : eles) {
							if (ele.hasAttr("href")) {
								String href = ele.attr("href");
								href = NetUtil.getRealURL(url, href);
								String newId = UUID.getUUID();
								NetUtil.downloadFileSimple(href, new File(FileUtil.getBathPath() + uuid + "/" + newId + ".css"));
								ele.attr("href", newId + ".css");
							}
						}
						eles = document.getElementsByTag("script");
						for (Element ele : eles) {
							if (ele.hasAttr("src")) {
								String src = ele.attr("src");
								src = NetUtil.getRealURL(url, src);
								String newId = UUID.getUUID();
								NetUtil.downloadFileSimple(src, new File(FileUtil.getBathPath() + uuid + "/" + newId + ".js"));
								ele.attr("src", newId + ".js");
							}
						}
						FileUtil.saveStringToFile(document.html(), new File(fileName));
						articleDBUtil.updateStatusToEnd(article.getId());
						syncNotification.addOneFinish();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				syncNotification.cancel();
			}
		}).start();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		addLog("onDestroy");
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		addLog("onLowMemory");
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		addLog("onConfigurationChanged");
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
		addLog("onRebind");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int result = super.onStartCommand(intent, flags, startId);
		addLog("onStartCommand");
		return result;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		boolean result = super.onUnbind(intent);
		addLog("onUnbind");
		return result;
	}
}
