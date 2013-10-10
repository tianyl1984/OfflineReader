package com.tianyl.android.offlinereader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.tianyl.android.offlinereader.common.FileUtil;
import com.tianyl.android.offlinereader.common.NetUtil;
import com.tianyl.android.offlinereader.common.ProgressThreadWrap;
import com.tianyl.android.offlinereader.common.RunnableWrap;
import com.tianyl.android.offlinereader.common.StringUtil;
import com.tianyl.android.offlinereader.common.UUID;
import com.tianyl.android.offlinereader.dao.ArticleDBUtil;
import com.tianyl.android.offlinereader.model.Article;

public class MainActivity extends Activity {

	private ListView articleListView;

	private ArticleDBUtil articleDBUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		articleListView = (ListView) findViewById(R.id.articleListView);
		articleDBUtil = new ArticleDBUtil(this);
		articleListView.setAdapter(new ArticleListAdapter(new ArrayList<Article>(), this));
		articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long id) {
				Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
				Bundle bundle = new Bundle();
				bundle.putLong("articleId", id);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		articleListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View arg1, int position, long id) {
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("操作");
				final long optId = id;
				builder.setItems(new String[] { "删除" }, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							delete(optId);
						}
					}

				});
				builder.create().show();
				return true;
			}
		});

		Bundle b = getIntent().getExtras();
		if (b != null) {
			String url = b.getString("newUrl");
			if (StringUtil.isNotBlank(url)) {
				addURL(url);
				// this.finish();
			}
		}

		flushData();
	}

	private void delete(long optId) {
		Article article = articleDBUtil.get(optId);
		articleDBUtil.delete(optId);
		File file = new File(FileUtil.getBathPath() + article.getPathId());
		if (file.exists()) {
			for (File f : file.listFiles()) {
				f.delete();
			}
			file.delete();
		}
		flushData();
	}

	private void flushData() {
		List<Article> articles = articleDBUtil.selectAll();
		ArticleListAdapter adapter = (ArticleListAdapter) articleListView.getAdapter();
		adapter.setArticles(articles);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.action_add:
			LayoutInflater li = LayoutInflater.from(MainActivity.this);
			final View view = li.inflate(R.layout.add_url, null);
			final EditText urlText = (EditText) view.findViewById(R.id.urlText);
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setView(view);
			AlertDialog ad = builder.create();
			ad.setTitle("输入要添加的地址");
			ad.setButton("确定", new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String url = urlText.getText().toString().trim();
					if (url.isEmpty()) {
						return;
					}
					handler.sendMessage(Message.obtain(handler, MSG_ADD_URL, url));
				}

			});
			ad.setButton2("取消", new android.content.DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
				}

			});
			ad.show();
			break;
		default:
			break;
		}
		return true;
	}

	private void addURL(final String url) {
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run(ProgressDialog progressDialog) {
				try {
					String html = NetUtil.getUrlResponse(url);
					String uuid = UUID.getUUID();
					String fileName = FileUtil.getBathPath() + uuid + "/" + uuid + ".html";
					Document document = Jsoup.parse(html);

					Article article = new Article();
					article.setStatus(Article.STATUS_START);
					article.setUrl(url);
					article.setPathId(uuid);
					String title = "未知";
					Elements eles = document.getElementsByTag("title");
					if (!eles.isEmpty()) {
						Element ele = eles.iterator().next();
						title = ele.html();
					}
					article.setTitle(title);
					articleDBUtil.save(article);

					eles = document.getElementsByTag("img");
					for (Element ele : eles) {
						if (ele.hasAttr("src")) {
							String picUrl = ele.attr("src");
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
					Toast.makeText(MainActivity.this, "添加成功", Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					Toast.makeText(MainActivity.this, "出错：" + e.getMessage(), Toast.LENGTH_LONG).show();
					e.printStackTrace();
				} finally {
					progressDialog.dismiss();
					handler.sendMessage(Message.obtain(handler, MSG_FLUSHDATA, null));
				}
			}
		}).start();
	}

	private static final int MSG_ADD_URL = 0;

	private static final int MSG_FLUSHDATA = 1;

	private Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ADD_URL:
				addURL(msg.obj.toString());
				break;
			case MSG_FLUSHDATA:
				flushData();
				break;

			default:
				break;
			}
			return false;
		}
	});
}
