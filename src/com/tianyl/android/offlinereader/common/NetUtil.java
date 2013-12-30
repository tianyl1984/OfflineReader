package com.tianyl.android.offlinereader.common;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;

public class NetUtil {

	private static String sessionId;

	public static boolean checkWifi(final Context context) {
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (wm.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
			new AlertDialog.Builder(context).setTitle("提示").setMessage("设置wifi？").setNegativeButton("取消", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			}).setPositiveButton("设置", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
				}
			}).show();
			return false;
		} else {
			return true;
		}
	}

	public static void clearSession() {
		sessionId = null;
	}

	public static void downloadFileSimple(String url, File file) throws IOException {
		HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
		con.setUseCaches(false);
		if (StringUtil.isNotBlank(sessionId)) {// 设置cookie
			con.setRequestProperty("Cookie", sessionId);
		}
		if (con.getResponseCode() != HttpURLConnection.HTTP_OK && con.getResponseCode() != HttpURLConnection.HTTP_PARTIAL) {
			throw new IOException();
		}
		InputStream is = con.getInputStream();
		BufferedInputStream bis = new BufferedInputStream(is);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(file);
		byte[] b = new byte[1024];
		int length = 0;
		while ((length = bis.read(b)) != -1) {
			fos.write(b, 0, length);
		}
		fos.close();
		bis.close();
	}

	public static String getUrlResponse(String url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) (new URL(url).openConnection());
		// POST必须大写
		conn.setRequestMethod("GET");
		conn.setUseCaches(false);
		// 请求头
		// conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:12.0) Gecko/20100101 Firefox/12.0");
		// 仅对当前请求自动重定向
		conn.setInstanceFollowRedirects(false);
		// header 设置编码
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		if (StringUtil.isNotBlank(sessionId)) {// 设置cookie
			// conn.setRequestProperty("Cookie", sessionId);
		}
		conn.setConnectTimeout(10000);
		// 连接
		conn.connect();
		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			Log.v("ResponseCode", conn.getResponseCode() + ":" + url);
			throw new IOException();
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String result = "";
		String temp = null;
		while ((temp = reader.readLine()) != null) {
			result += temp + "\n";
		}
		Map<String, List<String>> headerMap = conn.getHeaderFields();
		if (headerMap.containsKey("set-cookie")) {
			String cookies = conn.getHeaderField("set-cookie");
			sessionId = cookies.substring(0, cookies.indexOf(";"));
		}
		reader.close();
		conn.disconnect();
		return result;
	}

	public static final String getRealURL(String docUrl, String url) {
		if (url.startsWith("http://") || url.startsWith("https://")) {
			return url;
		}
		String aa = "";
		try {
			URI uri = new URI(docUrl);
			aa = uri.getScheme() + "://" + uri.getHost();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return aa + url;
	}
}
