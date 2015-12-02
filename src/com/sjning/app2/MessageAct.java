package com.sjning.app2;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.sjning.app2.receive.MessageItem;
import com.sjning.app2.ui.TopBar;

public class MessageAct extends Activity {
	private TextView data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_detail);
		setTopBar();
		data = (TextView) findViewById(R.id.data);
		MessageItem message = (MessageItem) getIntent().getSerializableExtra(
				"message");
		data.setText(message.getPhone() + "\n" + message.getDate() + "\n"
				+ message.getBody());
	}

	private void setTopBar() {
		TopBar topBar = (TopBar) findViewById(R.id.topBar);
		topBar.hiddenLeftButton(true);
		topBar.hiddenRightButton(true);
		topBar.setTitle("实刻订");
	}
}
