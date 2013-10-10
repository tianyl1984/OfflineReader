package com.tianyl.android.offlinereader.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Looper;

public class ProgressThreadWrap {

	private Context context;

	private RunnableWrap runnableWrap;

	public ProgressThreadWrap(Context context, RunnableWrap runnableWrap) {
		this.context = context;
		this.runnableWrap = runnableWrap;
	}

	public void start() {
		final ProgressDialog pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage("正在操作，请等待...");
		pd.setCancelable(false);
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				runnableWrap.run(pd);
				Looper.loop();
			}
		}).start();
	}
}
