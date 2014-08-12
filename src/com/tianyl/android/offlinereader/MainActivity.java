package com.tianyl.android.offlinereader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.tianyl.android.offlinereader.dao.ArticleDBUtil;
import com.tianyl.android.offlinereader.model.Article;
import com.tianyl.android.offlinereader.sync.SyncService;

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
				addURL(url, true);
				// this.finish();
			}
		}

		flushData();
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			flushData();
		}
	};

	@Override
	protected void onStart() {
		super.onStart();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.BROADCAST_ACTION_SYNC_FINISH);
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(receiver);
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
					// url = "http://mp.weixin.qq.com/mp/appmsg/show?__biz=MjM5ODAzMDMyMQ==&appmsgid=100014098&itemidx=1&sign=70c11536952cd9dfbe1c8abee6a34b3c&uin=MTUwNjE5&key=234b3ec6051a4a54145598a43f997fa544cdfcbd55bc45383ad632155ede9cbadfea7cef04ad0491cac84030e11e7d4f&devicetype=android-17&version=25000338&lang=zh_CN";
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
		case R.id.action_sync:
			if (!NetUtil.checkWifi(this)) {
				break;
			}
			if (articleDBUtil.selectUnEnd().size() > 0) {
				Intent service = new Intent(this, SyncService.class);
				startService(service);
			} else {
				Toast.makeText(this, "已全部下载", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.action_test:
			new Thread(new Runnable() {

				@Override
				public void run() {
					Looper.prepare();
					String url = "http://mp.weixin.qq.com/s?__biz=MjM5MjAzODU2MA==&mid=201206792&idx=1&sn=bb190b8df9d7476a96c62d27a499b314";
					try {
						String html = NetUtil.getUrlResponse(url);
						Toast.makeText(MainActivity.this, html, Toast.LENGTH_LONG).show();
					} catch (IOException e) {
						e.printStackTrace();
						Toast.makeText(MainActivity.this, "出错：" + e.getMessage(), Toast.LENGTH_LONG).show();
					}
					Looper.loop();
				}
			}).start();
			break;
		default:
			break;
		}
		return true;
	}

	private void addURL(final String url, final boolean isFinish) {
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run(ProgressDialog progressDialog) {
				try {
					Article article = new Article();
					article.setStatus(Article.STATUS_START);
					article.setUrl(url);
					article.setTitle("尚未下载数据");
					articleDBUtil.save(article);
					Toast.makeText(MainActivity.this, "添加成功", Toast.LENGTH_LONG).show();
					if (isFinish) {
						MainActivity.this.finish();
					}
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
				addURL(msg.obj.toString(), false);
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

	protected void onDestroy() {
		super.onDestroy();
		articleDBUtil.close();
	};
}
