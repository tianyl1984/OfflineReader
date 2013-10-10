package com.tianyl.android.offlinereader.common;

public class StringUtil {

	public static boolean isBlank(String str) {
		return str == null || str.trim().length() == 0;
	}

	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	public static final String getRandomStr() {
		return getRandomStr(5);
	}

	public static String getRandomStr(int count) {
		char A = 'A';
		char z = 'z';
		String result = "";
		for (int i = 0; i < count; i++) {
			Double d = Math.random() * (z - A) + A;
			char aa = (char) d.intValue();
			result += String.valueOf(aa);
		}
		return result;
	}

	public static String htmlDecode(String str) {
		if (isBlank(str)) {
			return str;
		}
		return str.replaceAll("&nbsp;", " ").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
	}
}
