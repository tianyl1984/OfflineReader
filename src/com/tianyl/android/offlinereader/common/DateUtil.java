package com.tianyl.android.offlinereader.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

	/**
	 * yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static String formatToString(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(date);
	}

	/**
	 * yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 */
	public static String getCurrentDate() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
	}
}
