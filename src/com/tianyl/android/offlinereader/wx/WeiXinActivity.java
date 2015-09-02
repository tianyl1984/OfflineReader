package com.tianyl.android.offlinereader.wx;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.tianyl.android.offlinereader.R;

public class WeiXinActivity extends Activity {

	EditText logEt = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weixin);
		logEt = (EditText) findViewById(R.id.logEt);
		findViewById(R.id.wxStartBtn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				logEt.append("afdas\n");
			}
		});
	}

}
