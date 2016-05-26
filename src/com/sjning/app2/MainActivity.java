package com.sjning.app2;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.sjning.app2.db.DBTool;
import com.sjning.app2.receive.BootService;
import com.sjning.app2.receive.MessageItem;
import com.sjning.app2.tools.FileUtils;
import com.sjning.app2.ui.TopBar;

public class MainActivity extends Activity implements OnClickListener {
	private ListView listView;
	private View dataView;
	private TextView noData;
	private Button sendBtn;

	private MyAdapter adapter;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			adapter.notifyDataSetChanged();
			if (adapter.getCount() == 0) {
				noData.setVisibility(View.VISIBLE);
				dataView.setVisibility(View.INVISIBLE);
			} else {
				noData.setVisibility(View.INVISIBLE);
				dataView.setVisibility(View.VISIBLE);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTopBar();
		dataView = findViewById(R.id.data_view);
		sendBtn = (Button) findViewById(R.id.btn_send);
		sendBtn.setOnClickListener(this);
		listView = (ListView) findViewById(R.id.listview);
		adapter = new MyAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, MessageAct.class);
				intent.putExtra("message", (MessageItem) adapter.getItem(arg2));
				startActivity(intent);
			};

		});
		noData = (TextView) findViewById(R.id.nodata);

		startService(new Intent(this, BootService.class));
		System.out.println("rrrrrrrrrrrrrrrrrrr");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initListView();
	}

	private void initListView() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				List<MessageItem> items = DBTool.getInstance().getSavedMessage(
						getApplicationContext(), null,
						// UserSession.getPhone(getApplicationContext())
						null);
				if (items != null) {
					adapter.setData(items);
				}
				mHandler.sendEmptyMessage(0);
			}

		}).start();
	}

	class MyAdapter extends BaseAdapter {
		private List<MessageItem> items = new ArrayList<MessageItem>();

		public void setData(List<MessageItem> tasks) {
			this.items.clear();
			this.items.addAll(tasks);
			tasks.clear();
			tasks = null;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			MessageItem item = this.items.get(position);
			if (convertView == null) {
				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.message_item, null);
				holder = new ViewHolder();
				holder.text1 = (TextView) convertView.findViewById(R.id.text1);
				holder.text2 = (TextView) convertView.findViewById(R.id.text2);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.text1.setText(item.getPhone());
			holder.text2.setText(item.getDate());
			return convertView;

		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		// Intent intent = new Intent();
		// intent.setClass(this, Search.class);
		// startActivity(intent);
	}

	class ViewHolder {
		TextView text1;
		TextView text2;

	}

	private void setTopBar() {
		TopBar topBar = (TopBar) findViewById(R.id.topBar);
		topBar.hiddenLeftButton(true);
		topBar.hiddenRightButton(true);
		topBar.setTitle("甩手掌柜");
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_send:
			FileUtils.saveToSDCardOrRAM(this, filename, content, filePath);
			break;

		}
	}
}
