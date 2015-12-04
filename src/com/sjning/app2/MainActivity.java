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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sjning.app2.db.DBTool;
import com.sjning.app2.receive.BootService;
import com.sjning.app2.receive.MessageItem;
import com.sjning.app2.tools.UserSession;
import com.sjning.app2.ui.TopBar;

public class MainActivity extends Activity {
	private ListView listView;
	
	private TextView noData;
	
	private MyAdapter adapter;
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			adapter.notifyDataSetChanged();
			if (adapter.getCount() == 0)
				noData.setVisibility(View.VISIBLE);
			else noData.setVisibility(View.INVISIBLE);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTopBar();
		listView = (ListView)findViewById(R.id.listview);
		adapter = new MyAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, MessageAct.class);
				intent.putExtra("message", (MessageItem)adapter.getItem(arg2));
				startActivity(intent);
			};
			
		});
		noData = (TextView)findViewById(R.id.nodata);
		
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
				List<MessageItem> items =
					DBTool.getInstance().getSavedMessage(getApplicationContext(),
						null,
//						UserSession.getPhone(getApplicationContext())
						null
						);
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
				holder.text1 = (TextView)convertView.findViewById(R.id.text1);
				holder.text2 = (TextView)convertView.findViewById(R.id.text2);
				holder.text3 = (TextView)convertView.findViewById(R.id.text3);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}
			holder.text1.setText(item.getPhone());
			holder.text2.setText(item.getBody());
			holder.text3.setText(item.getDate().replace(" ", "\n "));
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
		TopBar topBar = (TopBar)findViewById(R.id.topBar);
		topBar.hiddenLeftButton(true);
		topBar.hiddenRightButton(true);
		topBar.setTitle("甩手掌柜");
	}
}
