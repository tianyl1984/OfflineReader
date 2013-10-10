package com.tianyl.android.offlinereader.common;

import java.io.File;

public interface DownloadCallBack {

	public void download(long sum, long finished, File file);

	public void downloadFinish(File file);
}
