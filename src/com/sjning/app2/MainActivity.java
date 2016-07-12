package com.sjning.app2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.sjning.app2.intrface.TopBarClickListener;
import com.sjning.app2.receive.BootService;
import com.sjning.app2.receive.MessageItem;
import com.sjning.app2.tools.FileUtils;
import com.sjning.app2.tools.NormalUtil;
import com.sjning.app2.ui.TopBar;

public class MainActivity extends Activity implements OnClickListener {
	private ListView listView;
	private View dataView;
	private View noData;
	private Button sendBtn, restartBtn;

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
		noData = findViewById(R.id.nodata);
		restartBtn = (Button) findViewById(R.id.btn_restart);
		restartBtn.setOnClickListener(this);

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

		public String getOutPutMessage() {
			Date date = new java.util.Date();
			String dateTime = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
					.format(date);
			String temp = "TM: " + dateTime + "\r\n" + "\r\n";
			if (items != null && !items.isEmpty()) {
				for (int i = 0; i < items.size(); i++) {
					MessageItem item = items.get(i);
					temp += ("##: " + item.getPhone() + ":  " + item.getItems()
							+ "\r\n" + item.getBody() + "\r\n");
				}
			}
			return temp;
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
				holder.text3 = (TextView) convertView.findViewById(R.id.text3);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.text1.setText(item.getPhone());
			holder.text2.setText(item.getDate());
			holder.text3.setText(item.getItems());
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
		TextView text3;

	}

	private void setTopBar() {
		TopBar topBar = (TopBar) findViewById(R.id.topBar);
		topBar.hiddenLeftButton(true);
		topBar.hiddenRightButton(false);
		topBar.setTitle("甩手掌柜");
		topBar.setRightDrawable(R.drawable.close);
		topBar.setTopBarClickListener(new TopBarClickListener() {

			@Override
			public void rightBtnClick() {

				finish();

			}

			@Override
			public void leftBtnClick() {

			}
		});

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_send:
			try {
				String fileName = "易览通汇总" + ".txt";
				String filePath = NormalUtil.getRootDir();
				if (FileUtils.checkFileExist(filePath + fileName)) {
					FileUtils.deleteFile(filePath + fileName);
				}
				FileUtils.saveToSDCardOrRAM(this, fileName,
						adapter.getOutPutMessage(), filePath);
				Intent intent = new Intent(this, OkAct.class);
				startActivity(intent);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.btn_restart:
			startService(new Intent(this, BootService.class));
			break;
		}
	}
}
