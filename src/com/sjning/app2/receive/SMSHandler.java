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
		MessageItem item = (MessageItem)message.obj;
		phoneFilter = UserSession.getPhone(mContext);
//		if (TextUtils.isEmpty(phoneFilter))
//			return;
//		if (TextUtils.equals(item.getPhone(), phoneFilter))
			DBTool.getInstance().saveMessage(mContext, item);
		
	}
}
