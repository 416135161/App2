package com.sjning.app2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sjning.app2.bean.GetNetMessageBean;
import com.sjning.app2.bean.SendBean;
import com.sjning.app2.bean.GetNetMessageBean.ResultBean;
import com.sjning.app2.bean.GetNetMessageBean.ResultBean.ContentsBean;
import com.sjning.app2.bean.SendBean.ContentItem;
import com.sjning.app2.db.DBTool;
import com.sjning.app2.intrface.TopBarClickListener;
import com.sjning.app2.receive.MessageItem;
import com.sjning.app2.receive.SMSHandler;
import com.sjning.app2.receive.WatchService;
import com.sjning.app2.tools.FileUtils;
import com.sjning.app2.tools.HttpUtil;
import com.sjning.app2.tools.JsonUtil;
import com.sjning.app2.tools.MessageSender;
import com.sjning.app2.tools.NormalUtil;
import com.sjning.app2.tools.SecretKeyTool;
import com.sjning.app2.tools.SmsContent;
import com.sjning.app2.tools.UserSession;
import com.sjning.app2.ui.TopBar;

public class MainActivity extends Activity implements OnClickListener {
	private ListView listView;
	private View dataView;
	private View noData;
	private Button sendBtn, restartBtn, cleanBtn, acquireBtn, secretBtn;

	private MyAdapter adapter;
	private MainHelper mMainHelper = new MainHelper();

	private Dialog deleteDialog;

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
		if (UserSession.isFirst(this)) {
			NormalUtil.deletePath();
			DBTool.getInstance().deleteAll(this);
			UserSession.setFirstFalse(this);
		}

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
		cleanBtn = (Button) findViewById(R.id.btn_clean);
		cleanBtn.setOnClickListener(this);
		acquireBtn = (Button) findViewById(R.id.btn_acquire);
		acquireBtn.setOnClickListener(this);
		secretBtn = (Button) findViewById(R.id.btn_secret);
		secretBtn.setOnClickListener(this);

		if (mMainHelper.showSendReportPhoneDlg(this)) {
			;
		} else {
			initListView();
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public void initListView() {
		WatchService.actionReschedule(this);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Uri uri = Uri.parse("content://sms/inbox");
				SmsContent sc = new SmsContent(MainActivity.this, uri);
				List<MessageItem> itemsTemp = sc.getSmsInfo();
				if (itemsTemp != null) {
					for (MessageItem item : itemsTemp) {
						if (SMSHandler.filterMessage(item)) {
							System.out.println("GGGGGGGGG");
							DBTool.getInstance().saveMessage(MainActivity.this,
									item);
						}
					}
				}

				List<MessageItem> items = DBTool.getInstance().getSavedMessage(
						getApplicationContext(), null,
						// UserSession.getPhone(getApplicationContext())
						null);
				adapter.setData(items);
				mHandler.sendEmptyMessage(0);
			}

		}).start();
	}

	class MyAdapter extends BaseAdapter {
		private List<MessageItem> items = new ArrayList<MessageItem>();

		public void setData(List<MessageItem> tasks) {
			this.items.clear();
			if (tasks != null) {
				this.items.addAll(tasks);
				tasks.clear();
				tasks = null;
			}
		}

		public List<MessageItem> getItems() {
			return items;
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
					temp += ("##: " + item.getPhone() + ":  "
							+ item.getChildItems().size() + "\r\n"
							+ getItemBody(item) + "\r\n");
				}
			}
			return temp;
		}

		private String getItemBody(MessageItem item) {
			StringBuffer sBuffer = new StringBuffer();
			int size = item.getChildItems().size();
			for (int i = size; i > 0; i--) {
				sBuffer.append(i + " > ")
						.append(item.getChildItems().get(size - i))
						.append("\r\n");

			}
			return sBuffer.toString();
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
			if (item.getChildItems() != null)
				holder.text3.setText(item.getChildItems().size() + "");
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
				SimpleDateFormat tempDate = new SimpleDateFormat("yyyyMM");
				String datetime = tempDate.format(new java.util.Date());
				String fileName = "易览通汇总" + "-" + datetime + ".txt";
				String filePath = NormalUtil.getRootDir();
				if (FileUtils.checkFileExist(filePath + fileName)) {
					FileUtils.deleteFile(filePath + fileName);
				}
				FileUtils.saveToSDCardOrRAM(this, fileName,
						adapter.getOutPutMessage(), filePath);
				sendMessage();
				Intent intent = new Intent(this, OkAct.class);
				startActivity(intent);

				// 云端备份
				if (TextUtils.isEmpty(UserSession
						.getMyPhone(getApplicationContext()))) {
					showSetMyPhoneDlg();
				} else {
					netSaveToCloud(this);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.btn_restart:
			WatchService.actionReschedule(this);
			if (TextUtils.isEmpty(UserSession
					.getMyPhone(getApplicationContext()))) {
				showSetMyPhoneDlg();
			} else {
				getInternet(this);
			}
			break;
		case R.id.btn_clean:
			final boolean isExit = isSystemMsgExit();
			View view = getLayoutInflater().inflate(R.layout.dlg_delete, null);
			final Button button = (Button) view.findViewById(R.id.button);
			final Button btnCancle = (Button) view
					.findViewById(R.id.button_cancle);
			TextView textTip = (TextView) view.findViewById(R.id.text_tip);
			if (isExit) {
				textTip.setText("清空数据之前需先删除短信里的相关数据！");
			} else {
				textTip.setText("清空列表数据前确定已把数据发送导出？");
			}

			btnCancle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					deleteDialog.dismiss();
				}
			});
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (isExit) {

					} else {
						DBTool.getInstance().deleteAll(MainActivity.this);
						NormalUtil.deletePath();
						initListView();
					}
					deleteDialog.dismiss();
				}
			});
			deleteDialog = new AlertDialog.Builder(this).setTitle("删除提示")
					.setView(view).create();
			deleteDialog.setCanceledOnTouchOutside(true);
			deleteDialog.show();
			break;

		case R.id.btn_acquire:
			if (TextUtils.isEmpty(UserSession
					.getMyPhone(getApplicationContext()))) {
				showSetMyPhoneDlg();
			} else {
				getInternet(this);
			}
			break;
		case R.id.btn_secret:
			showSecret();
			break;
		}
	}

	private void sendMessage() {
		List<MessageItem> messages = adapter.getItems();
		if (messages == null)
			return;

		List<String> items = new ArrayList<String>();
		for (MessageItem message : messages) {
			items.add(message.getPhone() + "  "
					+ message.getChildItems().size());
		}
		List<String> sendInfo = new ArrayList<String>();
		int size = items.size();
		int length = 6;
		int aaa = size / length + 1;
		int hhh = size % length;
		for (int i = 0; i < aaa; i++) {
			int ccc;
			if (i == aaa - 1) {
				ccc = hhh;
			} else {
				ccc = length;
			}
			String temp = "*";
			for (int j = 0; j < ccc; j++) {
				temp += (items.get(i * length + j) + "*");
			}
			sendInfo.add(temp);
		}
		for (String info : sendInfo) {
			MessageSender.getInstance().sendSms(
					UserSession.getPhoneNo(getApplicationContext()), info);
			System.out.println("LLL:" + info);
		}
	}

	private boolean isSystemMsgExit() {
		Uri uri = Uri.parse("content://sms/inbox");
		SmsContent sc = new SmsContent(MainActivity.this, uri);
		List<MessageItem> itemsTemp = sc.getSmsInfo();
		if (itemsTemp != null) {
			for (MessageItem item : itemsTemp) {
				if (SMSHandler.filterMessage(item)) {
					return true;
				}
			}
		}
		return false;
	}

	private void getInternet(Context context) {
		showWaitDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				HttpUtil.SERVER_ADDRESS + "app/comment/list.do",
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						closeWaitDialog();
						Log.e("TAG", response);
						NormalUtil.displayMessage(getApplicationContext(),
								"获取数据成功");
						if (!TextUtils.isEmpty(response)) {
							dealNetGetBean(response);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						closeWaitDialog();
						Log.e("TAG", error.getMessage(), error);
						NormalUtil.displayMessage(getApplicationContext(),
								"获取数据失败，请检查网络");
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				// 在这里设置需要post的参数
				Map<String, String> map = new HashMap<String, String>();
				map.put("recivephone",
						UserSession.getMyPhone(getApplicationContext()));
				return map;
			}
		};
		HttpUtil.getInstance().addRequest(stringRequest, context);
	}

	private Dialog dialog;

	private void showSetMyPhoneDlg() {
		if (dialog == null) {
			View view = getLayoutInflater()
					.inflate(R.layout.set_my_phone, null);
			final EditText text1 = (EditText) view.findViewById(R.id.text1);
			final EditText text2 = (EditText) view.findViewById(R.id.text2);
			final Button button = (Button) view.findViewById(R.id.button);
			final Button btnCancle = (Button) view
					.findViewById(R.id.button_cancle);

			btnCancle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String phoneNo1 = text1.getEditableText().toString();
					String phoneNo2 = text2.getEditableText().toString();
					if (TextUtils.isEmpty(phoneNo1) || phoneNo1.length() != 11) {
						NormalUtil.displayMessage(getApplicationContext(),
								"输入的非手机号码！");
						dialog.show();
						return;
					}

					if (TextUtils.isEmpty(phoneNo2) || phoneNo2.length() != 11) {
						NormalUtil.displayMessage(getApplicationContext(),
								"重新输入的非手机号码！");
						dialog.show();
						return;
					}

					if (!TextUtils.equals(phoneNo1, phoneNo2)) {
						NormalUtil.displayMessage(getApplicationContext(),
								"请两次输入相同的手机号码！");
						dialog.show();
						return;
					}
					UserSession.setMyPhone(getApplicationContext(), phoneNo1);

					dialog.dismiss();
				}
			});
			dialog = new AlertDialog.Builder(MainActivity.this)
					.setTitle("本机的手机号码").setView(view).create();
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		} else
			dialog.show();

	}

	private Dialog waitDialog;

	private void showWaitDialog() {
		if (waitDialog == null) {
			View view = getLayoutInflater().inflate(
					R.layout.common_dialog_loading_layout, null);
			waitDialog = new AlertDialog.Builder(MainActivity.this).setView(
					view).create();
			waitDialog.setCanceledOnTouchOutside(false);
			waitDialog.show();
		} else
			waitDialog.show();
	}

	private void closeWaitDialog() {
		if (waitDialog != null && waitDialog.isShowing())
			waitDialog.dismiss();
	}

	private void dealNetGetBean(String response) {
		GetNetMessageBean netMessageBean = JsonUtil.jsonToObject(response,
				GetNetMessageBean.class);
		if (netMessageBean.getResult() != null
				&& !netMessageBean.getResult().isEmpty()) {
			for (ResultBean resultBean : netMessageBean.getResult()) {
				MessageItem messageItem = new MessageItem();
				messageItem.setPhone(resultBean.getPhone());
				messageItem.setDate(UserSession.getDataStrFromTimeMillis(
				// resultBean.getComdate(),
						System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
				ArrayList<String> childItems = new ArrayList<String>();
				if (resultBean.getContents() != null
						&& !resultBean.getContents().isEmpty())
					for (ContentsBean contentsBean : resultBean.getContents()) {
						childItems.add(contentsBean.getDate() + " "
								+ contentsBean.getTag());
					}
				messageItem.setChildItems(childItems);
				DBTool.getInstance()
						.saveMessage(MainActivity.this, messageItem);
			}
		}
		List<MessageItem> items = DBTool.getInstance().getSavedMessage(
				getApplicationContext(), null,
				// UserSession.getPhone(getApplicationContext())
				null);
		adapter.setData(items);
		adapter.notifyDataSetChanged();
		mHandler.sendEmptyMessage(0);
	}

	private void netSaveToCloud(Context context) {
		List<MessageItem> messages = adapter.getItems();
		if (messages == null)
			return;
		List<SendBean> sendBeans = new ArrayList<SendBean>();
		for (MessageItem message : messages) {
			SendBean sendBean = new SendBean();
			sendBean.setComdate(NormalUtil.stringToLong(message.getDate(),
					"yyyy-MM-dd HH:mm:ss") + "");
			sendBean.setPhone(message.getPhone());
			sendBean.setRecivephone(UserSession
					.getMyPhone(getApplicationContext()));
			ArrayList<ContentItem> contents = new ArrayList<ContentItem>();
			if (message.getChildItems() != null
					&& message.getChildItems().size() > 0) {
				for (String childItem : message.getChildItems()) {
					ContentItem contentItem = new ContentItem();
					contentItem.setDate(childItem.substring(0, 19));
					contentItem.setTag(childItem.substring(20));
					contents.add(contentItem);
					System.out.println(childItem);
				}
			}
			sendBean.setContents(contents);
			sendBeans.add(sendBean);
		}
		final String sendData = JsonUtil.objectToJson(sendBeans);
		System.out.println(sendData);
		showWaitDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				HttpUtil.SERVER_ADDRESS + "app/comment/add2.do",
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						closeWaitDialog();
						Log.e("TAG", response);
						NormalUtil.displayMessage(getApplicationContext(),
								"备份数据成功");
						if (!TextUtils.isEmpty(response)) {
							dealNetGetBean(response);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						closeWaitDialog();
						Log.e("TAG", error.getMessage(), error);
						NormalUtil.displayMessage(getApplicationContext(),
								"备份数据失败，请检查网络");
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				// 在这里设置需要post的参数
				Map<String, String> map = new HashMap<String, String>();
				map.put("data", sendData);
				return map;
			}
		};
		HttpUtil.getInstance().addRequest(stringRequest, context);
	}

	Dialog dialogS = null;

	private void showSecret() {
		View viewS = getLayoutInflater()
				.inflate(R.layout.show_secret_dlg, null);
		Button button = (Button) viewS.findViewById(R.id.button);
		Button btnCancle = (Button) viewS.findViewById(R.id.button_cancle);
		TextView text1 = (TextView) viewS.findViewById(R.id.text1);
		text1.setText(SecretKeyTool.getSecret());
		btnCancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogS.dismiss();
			}
		});
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				dialogS.dismiss();
			}
		});
		dialogS = new AlertDialog.Builder(MainActivity.this)
				.setTitle("秘钥").setView(viewS).create();
		dialogS.setCanceledOnTouchOutside(false);
		dialogS.show();
	}

}
