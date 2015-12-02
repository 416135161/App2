package com.sjning.app2.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {
	private static final String KEY = "phone";
	private static final String SPNAME = "temp";

	public static String getPhone(Context context) {
		return context.getSharedPreferences(SPNAME, Context.MODE_PRIVATE)
				.getString(KEY, "15802863359");
	}

	public static void setPhone(Context context, String phone) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				SPNAME, Context.MODE_PRIVATE);
		sharedPreferences.edit().putString(KEY, phone).commit();
	}
	public static String getDataStrFromTimeMillis(long milltime, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.format(new Date(milltime));
	}
}
