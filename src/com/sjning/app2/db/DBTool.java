package com.sjning.app2.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.sjning.app2.receive.MessageItem;
import com.sjning.app2.tools.NormalUtil;

public class DBTool {
	private static DBTool dbTool;

	private DBTool() {

	}

	public static DBTool getInstance() {
		if (dbTool == null)
			dbTool = new DBTool();
		return dbTool;

	}

	public synchronized List<MessageItem> getSavedMessage(Context context,
			String searchStr, String phone) {
		List<MessageItem> list = null;

		SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();

		String sql = "SELECT * FROM table_sms WHERE date(dateTime) > strftime( '%Y-%m-%d %H:%M:%S', date('now', '-1 month')) ";
		if (searchStr != null) {
			sql += "and (body like '%" + searchStr + "%') ";
		}
		if (phone != null && !phone.equals(""))
			sql += "and phone = " + phone;
		sql += " ORDER BY dateTime DESC";
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				if (list == null) {
					list = new ArrayList<MessageItem>();
				}
				MessageItem message = new MessageItem();
				message.setDate(cursor.getString(cursor
						.getColumnIndex("dateTime")));
				message.setPhone(cursor.getString(cursor
						.getColumnIndex("phone")));
				list.add(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				getMessageChild(context, list.get(i));
			}
		}

		return list;
	}

	public void getMessageChild(Context context, MessageItem item) {
		List<String> childItems = null;
		SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
		String sql = "SELECT * FROM table_sms_child WHERE date(dateTime) > strftime( '%Y-%m-%d %H:%M:%S', date('now', '-1 month')) ";
		// sql += "and phone = " + item.getPhone();
		sql += " ORDER BY dateTime DESC";
		System.out.println(sql);
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				if (childItems == null) {
					childItems = new ArrayList<String>();
				}
				String phone = cursor.getString(cursor.getColumnIndex("phone"));
				if (TextUtils.equals(item.getPhone(), phone))
					childItems.add(cursor.getString(cursor
							.getColumnIndex("childItem")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
		if (childItems != null && childItems.size() > 0) {
			List<Date> dates = new ArrayList<Date>();
			for (String childItem : childItems) {
				try {
					Date date = NormalUtil.stringToDate(childItem,
							"yyyy-MM-dd HH:mm:ss");
					dates.add(date);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Collections.sort(dates);
			childItems.clear();
			for (int i = dates.size() - 1; i >= 0; i--) {
				SimpleDateFormat tempDate = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String datetime = tempDate.format(dates.get(i));
				childItems.add(datetime);
			}
		}

		item.setChildItems(childItems);
	}

	public synchronized void saveMessage(Context context, MessageItem item) {
		if (item == null)
			return;
		SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.clear();
		contentValues.put("dateTime", item.getDate());
		contentValues.put("phone", item.getPhone());
		db.replace(DatabaseHelper.TABLE_SMS, null, contentValues);

		if (item.getChildItems() != null) {
			for (String childItem : item.getChildItems()) {
				contentValues.clear();
				contentValues.put("dateTime", item.getDate());
				contentValues.put("phone", item.getPhone());
				contentValues.put("childItem", childItem);
				db.replace(DatabaseHelper.TABLE_SMS_CHILD, null, contentValues);
			}

		}

		db.close();
	}

	public void deleteAll(Context context) {
		SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
		db.delete(DatabaseHelper.TABLE_SMS, null, null);
		db.delete(DatabaseHelper.TABLE_SMS_CHILD, null, null);
	}
}
