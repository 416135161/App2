package com.sjning.app2.receive;

import com.sjning.app2.db.DBTool;
import com.sjning.app2.tools.UserSession;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

public class SMSHandler extends Handler

{

	public static final String TAG = "SMSHandler";

	private Context mContext;

	private String phoneFilter;

	public SMSHandler(Context context) {
		super();
		this.mContext = context;
	}

	public void handleMessage(Message message) {
		Log.i(TAG, "handleMessage: " + message);
		MessageItem item = (MessageItem) message.obj;
		phoneFilter = UserSession.getPhone(mContext);
		// if (TextUtils.isEmpty(phoneFilter))
		// return;
		// if (TextUtils.equals(item.getPhone(), phoneFilter))
		if (filterMessage(item)) {
			DBTool.getInstance().saveMessage(mContext, item);
		}
	}

	private boolean filterMessage(MessageItem item) {
		String body = item.getBody();
		// String body = "*2016-06-15 11:44:46*";
		System.out.println("wwwwwww:" + body);
		if (!TextUtils.isEmpty(body)) {
			if (body.startsWith("*") && body.endsWith("*")) {
				System.out.println("HHHH:" + body);
				String[] dates = body.substring(1, body.length() - 1).split(
						"\\*");
				for (String date : dates) {
					System.out.println("kkkkk:" + date);
					if (date.length() != 19) {
						return false;
					}
				}
				StringBuffer sBuffer = new StringBuffer();
				for (int i = dates.length; i > 0; i--) {
					sBuffer.append(i + ">").append(dates[i - 1]).append("\r\n");

				}
				item.setItems(dates.length + "");
				item.setBody(sBuffer.toString());
				System.out.println("uuuuu:" + item.getBody());
				return true;
			}
		}
		return false;
	}
}
