package com.tianyl.android.offlinereader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class OpenBrowerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Uri data = getIntent().getData();
		Intent intent = new Intent();
		intent.setClass(this, MainActivity.class);
		if (data != null) {
			Bundle b = new Bundle();
			b.putString("newUrl", data.toString());
			intent.putExtras(b);
		}
		startActivity(intent);
		finish();
	}
}
