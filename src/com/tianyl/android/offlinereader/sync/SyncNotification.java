package com.tianyl.android.offlinereader.sync;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.widget.RemoteViews;

import com.tianyl.android.offlinereader.R;

public class SyncNotification {

	private Integer id;
	private int count;
	private int finish;
	private Notification notification;
	private static NotificationManager notificationManager;

	public SyncNotification(Service service) {
		if (notificationManager == null) {
			notificationManager = (NotificationManager) service.getSystemService(Service.NOTIFICATION_SERVICE);
		}
		id = 10;
		notification = new Notification();
		notification.icon = R.drawable.download_icon;
		notification.tickerText = service.getResources().getString(R.string.no_startSync);
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		notification.contentView = new RemoteViews(service.getPackageName(), R.layout.notification_item);
		notification.contentView.setTextViewText(R.id.titleTextView, "正在同步");
		notification.contentView.setTextViewText(R.id.progressTextView, "0/0");
		notification.contentView.setProgressBar(R.id.downloadProgressBar, 100, 0, false);
	}

	public void notifyNotification() {
		notification.contentView.setTextViewText(R.id.progressTextView, finish + "/" + count);
		notification.contentView.setProgressBar(R.id.downloadProgressBar, count, finish, false);
		notificationManager.notify(id, notification);
	}

	public void addOneFinish() {
		finish++;
		notifyNotification();
	}

	public void setCount(int size) {
		count = size;
		notifyNotification();
	}

	public void cancel() {
		notificationManager.cancel(id);
	}
}
