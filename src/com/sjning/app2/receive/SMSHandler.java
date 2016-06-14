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
		if(filterMessage(item)){
			DBTool.getInstance().saveMessage(mContext, item);
		}
	}
	
	
	private boolean filterMessage(MessageItem item){
		String body = item.getBody();
		System.out.println("wwwwwww:" + body);
		if(!TextUtils.isEmpty(body)){
			if( body.startsWith("*") && body.endsWith("*")){
				System.out.println("HHHH:" + body);
				String newBody = body.substring(1, body.length() - 1);
				System.out.println("ggggg:" + newBody);
				String [] dates = newBody.split("\\*");
				for(String date : dates){
					System.out.println("kkkkk:" + date);
					if(date.length() != 19){
						return false;
					}
				}
				item.setItems(dates.length);
				item.setBody(newBody.replace("*", "\n"));
				System.out.println("uuuuu:" + item.getBody());
				return true;
			}
		}
		return false;
	}
}
