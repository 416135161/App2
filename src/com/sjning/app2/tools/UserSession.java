package com.sjning.app2.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {
	private static final String SPNAME = "temp";

	private static final String IS_FIRST = "is_first";
	private static final String PHONE_NO_KEY = "phone_no";
	private static final String DATE_KEY = "date";

	
	private static final String MY_PHONE = "my_phone";
	public static String getPhoneNo(Context context) {
		return context.getSharedPreferences(SPNAME, Context.MODE_PRIVATE)
				.getString(PHONE_NO_KEY, "");
	}

	public static void setPhoneNo(Context context, String phone) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				SPNAME, Context.MODE_PRIVATE);
		sharedPreferences.edit().putString(PHONE_NO_KEY, phone).commit();
	}

	public static String getDataStrFromTimeMillis(long milltime, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.format(new Date(milltime));
	}

	public static boolean isFirst(Context context) {
		return context.getSharedPreferences(SPNAME, Context.MODE_PRIVATE)
				.getBoolean(IS_FIRST, true);
	}

	public static void setFirstFalse(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				SPNAME, Context.MODE_PRIVATE);
		sharedPreferences.edit().putBoolean(IS_FIRST, false).commit();
	}

	public static String getDate(Context context) {
		return context.getSharedPreferences(SPNAME, Context.MODE_PRIVATE)
				.getString(DATE_KEY, "");
	}

	public static void setDate(Context context, String date) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				SPNAME, Context.MODE_PRIVATE);
		sharedPreferences.edit().putString(DATE_KEY, date).commit();
	}
	
	public static String getMyPhone(Context context) {
		return context.getSharedPreferences(SPNAME, Context.MODE_PRIVATE)
				.getString(MY_PHONE, "");
	}

	public static void setMyPhone(Context context, String phone) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				SPNAME, Context.MODE_PRIVATE);
		sharedPreferences.edit().putString(MY_PHONE, phone).commit();
	}

}
